package com.framework.auth;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static io.restassured.RestAssured.given;

/**
 * OAuth2 Client Credentials provider with in-memory cache.
 * Assumes token endpoint returns:
 *  { "access_token": "...", "expires_in": 3600, "token_type": "Bearer" }
 */
public class OAuth2ClientCredentialsStrategy implements AuthStrategy {
    private static final Logger log = LoggerFactory.getLogger(OAuth2ClientCredentialsStrategy.class);

    private final String tokenUrl;
    private final String clientId;
    private final String clientSecret;
    private final String scope;
    private final String audience;

    private final AtomicReference<Token> cache = new AtomicReference<>();

    public OAuth2ClientCredentialsStrategy(String tokenUrl, String clientId, String clientSecret,
                                           String scope, String audience) {
        this.tokenUrl = tokenUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
        this.audience = audience;
    }

    @Override
    public void apply(RequestSpecification spec) {
        String token = getToken();
        spec.auth().oauth2(token);
    }

    private String getToken() {
        Token tok = cache.get();
        if (tok != null && tok.expiresAt.isAfter(Instant.now().plusSeconds(30))) {
            return tok.accessToken;
        }

        log.info("Fetching new OAuth2 token from {}", tokenUrl);
        Map<String, String> form = new HashMap<>();
        form.put("grant_type", "client_credentials");
        if (scope != null && !scope.isBlank()) form.put("scope", scope);
        if (audience != null && !audience.isBlank()) form.put("audience", audience);

        Response resp = given()
                .relaxedHTTPSValidation()
                .auth().preemptive().basic(clientId, clientSecret)
                .contentType(ContentType.URLENC)
                .formParams(form)
                .post(tokenUrl)
                .then().statusCode(200)
                .extract().response();

        String accessToken = resp.jsonPath().getString("access_token");
        int expiresIn = resp.jsonPath().getInt("expires_in");

        Token newToken = new Token(accessToken, Instant.now().plusSeconds(Math.max(60, expiresIn)));
        cache.set(newToken);

        return newToken.accessToken;
    }

    private record Token(String accessToken, Instant expiresAt) {}
}
