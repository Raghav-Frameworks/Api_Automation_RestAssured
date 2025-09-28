package com.framework.core;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.File;
import java.util.Map;

public class ApiClient {

    private final RequestSpecification base;

    public ApiClient() {
        this.base = RequestSpecFactory.build();
    }

    public Response get(String path,
                        Map<String, ?> pathParams,
                        Map<String, ?> queryParams,
                        Map<String, String> headers) {
        return prepare(pathParams, queryParams, headers)
                .when().get(path).andReturn();
    }

    public Response post(String path, Object body,
                         Map<String, ?> pathParams,
                         Map<String, ?> queryParams,
                         Map<String, String> headers) {
        return prepare(pathParams, queryParams, headers)
                .contentType(ContentType.JSON)
                .body(body)
                .when().post(path).andReturn();
    }

    public Response put(String path, Object body,
                        Map<String, ?> pathParams,
                        Map<String, ?> queryParams,
                        Map<String, String> headers) {
        return prepare(pathParams, queryParams, headers)
                .contentType(ContentType.JSON)
                .body(body)
                .when().put(path).andReturn();
    }

    public Response patch(String path, Object body,
                          Map<String, ?> pathParams,
                          Map<String, ?> queryParams,
                          Map<String, String> headers) {
        return prepare(pathParams, queryParams, headers)
                .contentType(ContentType.JSON)
                .body(body)
                .when().patch(path).andReturn();
    }

    public Response delete(String path,
                           Map<String, ?> pathParams,
                           Map<String, ?> queryParams,
                           Map<String, String> headers) {
        return prepare(pathParams, queryParams, headers)
                .when().delete(path).andReturn();
    }

    public Response head(String path,
                         Map<String, ?> pathParams,
                         Map<String, ?> queryParams,
                         Map<String, String> headers) {
        return prepare(pathParams, queryParams, headers)
                .when().head(path).andReturn();
    }

    public Response options(String path,
                            Map<String, ?> pathParams,
                            Map<String, ?> queryParams,
                            Map<String, String> headers) {
        return prepare(pathParams, queryParams, headers)
                .when().options(path).andReturn();
    }

    public Response uploadMultipart(String path, String partName, File file,
                                    Map<String, ?> pathParams,
                                    Map<String, ?> queryParams,
                                    Map<String, String> headers) {
        return prepare(pathParams, queryParams, headers)
                .multiPart(partName, file)
                .when().post(path).andReturn();
    }

    public Response download(String path,
                             Map<String, ?> pathParams,
                             Map<String, ?> queryParams,
                             Map<String, String> headers) {
        return prepare(pathParams, queryParams, headers)
                .when().get(path).andReturn();
    }

    private RequestSpecification prepare(Map<String, ?> pathParams,
                                         Map<String, ?> queryParams,
                                         Map<String, String> headers) {
        RequestSpecification spec = base;
        if (pathParams != null) spec = spec.pathParams(pathParams);
        if (queryParams != null) spec = spec.queryParams(queryParams);
        if (headers != null) spec = spec.headers(headers);
        return spec;
    }
}
