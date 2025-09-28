package com.framework.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ConfigLoader {
    private static final Logger log = LoggerFactory.getLogger(ConfigLoader.class);
    private static final String ENV_KEY = "ENV"; // system property or env variable
    private static Environment cached;

    public static Environment load() {
        if (cached != null) return cached;

        // Try system property ENV, else env variable, else default "dev"
        String env = System.getProperty(ENV_KEY, System.getenv().getOrDefault(ENV_KEY, "dev"));
        String resource = "/config/" + env + ".yaml";

        log.info("Loading environment config from {}", resource);
        try (InputStream is = ConfigLoader.class.getResourceAsStream(resource)) {
            Objects.requireNonNull(is, "Config file not found: " + resource);
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            cached = mapper.readValue(is, Environment.class);
            return cached;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config " + resource, e);
        }
    }
}
