package ru.vsu.sc.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class JSONParser {

    public static Map<String, Object> parseJSON(String filePath) {
        String jsonData = readFile(filePath);
        return parseJSONObject(jsonData);
    }

    private static String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> parseJSONObject(String jsonData) {
        Map<String, Object> jsonMap = new LinkedHashMap<>();

        jsonData = jsonData.trim();
        if (jsonData.startsWith("{") && jsonData.endsWith("}")) {
            jsonData = jsonData.substring(1, jsonData.length() - 1);

            String[] keyValuePairs = jsonData.split(",");
            for (String keyValuePair : keyValuePairs) {
                String[] keyValue = keyValuePair.split(":", 2);
                String key = keyValue[0].trim().replace("\"", "");
                Object value = parseValue(keyValue[1]);
                jsonMap.put(key, value);
            }
        } else {
            throw new IllegalArgumentException("Invalid JSON format");
        }

        return jsonMap;
    }

    private static Object parseValue(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        } else if ("true".equalsIgnoreCase(value)) {
            return true;
        } else if ("false".equalsIgnoreCase(value)) {
            return false;
        } else if (value.contains(".")) {
            return Double.parseDouble(value);
        } else {
            return Integer.parseInt(value);
        }
    }
}