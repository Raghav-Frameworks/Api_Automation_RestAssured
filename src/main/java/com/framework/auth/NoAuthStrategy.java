package com.framework.auth;

import io.restassured.specification.RequestSpecification;

public class NoAuthStrategy implements AuthStrategy {
    @Override
    public void apply(RequestSpecification spec) {
        // no authentication applied
    }
}
