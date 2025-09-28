package com.framework.util;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

public class SchemaValidator {

    public static void assertBodyMatchesSchema(Response response, String schemaOnClasspath) {
        response.then().assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(schemaOnClasspath));
    }
}
