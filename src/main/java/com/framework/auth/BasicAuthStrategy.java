package com.framework.auth;

import io.restassured.specification.RequestSpecification;

public class BasicAuthStrategy implements AuthStrategy {
    private final String username;
    private final String password;

    public BasicAuthStrategy(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void apply(RequestSpecification spec) {
        spec.auth().preemptive().basic(username, password);
    }
}
