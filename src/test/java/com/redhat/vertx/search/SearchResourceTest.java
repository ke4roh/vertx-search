package com.redhat.vertx.search;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class SearchResourceTest {

    @Test
    public void testHelloEndpoint() {
        Response response = given()
                .when().body("{\"name\":\"Jason\" }")
                .header("Content-type","application/json")
                .post("/helloPipeline.json")
                .then()
                .statusCode(200).contentType(ContentType.JSON).extract().response();
        Map<String,String> map = response.jsonPath().get();
        assertThat(map.get("__uuid__")).isNotBlank();
        assertThat(map.get("name")).isEqualTo("Jason");
        assertThat(map.get("greetings")).isEqualTo("Hello, Jason");
    }

}