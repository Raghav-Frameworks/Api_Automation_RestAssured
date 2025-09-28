package com.framework.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Reads a JSON file from classpath and returns it as String.
     */
    public static String readClasspath(String resourcePath) {
        try (InputStream is = JsonUtils.class.getResourceAsStream(resourcePath)) {
            if (is == null) throw new IllegalArgumentException("Resource not found: " + resourcePath);
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource " + resourcePath, e);
        }
    }

    /**
     * Reads a JSON file into Map.
     */
    public static Map<String, Object> readMap(String resourcePath) {
        String json = readClasspath(resourcePath);
        try {
            return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse json as map: " + resourcePath, e);
        }
    }

    /**
     * Reads JSON as a Jackson JsonNode tree.
     */
    public static JsonNode readTree(String resourcePath) {
        String json = readClasspath(resourcePath);
        try {
            return MAPPER.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse json tree: " + resourcePath, e);
        }
    }

    /**
     * Converts object to JSON string.
     */
    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }
}
