package com.framework.core;

import com.framework.auth.*;
import com.framework.config.ConfigLoader;
import com.framework.config.Environment;
import com.framework.core.filters.CorrelationIdFilter;
import com.framework.core.filters.RetryFilter;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class RequestSpecFactory {

    public static RequestSpecification build() {
        Environment cfg = ConfigLoader.load();

        RestAssuredConfig raConfig = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", cfg.getTimeoutMs())
                        .setParam("http.socket.timeout", cfg.getTimeoutMs())
                        .setParam("http.connection-manager.timeout", (long) cfg.getTimeoutMs())
                );

        RequestSpecification spec = given()
                .config(raConfig)
                .baseUri(cfg.getBaseUri())
                .basePath(cfg.getBasePath() == null ? "" : cfg.getBasePath())
                .accept(ContentType.JSON)
                .filters(
                        new AllureRestAssured(),
                        new CorrelationIdFilter(),
                        new RetryFilter(3, 200)
                );

        // Console logging
        spec.filter(new RequestLoggingFilter(LogDetail.METHOD));
        spec.filter(new RequestLoggingFilter(LogDetail.URI));
        spec.filter(new ResponseLoggingFilter(LogDetail.STATUS));

        // Default headers & query params from config
        Map<String, String> headers = cfg.getDefaultHeaders();
        if (headers != null) headers.forEach(spec::header);

        Map<String, String> q = cfg.getDefaultQueryParams();
        if (q != null) q.forEach(spec::queryParam);

        // Apply auth
        applyAuth(spec, cfg.getAuth());

        return spec;
    }

    private static void applyAuth(RequestSpecification spec, Environment.AuthConfig auth) {
        if (auth == null || auth.getType() == null) {
            new NoAuthStrategy().apply(spec);
            return;
        }
        switch (auth.getType().toLowerCase()) {
            case "basic" -> new BasicAuthStrategy(auth.getUsername(), auth.getPassword()).apply(spec);
            case "bearer" -> new BearerTokenStrategy(auth.getToken()).apply(spec);
            case "oauth2" -> {
                Environment.OAuth2Config o = auth.getOauth2();
                new OAuth2ClientCredentialsStrategy(
                        o.getTokenUrl(), o.getClientId(), o.getClientSecret(),
                        o.getScope(), o.getAudience()
                ).apply(spec);
            }
            default -> new NoAuthStrategy().apply(spec);
        }
    }
}
