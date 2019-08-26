package com.redhat.vertx.search.step;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.redhat.vertx.Engine;
import com.redhat.vertx.pipeline.AbstractStep;
import com.redhat.vertx.pipeline.Step;
import com.redhat.vertx.pipeline.StepDependencyNotMetException;
import io.reactivex.Maybe;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.HttpStatusException;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Step.class)
public class SolrClient extends AbstractStep {
    private static final Set<String> PARAMS = new HashSet<>(Arrays.asList((
            "sow,mm.autoRelax,boost,lowercaseOperators,ps,pf2,ps2,pf3,ps3,stopwords,uf,qf," +
                    "defType,sort,start,rows,fq,fl,debug,explainOther,timeAllowed,segmentTerminateEarly," +
                    "omitHeader,cache,logParamsList,echoParams,q").split("\\,")));
    // wt is not in the above list because it is not specifiable by consumers of this client instance
    private WebClient http;

    @Override
    public void init(Engine engine, JsonObject config) {
        super.init(engine, config);
    }

    protected URL generateUrl(JsonObject env) throws StepDependencyNotMetException, URISyntaxException, MalformedURLException {
        String host_url = env.getString("host_url");
        String path = env.getString("path");
        String q = env.getString("q");

        if (host_url == null || path == null || q == null || host_url.length()<1 || path.length() <1 || q.length() <1 ) {
            throw new StepDependencyNotMetException();
        }

        URIBuilder b = new URIBuilder(host_url);
        b.setPath(path);
        PARAMS.stream().filter(env::containsKey).forEach(param -> {
            Object p = env.getValue(param);
            if (p instanceof JsonArray) {
                for (Object pp : ((JsonArray)p)) {
                    b.addParameter(param,(String)pp);
                }
            } else {
                b.addParameter(param,(String)p);
            }
        });
        b.setParameter("wt","json");
        return b.build().toURL();
    }

    @Override
    protected Maybe<Object> executeSlow(JsonObject env) {
        if (http == null) {
            http = WebClient.create(engine.getRxVertx());
        }

        URL url;
        try {
            url = generateUrl(env);
        } catch (StepDependencyNotMetException | URISyntaxException | MalformedURLException e) {
            return Maybe.error(e);
        }

        logger.info(url.toString());

        return Maybe.create( step ->
                addDisposable(env.getJsonObject("doc").getString(Engine.DOC_UUID),
                        http.getAbs(url.toString())
                                .as(BodyCodec.jsonObject())
                                .rxSend()
                                .subscribe(response -> {
                                            if (response.statusCode() == 200) {
                                                step.onSuccess(response.body());
                                            } else {
                                                // TODO make a new exception - this is a weird dependency
                                                step.onError(new HttpStatusException(response.bodyAsString(),response.statusCode(),url.toString()));
                                            }

                                        },
                                        step::onError
                                )
                )
        );
    }
}
