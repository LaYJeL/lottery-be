package com.game.lottery.e2e;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@Tag("e2e")
public class BackendE2ETest {

    @Test
    void backendShouldBeUpAndRunning() {
        RestAssured.baseURI = "http://localhost:8088";

        // Check Swagger UI availability as a sign of life
        given()
                .when()
                .get("/swagger-ui/index.html")
                .then()
                .statusCode(200);
    }
}
