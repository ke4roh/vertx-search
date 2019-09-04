package com.redhat.vertx.search.step;

import io.vertx.core.json.JsonObject;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TestSolrClient {

    @Test
    public void testGenerateUrl() throws Exception {
        SolrClient client = new SolrClient();
        JsonObject env = new JsonObject();
        env.put("dehydration","false");
        env.put("rows",3);
        env.put("q","Jason");
        env.put("host_url","http://localhost:8080/");
        env.put("path","/solr/access/select");

        URL url = client.generateUrl(env);
        assertThat(url.getHost()).isEqualTo("localhost");

        Map<String,String> params = new HashMap<>();
        URLEncodedUtils.parse(url.toURI(), Charset.forName("UTF-8")).iterator().forEachRemaining(nvp -> params.put(nvp.getName(),nvp.getValue()));
        assertThat(params.size()).isEqualTo(3);
        assertThat(params.get("rows")).isEqualTo("3");
        assertThat(params.get("wt")).isEqualTo("json");
        assertThat(params.get("q")).isEqualTo("Jason");
        assertThat(params.containsKey("dehydration")).isFalse();
    }
}
