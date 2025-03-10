package com.dutchaen.kitjson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;

public class StructParser {

    public static String parseGo(JsonElement element, String name, StringBuilder builder, ArrayList<String> usedStructs) {

        var values = new HashMap<String, JsonElement>();

        if (builder == null) {
            builder = new StringBuilder();
        }

        if (usedStructs == null) {
            usedStructs = new ArrayList<>();
        }

        builder.append(String.format("\r\n\r\ntype %s struct {\r\n", name));

        if (element.isJsonObject()) {
            var jsonObject = element.getAsJsonObject();
            for (var item : jsonObject.entrySet()) {
                var key = item.getKey();
                var value = item.getValue();
                var pascalCasedKey = convertToPascalCase(key);

                if (isString(value)) {
                    builder.append(String.format("\t%s string `json:\"%s\"`\r\n", pascalCasedKey, key));
                }
                else if (isBoolean(value)) {
                    builder.append(String.format("\t%s bool `json:\"%s\"`\r\n", pascalCasedKey, key));
                }
                else if (isNumber(value)) {
                    builder.append(String.format("\t%s float64 `json:\"%s\"`\r\n", pascalCasedKey, key));
                }
                else if (value.isJsonNull()) {
                    builder.append(String.format("\t%s interface{} `json:\"%s\"`\r\n", pascalCasedKey, key));
                }
                else if (value.isJsonObject()) {
                    values.put(pascalCasedKey, value);
                    builder.append(String.format("\t%s %s `json:\"%s\"`\r\n", pascalCasedKey, pascalCasedKey, key));
                }
                else if (value.isJsonArray()) {
                    golangHandleArray(value, values, key, builder);
                }

            }
        }
        else if (element.isJsonArray()) {
            golangHandleArray(element, values, name, builder);
        }

        builder.append('}');

        for (var item : values.entrySet()) {
            String structName = item.getKey();
            JsonElement nextElement = item.getValue();

            if (usedStructs.contains(structName)) {
                continue;
            }

            parseGo(nextElement, structName, builder, usedStructs);
            usedStructs.add(structName);
        }

        return builder.toString().stripLeading();
    }


    public static String parseRust(JsonElement element, String name, StringBuilder builder, ArrayList<String> usedStructs) {

        var values = new HashMap<String, JsonElement>();

        if (builder == null) {
            builder = new StringBuilder();
        }

        if (usedStructs == null) {
            usedStructs = new ArrayList<>();
        }

        builder.append(String.format("\r\n\r\n#[derive(Serialize, Deserialize)]\r\npub struct %s {\r\n", name));

        if (element.isJsonObject()) {
            var jsonObject = element.getAsJsonObject();
            for (var item : jsonObject.entrySet()) {
                var key = item.getKey();
                var value = item.getValue();
                var pascalCasedKey = convertToPascalCase(key);

                if (isString(value)) {
                    builder.append(String.format("\tpub %s: String,\n", key));
                }
                else if (isBoolean(value)) {
                    builder.append(String.format("\tpub %s: bool,\r\n", key));
                }
                else if (isNumber(value)) {
                    builder.append(String.format("\tpub %s: f64,\r\n", key));
                }
                else if (value.isJsonNull()) {
                    builder.append(String.format("\tpub %s: serde_json::Value,\r\n", key));
                }
                else if (value.isJsonObject()) {
                    values.put(pascalCasedKey, value);
                    builder.append(String.format("\tpub %s: %s,\r\n", pascalCasedKey, key));
                }
                else if (value.isJsonArray()) {
                    rustHandleArray(value, values, key, builder);
                }

            }
        }
        else if (element.isJsonArray()) {
            rustHandleArray(element, values, name, builder);
        }

        builder.append('}');

        for (var item : values.entrySet()) {
            String structName = item.getKey();
            JsonElement nextElement = item.getValue();

            if (usedStructs.contains(structName)) {
                continue;
            }

            parseRust(nextElement, structName, builder, usedStructs);
            usedStructs.add(structName);
        }

        return builder.toString().stripLeading();

    }

