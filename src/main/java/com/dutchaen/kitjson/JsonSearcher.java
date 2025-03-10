package com.dutchaen.kitjson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;

public class JsonSearcher {

    public static HashMap<String, String> findValue(JsonElement element, String value) {

        var map = new HashMap<String, String>();

        if (element.isJsonArray()) {
            searchArray("", element.getAsJsonArray(), value, map);

        }
        else if (element.isJsonObject()) {
            searchObject("", element.getAsJsonObject(), value, map);
        }

        return map;
    }

    private static void searchArray(String path, JsonArray array, String query, HashMap<String, String> map) {
        for (int i = 0; i < array.size(); i++) {
            JsonElement element = array.get(i);
            String currentPath = path + String.format("[%d]", i);

            if (element.isJsonObject()) {
                searchObject(currentPath, element.getAsJsonObject(), query, map);
            }
            else if (element.isJsonArray()) {
                searchArray(currentPath, element.getAsJsonArray(), query, map);
            }
            else if (element.toString().contains(query)) {
                var typeString = findType(element);
                map.put(currentPath, typeString);
            }
        }
    }

    private static void searchObject(String path, JsonObject object, String query, HashMap<String, String> map) {
        for (var item : object.entrySet()) {
            var key = item.getKey();
            var value = item.getValue();
            String currentPath = path + String.format("['%s']", key);

            if (value.isJsonObject()) {
                searchObject(currentPath, value.getAsJsonObject(), query, map);
            }
            else if (value.isJsonArray()) {
                searchArray(currentPath, value.getAsJsonArray(), query, map);
            }
            else if (value.toString().contains(query)) {
                var typeString = findType(value);
                map.put(currentPath, typeString);
            }
        }
    }

    private static String findType(JsonElement element) {
        if (element.isJsonPrimitive()) {
            var primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return "boolean";
            }
            else if (primitive.isNumber()) {
                return "number";
            }
            else if (primitive.isString()) {
                return "string";
            }
        }
        else if (element.isJsonArray()) {
            return "array<object>";
        }
        else if (element.isJsonObject()) {
            return "map<string,object>";
        }

        return "undefined";
    }
}
