package com.redhat.vertx.search.step;

import com.redhat.vertx.Engine;
import com.redhat.vertx.pipeline.AbstractStep;
import com.redhat.vertx.pipeline.StepDependencyNotMetException;
import io.reactivex.Maybe;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.HttpStatusException;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SolrClient extends AbstractStep {
    private static final Set<String> PARAMS = new HashSet<>(Arrays.asList((
            "sow,mm.autoRelax,boost,lowercaseOperators,ps,pf2,ps2,pf3,ps3,stopwords,uf,qf," +
                    "defType,sort,start,rows,fq,fl,debug,explainOther,timeAllowed,segmentTerminateEarly," +
                    "omitHeader,wt,cache,logParamsList,echoParams,q").split("\\,")));
    private WebClient http;

    @Override
    public void init(Engine engine, JsonObject config) {
        super.init(engine, config);
        http = WebClient.create(engine.getVertRx());
    }

    @Override
    protected Maybe<Object> executeSlow(JsonObject env) {
        String host_url = env.getString("host_url");
        String path = env.getString("path");
        String q = env.getString("q");

        if (host_url == null || path == null || q == null || host_url.length()<1 || path.length() <1 || q.length() <1 ) {
            return Maybe.error(new StepDependencyNotMetException());
        }

        URIBuilder b = null;
        final URL url;
        try {
            b = new URIBuilder(host_url);
            b.setPath(path);
            for (String param: PARAMS.stream().filter(env::containsKey).collect(Collectors.toSet())) {
                Object p = env.getValue(param);
                if (p instanceof JsonArray) {
                    for (Object pp : ((JsonArray)p)) {
                        b.addParameter(param,(String)pp);
                    }
                } else {
                    b.addParameter(param,(String)p);
                }
            }
            b.setParameter("wt","json");
            b.setParameter("indent","on");
            url = b.build().toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            return Maybe.error(e);
        }

        logger.info(url.toString());

        return Maybe.create( step ->
                addDisposable(env.getJsonObject("doc").getString(Engine.DOC_UUID),
                        http.getAbs(url.toString())
                                .putHeader("Content-type","application/json")
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
                                        err -> {
                                            step.onError(err);
                                        })
                )
        );
    }
}
