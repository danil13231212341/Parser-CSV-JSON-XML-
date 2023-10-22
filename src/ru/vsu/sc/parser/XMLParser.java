package ru.vsu.sc.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class XMLParser {
    private static class IndexWrapper {
        private int index = 0;
        private final String data;

        public IndexWrapper(String data) {
            this.data = data.trim();
        }

        public Character charNow() {
            return data.charAt(index);
        }

        public void next() {
            index++;
        }

        public void back() {
            index--;
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

        private int backWhileNot(Character chr) {
            /*
            stays if now on chr.
             */
            while (!Objects.equals(charNow(), chr)) back();
            return index;
        }

        public Character nextWhileNotIn(String str) {
            while (!str.contains(String.valueOf(charNow()))) next();
            return charNow();
        }

        public int getIndex() {
            return index;
        }

        public String getData() {
            return data;
        }
    }

    private Object map;
    public XMLParser(String filePath) {
        this.map = parseXML(filePath);
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

    private static Object parseXML(String filePath) {
        String xmlData = readFile(filePath);
        return parseXMLbyData(xmlData);
    }

    private static Object parseXMLbyData(String xmlData) {
        return parseXML(new IndexWrapper(xmlData));
    }

    private static String parseTag(IndexWrapper iw) {
        assert iw.charNow() == '<' : "Не нечало тега";
        int indexStart = iw.getIndex();
        iw.nextWhileNot('>');
        return iw.getData().substring(indexStart + 1, iw.getIndex());
    }

    private static Object parseXML(IndexWrapper iw) {
        Stack<String> tagStack = new Stack<>();
        Stack<Integer> indexStack = new Stack<>();
        Stack<Map<String, List<Object>>> mapStack = new Stack<>();


        Map<String, List<Object>> toReturn = new HashMap<>();

        iw.nextWhileNot('<');
        String curTag = parseTag(iw);

        indexStack.add(iw.index);
        tagStack.add("/" + curTag);
        mapStack.add(toReturn);
        mapStack.add(new HashMap<>());

        //System.out.println(curTag);

        //System.out.println("Старт");
        //System.out.println(tagStack);

        while (!tagStack.isEmpty()) {
            iw.nextWhileNot('<');

            curTag = parseTag(iw);
            //System.out.println(curTag);

            if (Objects.equals(curTag, tagStack.peek())) {
                String realTag = curTag.substring(1);
                int indexStart = indexStack.peek();
                int indexEnd = iw.backWhileNot('<');
                iw.nextWhileNot('>');

                // no tag inside check
                iw.back();
                int indexLast = iw.backWhileNot('>');

                iw.next();
                iw.nextWhileNot('>');

                if (indexLast == indexStack.peek()) {
                    mapStack.pop();
                    Object value = parsePrimitiveValue(iw.getData().substring(indexStart + 1, indexEnd));


                    List<Object> lst =  mapStack.peek().getOrDefault(realTag, new ArrayList<>());
                    lst.add(value);
                    mapStack.peek().put(realTag, lst);

                    //System.out.println("value : " + value);
                } else {
                    // need mapToAdd add
                    Map<String, List<Object>> mapToAdd = mapStack.pop();

                    List<Object> lst = mapStack.peek().getOrDefault(realTag, new ArrayList<>());
                    lst.add(mapToAdd);
                    mapStack.peek().put(realTag, lst);
                }


                indexStack.pop();
                tagStack.pop();

                //System.out.println("Закрыл");
                //System.out.println(tagStack);

            } else {
                indexStack.add(iw.index);
                tagStack.add("/" + curTag);
                mapStack.add(new HashMap<>());

                //System.out.println("Новый");
                //System.out.println(tagStack);

            }
            //.out.println("_-".repeat(20));
        }
        return toReturn;
    }

    public Object getByKeyLine( String keyLine) {
        List<String> keyList = parseKeyLine(keyLine);
        Object obj = map;
        for (String key : keyList) {
            Map<String, List<Object>> localMap = (Map<String, List<Object>>) obj;
            String subKey = key.substring(0, key.indexOf(":"));
            int index = Integer.parseInt(key.substring(key.indexOf(":") + 1));

            obj = localMap.get(subKey).get(index);
        }
        return obj;
    }

    private static List<String> parseKeyLine(String keyLine) {
        List<String> keyList = new java.util.ArrayList<>(List.of(keyLine.split("=>")));
        for (int i = 0; i < keyList.size(); i++) {
            if (!keyList.get(i).contains(":")) keyList.set(i, keyList.get(i) + ":0");
        }
        return keyList;
    }
    private static boolean isDouble(String value){
        try{
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }
    private static boolean isInteger(String value){
        try{
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }
    private static Object parsePrimitiveValue(String value) {
        String valuerTrim = value.trim();
        if ("true".equals(valuerTrim)) return true;
        if ("false".equals(valuerTrim)) return false;
        if (isDouble(valuerTrim)) return Double.parseDouble(valuerTrim);
        if (isInteger(valuerTrim)) return Integer.parseInt(valuerTrim);
        return value;
    }

    public Object getMap() {
        return map;
    }
}
