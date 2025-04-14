package com.devsoft.sexyschreiben.core.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONHelper {

    static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
    }

    private JSONHelper() {
        // private
    }

    public static <T> T mapJsonToObject(Class<T> clazz,
                                        String json) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T mapJsonToObject(T obj,
                                        String json) {
        try {
            return objectMapper.readerForUpdating(obj).readValue(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> mapJsonToObjectList(Class<T> clazz,
                                                  String json) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String mapObjectToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> mapJsonToMap(Class<T> clazz,
                                                  String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String, List<T>> mapJsonToMapList(Class<T> clazz,
                                                            String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> mapJsonToMap(String json) {
        try {
            return objectMapper.readValue(json, HashMap.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
