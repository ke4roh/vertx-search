package com.redhat.vertx.search.filter;

import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.lib.filter.Filter;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class SynonymFilterTest {
    @Test
    public void testPreFilter() {
        String test = "+(product:\"Red Hat Enterprize Linux\") Washington spent the winter {here} with a RHel 7 system and all he needed was a good [wi-fi] connection -test:RHEL-7";
        String expected = "+(product:\"Red Hat Enterprize Linux\") Washington spent the winter {here} with a  rhel7  system and all he needed was a good [ wifi ] connection -test: rhel7";

        Response response = given()
                .when().body(new JsonObject().put("q",test).toString())
                .header("Content-type","application/json")
                .post("/preFilterTest.json")
                .then()
                .statusCode(200).contentType(ContentType.JSON).extract().response();
        Map<String,String> map = response.jsonPath().get();
        assertThat(map.get("__uuid__")).isNotBlank();
        assertThat(map.get("filtered")).isEqualTo(expected);
    }

    @Test
    public void testSynonymReplacement() {
        Filter f = new SynonymFilter();
        JinjavaConfig.Builder builder = JinjavaConfig.newBuilder();
        builder.withFailOnUnknownTokens(true);
        JinjavaConfig config = builder.build();
        Jinjava jinjava = new Jinjava(config);
        jinjava.getGlobalContext().registerFilter(new SynonymFilter());

        Map<String, Object> context = new HashMap<String, Object>();
        Map<String, Object> synonyms = new HashMap<String, Object>();
        synonyms.put("men","people");
        synonyms.put("podiatrists", "country");

        context.put("synonyms",synonyms);

        String result = jinjava.render("{{\"Now is the time for all good men to come to the aid of their podiatrists.\" | replace_synonyms(synonyms) }}", context);

        assertThat(result).isEqualTo("Now is the time for all good people to come to the aid of their country.");
    }
}
