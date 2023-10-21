package ru.vsu.sc.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;
import java.util.*;

public class JSONParser {
    private static class IndexWapper {
        private int index = 0;
        private final String data;

        public IndexWapper(String data) {
            this.data = data.trim();
        }

        public Character charNow() {
            return data.charAt(index);
        }

        public void next() {
            index++;
        }

        public boolean hasNext() {
            return index + 1 < data.length();
        }

        private void skipSpaces() {
            while (Character.isWhitespace(data.charAt(index))) next();
        }

        private int nextWhileNot(Character chr) {
            /*
            stays if now on chr.
             */
            while (!Objects.equals(charNow(), chr)) next();
            return index;
        }

        public Character nextWhileNotIn(String str) {
            while ( !str.contains(String.valueOf(charNow()))) next();
            return charNow();
        }

        public int getIndex() {
            return index;
        }

        public String getData() {
            return data;
        }
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

    public static Object parseJSON(String filePath) {
        String jsonData = readFile(filePath);
        return parseJSONbyData(jsonData);
    }

    private static Object parseJSONbyData(String jsonData) {
        return parseValue(new IndexWapper(jsonData));
    }

    private static Map<String, Object> parseMap(IndexWapper iw) {
        String jsonData = iw.getData();
        Map<String, Object> jsonMap = new LinkedHashMap<>();

        // value
        int indexSave;

        iw.next();
        iw.skipSpaces();
        while (iw.charNow() != '}') {
            Character charNow = iw.charNow();
            // Key Search
            indexSave = iw.nextWhileNot('"');
            iw.next();
            iw.nextWhileNot('"');
            String key = jsonData.substring(indexSave + 1, iw.getIndex());
            // Value
            iw.nextWhileNot(':');
            iw.next();
            iw.skipSpaces();
            Object value = parseValue(iw);
            // Adding
            jsonMap.put(key, value);
            iw.skipSpaces();
        }
        iw.next();
        return jsonMap;
    }

    private static Object parseValue(IndexWapper iw) {
        iw.skipSpaces();
        Character chr = iw.charNow();
        if (chr == '[') return parseList(iw);
        if (chr == '{') return parseMap(iw);
        return parsePrimitiveValue(iw);

    }

    private static ArrayList<Object> parseList(IndexWapper iw) {
        ArrayList<Object> list = new ArrayList<>();
        iw.next();
        iw.skipSpaces();
        while(iw.charNow() != ']'){
            list.add(parseValue(iw));
        }
        iw.next();
        return list;
    }

    private static Object parsePrimitiveValue(IndexWapper iw) {

        if (iw.charNow() == '"') {
            int indexStart = iw.getIndex();
            iw.next();
            int indexEnd =  iw.nextWhileNot('"');
            iw.nextWhileNotIn(",]}");
            return iw.getData().substring(indexStart + 1, indexEnd).trim();
        }

        int indexSave = iw.getIndex();
        iw.next();
        iw.nextWhileNotIn(",]}");
        String value = iw.getData().substring(indexSave, iw.getIndex()).trim();

        if ("true".equals(value)) return true;
        if ("false".equals(value)) return false;
        if (value.contains(".")) return Double.parseDouble(value);
        else return Integer.parseInt(value);

    }
}