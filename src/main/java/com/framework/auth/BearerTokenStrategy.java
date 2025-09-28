package com.framework.auth;

import io.restassured.specification.RequestSpecification;

public class BearerTokenStrategy implements AuthStrategy {
    private final String token;

    public BearerTokenStrategy(String token) {
        this.token = token;
    }

    @Override
    public void apply(RequestSpecification spec) {
        spec.auth().oauth2(token);
    }
}
