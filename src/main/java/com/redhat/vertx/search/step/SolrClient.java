package com.redhat.vertx.search.step;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.redhat.vertx.pipeline.Step;
import com.redhat.vertx.pipeline.steps.HttpClient;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.http.client.utils.URIBuilder;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Step.class)
public class SolrClient extends HttpClient {
    private static final Set<String> PARAMS = new HashSet<>(Arrays.asList((
            "sow,mm.autoRelax,boost,lowercaseOperators,pf,pf2,pf3,ps,ps2,ps3,stopwords,uf,qf," +
                    "df,defType,sort,start,rows,fq,fl,debug,explainOther,timeAllowed,segmentTerminateEarly," +
                    "omitHeader,cache,logParamsList,echoParams,q,q.op,tie,mm,bq,hl.fragsize,req_type" +
            "hl.fl,hl.snippets,enableElevation,hl.simple.post").split("\\,")));
    // TODO are there other parameters for edismax et al that need to be considered? How to make exhaustive list?
    // wt is not in the above list because it is not specifiable by consumers of this client instance

    @Override
    public String getUrl(JsonObject env) {
        try {
            String host_url = env.getString("host_url");
            String path = env.getString("path");
            String q = env.getString("q");

            URIBuilder b = new URIBuilder(host_url);
            b.setPath(path);
            PARAMS.stream().filter(env::containsKey).forEach(param -> {
                Object p = env.getValue(param);
                if (p instanceof JsonArray) {
                    for (Object pp : ((JsonArray) p)) {
                        b.addParameter(param, String.valueOf(pp));
                    }
                } else {
                    b.addParameter(param, String.valueOf(p));
                }
            });
            b.setParameter("wt", "json");
            return b.build().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
