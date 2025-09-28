package com.framework.config;

import java.util.Map;

public class Environment {
    private String baseUri;
    private String basePath;
    private int timeoutMs;
    private AuthConfig auth;
    private Map<String, String> defaultHeaders;
    private Map<String, String> defaultQueryParams;

    public String getBaseUri() { return baseUri; }
    public String getBasePath() { return basePath; }
    public int getTimeoutMs() { return timeoutMs; }
    public AuthConfig getAuth() { return auth; }
    public Map<String, String> getDefaultHeaders() { return defaultHeaders; }
    public Map<String, String> getDefaultQueryParams() { return defaultQueryParams; }

    public static class AuthConfig {
        private String type; // none | basic | bearer | oauth2
        private String username;
        private String password;
        private String token;
        private OAuth2Config oauth2;

        public String getType() { return type; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getToken() { return token; }
        public OAuth2Config getOauth2() { return oauth2; }
    }

    public static class OAuth2Config {
        private String tokenUrl;
        private String clientId;
        private String clientSecret;
        private String scope;
        private String audience;

        public String getTokenUrl() { return tokenUrl; }
        public String getClientId() { return clientId; }
        public String getClientSecret() { return clientSecret; }
        public String getScope() { return scope; }
        public String getAudience() { return audience; }
    }
}
