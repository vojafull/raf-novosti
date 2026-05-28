package org.example.demo.util;

import java.util.HashMap;
import java.util.Map;

public class ServiceResponse {

    public static Map<String, Object> error(String message, int status) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", message);
        response.put("status", status);
        return response;
    }

    public static Map<String, Object> success(String key, Object value) {
        Map<String, Object> response = new HashMap<>();
        response.put(key, value);
        return response;
    }
}
