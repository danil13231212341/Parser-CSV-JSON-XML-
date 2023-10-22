package ru.vsu.sc.parser;

import ru.vsu.sc.parser.util.MyHashMultiMap;

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

    public static Object parseXML(String filePath) {
        String jsonData = readFile(filePath);
        return parseXMLbyData(jsonData);
    }

    private static Object parseXMLbyData(String jsonData) {
        return parseXML(new IndexWrapper(jsonData));
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

        System.out.println(curTag);

        System.out.println("Старт");
        System.out.println(tagStack);

        while (!tagStack.isEmpty()) {
            iw.nextWhileNot('<');

            curTag = parseTag(iw);
            System.out.println(curTag);

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

                if(indexLast == indexStack.peek()){
                    Map<String, List<Object>> mapToAdd = mapStack.pop();
                    List<Object> lst = mapToAdd.getOrDefault(realTag, new ArrayList<>());

                    String value = iw.getData().substring(indexStart + 1, indexEnd);
                    lst.add(value);

                    lst = mapStack.peek().getOrDefault(realTag, new ArrayList<>());
                    lst.add(mapToAdd);
                    mapStack.peek().put(realTag, lst);

                    System.out.println("value : " + value);
                }
                else{
                    // need mapToAdd add
                    Map<String, List<Object>> mapToAdd = mapStack.pop();

                    List<Object> lst = mapStack.peek().getOrDefault(realTag, new ArrayList<>());
                    lst.add(mapToAdd);
                    mapStack.peek().put(realTag, lst);
                }


                indexStack.pop();
                tagStack.pop();

                System.out.println("Закрыл");
                System.out.println(tagStack);

            } else {
                indexStack.add(iw.index);
                tagStack.add("/" + curTag);
                mapStack.add(new HashMap<>());

                System.out.println("Новый");
                System.out.println(tagStack);

            }
            System.out.println("_-".repeat(20));
        }
        return toReturn;
    }

    private static Object parsePrimitiveValue(String value) {
        if (true) return value;
        if ("true".equals(value)) return true;
        if ("false".equals(value)) return false;
        if (value.contains(".")) return Double.parseDouble(value);

        else return Integer.parseInt(value);

    }

}