    public static String parseCSharp(JsonElement element, String name, StringBuilder builder, ArrayList<String> usedStructs) {
        var values = new HashMap<String, JsonElement>();

        if (builder == null) {
            builder = new StringBuilder();
        }

        if (usedStructs == null) {
            usedStructs = new ArrayList<>();
        }

        builder.append(String.format("\r\n\r\npublic class %s\r\n{\r\n", name));

        if (element.isJsonObject()) {
            var jsonObject = element.getAsJsonObject();
            for (var item : jsonObject.entrySet()) {
                var key = item.getKey();
                var value = item.getValue();
                var pascalCasedKey = convertToPascalCase(key);

                if (isString(value)) {
                    builder.append(String.format("\t[JsonProperty(\"%s\")]\r\n\tpublic string %s { get; set; } \r\n", key, pascalCasedKey));
                }
                else if (isBoolean(value)) {
                    builder.append(String.format("\t[JsonProperty(\"%s\")]\r\n\tpublic bool %s { get; set; } \r\n", key, pascalCasedKey));
                }
                else if (isNumber(value)) {
                    builder.append(String.format("\t[JsonProperty(\"%s\")]\r\n\tpublic double %s { get; set; } \r\n", key, pascalCasedKey));
                }
                else if (value.isJsonNull()) {
                    builder.append(String.format("\t[JsonProperty(\"%s\")]\r\n\tpublic JToken %s { get; set; } \r\n", key, pascalCasedKey));
                }
                else if (value.isJsonObject()) {
                    values.put(pascalCasedKey, value);
                    builder.append(String.format("\t[JsonProperty(\"%s\")]\r\n\tpublic %s %s { get; set; } \r\n", key, pascalCasedKey, pascalCasedKey));
                }
                else if (value.isJsonArray()) {
                    csharpHandleArray(value, values, key, builder);
                }

            }
        }
        else if (element.isJsonArray()) {
            csharpHandleArray(element, values, name, builder);
        }

        builder.append('}');

        for (var item : values.entrySet()) {
            String structName = item.getKey();
            JsonElement nextElement = item.getValue();

            if (usedStructs.contains(structName)) {
                continue;
            }

            parseCSharp(nextElement, structName, builder, usedStructs);
            usedStructs.add(structName);
        }

        return builder.toString().stripLeading();
    }


    private static boolean isString(JsonElement element) {
        if (!element.isJsonPrimitive()) {
            return false;
        }

        var primitive = element.getAsJsonPrimitive();
        return primitive.isString();
    }

    private static boolean isNumber(JsonElement element) {
        if (!element.isJsonPrimitive()) {
            return false;
        }

        var primitive = element.getAsJsonPrimitive();
        return primitive.isNumber();
    }

    private static boolean isBoolean(JsonElement element) {
        if (!element.isJsonPrimitive()) {
            return false;
        }

        var primitive = element.getAsJsonPrimitive();
        return primitive.isBoolean();
    }

    private static String convertToPascalCase(String text) {
        boolean capitalize = false;
        var builder = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '_') {
                capitalize = true;
                continue;
            }

            if (i == 0 || capitalize) {
                builder.append(Character.toUpperCase(c));
                capitalize = false;
                continue;
            }

