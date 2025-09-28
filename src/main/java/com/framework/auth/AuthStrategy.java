package com.framework.auth;

import io.restassured.specification.RequestSpecification;

public interface AuthStrategy {
    void apply(RequestSpecification spec);
}