            builder.append(c);
        }

        return builder.toString();
    }

    private static String golangDetermineArrayType(JsonArray array) {
        var result = "";
        final var defaultArrayType = "[]interface{}";

        for (var item : array) {
            if (isString(item)) {
                if (result.isEmpty()) {
                    result = "[]string";
                } else if (!result.equals("[]string")) {
                    return defaultArrayType;
                }
            }
            else if (isBoolean(item)) {
                if (result.isEmpty()) {
                    result = "[]bool";
                } else if (!result.equals("[]bool")) {
                    return defaultArrayType;
                }
            }
            else if (isNumber(item)) {
                if (result.isEmpty()) {
                    result = "[]float64";
                } else if (!result.equals("[]float64")) {
                    return defaultArrayType;
                }
            }
            else {
                result = defaultArrayType;
                break;
            }
        }

        return result;
    }

    private static String rustDetermineArrayType(JsonArray array) {
        var result = "";
        final var defaultArrayType = "Vec<serde_json::Value>";

        for (var item : array) {
            if (isString(item)) {
                if (result.isEmpty()) {
                    result = "Vec<String>";
                } else if (!result.equals("Vec<String>")) {
                    return defaultArrayType;
                }
            }
            else if (isBoolean(item)) {
                if (result.isEmpty()) {
                    result = "Vec<bool>";
                } else if (!result.equals("Vec<bool>")) {
                    return defaultArrayType;
                }
            }
            else if (isNumber(item)) {
                if (result.isEmpty()) {
                    result = "Vec<f64>";
                } else if (!result.equals("Vec<f64>")) {
                    return defaultArrayType;
                }
            }
            else {
                result = defaultArrayType;
                break;
            }
        }

        return result;
    }

    private static String csharpDetermineArrayType(JsonArray array) {
        var result = "";
        final var defaultArrayType = "JToken[]";

        for (var item : array) {
            if (isString(item)) {
                if (result.isEmpty()) {
                    result = "string[]";
                } else if (!result.equals("string[]")) {
                    return defaultArrayType;
                }
            }
            else if (isBoolean(item)) {
                if (result.isEmpty()) {
                    result = "bool[]";
                } else if (!result.equals("bool[]")) {
                    return defaultArrayType;
                }
            }
            else if (isNumber(item)) {
                if (result.isEmpty()) {
                    result = "double[]";
                } else if (!result.equals("double[]")) {
                    return defaultArrayType;
                }
            }
            else {
                result = defaultArrayType;
                break;
            }
        }

        return result;
    }

    private static void golangHandleArray(JsonElement element, HashMap<String, JsonElement> values, String name, StringBuilder builder) {

        var pascalCasedName = convertToPascalCase(name);

        var nounPascalCasedName = pascalCasedName
                .subSequence(0, pascalCasedName.length()-1)
                .toString();

        var array = element.getAsJsonArray();

        if (!array.isEmpty()) {
            var firstElement = array.get(0);
            if (firstElement.isJsonObject()) {
                values.put(nounPascalCasedName, firstElement);
                builder.append(String.format("\t%s %s[] `json:\"%s\"`\r\n", pascalCasedName, nounPascalCasedName, name));
            }
            else {
                var arrayType = golangDetermineArrayType(array);
                builder.append(String.format("\t%s %s `json:\"%s\"`\r\n", pascalCasedName, arrayType, name));
            }
        }
        else {
            builder.append(String.format("\t%s []interface{} `json:\"%s\"`\r\n", pascalCasedName, name));
        }
    }

    private static void rustHandleArray(JsonElement element, HashMap<String, JsonElement> values, String name, StringBuilder builder) {

        var pascalCasedName = convertToPascalCase(name);

        var nounPascalCasedName = pascalCasedName
                .subSequence(0, pascalCasedName.length()-1)
                .toString();

        var array = element.getAsJsonArray();

        if (!array.isEmpty()) {
            var firstElement = array.get(0);
            if (firstElement.isJsonObject()) {
                values.put(nounPascalCasedName, firstElement);
                builder.append(String.format("\tpub %s: Vec<%s>,\r\n", name, nounPascalCasedName));
            }
            else {
                var arrayType = rustDetermineArrayType(array);
                builder.append(String.format("\tpub %s: %s,\r\n", name, arrayType));
            }
        }
        else {
            builder.append(String.format("\tpub %s: Vec<serde_json::Value>,\r\n", name));
        }
    }

    private static void csharpHandleArray(JsonElement element, HashMap<String, JsonElement> values, String name, StringBuilder builder) {

        var pascalCasedName = convertToPascalCase(name);

        var nounPascalCasedName = pascalCasedName
                .subSequence(0, pascalCasedName.length()-1)
                .toString();

        var array = element.getAsJsonArray();

        if (!array.isEmpty()) {
            var firstElement = array.get(0);
            if (firstElement.isJsonObject()) {
                values.put(nounPascalCasedName, firstElement);
                builder.append(String.format("\t[JsonProperty(\"%s\")]\r\n\tpublic %s[] %s { get; set; } \r\n", name, nounPascalCasedName, pascalCasedName));
            }
            else {
                var arrayType = csharpDetermineArrayType(array);
                builder.append(String.format("\t[JsonProperty(\"%s\")]\r\n\tpublic %s %s { get; set; } \r\n", name, arrayType, pascalCasedName));
            }
        }
        else {
            builder.append(String.format("\t[JsonProperty(\"%s\")]\r\n\tpublic JToken[] %s { get; set; } \r\n", name, pascalCasedName));
        }
    }
}
