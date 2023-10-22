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

    public static MyHashMultiMap<String, Object> parseXML(String filePath) {
        String jsonData = readFile(filePath);
        return parseXMLbyData(jsonData);
    }

    private static MyHashMultiMap<String, Object> parseXMLbyData(String jsonData) {
        return parseXML(new IndexWrapper(jsonData));
    }

    private static String parseTag(IndexWrapper iw) {
        assert iw.charNow() == '<' : "Не нечало тега";
        int indexStart = iw.getIndex();
        iw.nextWhileNot('>');
        return iw.getData().substring(indexStart + 1, iw.getIndex());
    }

    private static MyHashMultiMap<String, Object> parseXML(IndexWrapper iw) {
        iw.nextWhileNot('<');

        MyHashMultiMap<String, Object> mMap = new MyHashMultiMap<>();

        String curTag = parseTag(iw);
        String closedCurTag = "/" + curTag;

        int indexSave = iw.getIndex(); // stand on '>'
        String tagNow;

        iw.next();
        iw.nextWhileNot('<');
        tagNow = parseTag(iw);
        if (tagNow.equals(closedCurTag)) {
            // <tag>!no tags inside!</tag>      =>      primitive value inside
            iw.backWhileNot('<');
            mMap.put(curTag, parsePrimitiveValue(iw.getData().substring(indexSave + 1, iw.getIndex())));
            iw.nextWhileNot('>');
            return mMap;
        }
        while (!Objects.equals(closedCurTag, tagNow)) {
            iw.next();
            iw.backWhileNot('<');
            mMap.put(curTag, parseXML(iw));

            iw.next();
            iw.nextWhileNot('<'); // next tag
            tagNow = parseTag(iw);
        }
        return mMap;
    }

    private static Object parsePrimitiveValue(String value) {
        if (true) return value;
        if ("true".equals(value)) return true;
        if ("false".equals(value)) return false;
        if (value.contains(".")) return Double.parseDouble(value);

        else return Integer.parseInt(value);

    }

    public static Object getFromXMLMapByKeyLine(MyHashMultiMap<String, Object> mMap, String keyLine) {
        List<String> keyList = parseKeyLine(keyLine);
        System.out.println(keyList);
        Object obj = mMap;
        for (String key : keyList) {
            MyHashMultiMap<String, Object> localMMap = (MyHashMultiMap<String, Object>) obj;
            String subKey = key.substring(0, key.indexOf(":"));
            int index = Integer.parseInt(key.substring(key.indexOf(":") + 1));
            obj = localMMap.get(subKey).get(index);
        }
        return obj;
    }

    public static Map<String, Object> fixMMap(MyHashMultiMap<String, Object> mMap){
        Map<String, Object>obj = new HashMap<>();
        for(String key : mMap.keySet()){
            if(mMap.get(key).size() == 1) {
                if(mMap.get(key).get(0) instanceof MyHashMultiMap<?,?>){
                    obj.putAll(fixMMap((MyHashMultiMap<String, Object>) mMap.get(key).get(0)));
                } else{
                    obj.put(key, mMap.get(key).get(0));
                }
            }
            else {
                List<Map<String, Object>> lst = new ArrayList<>(mMap.get(key).size());
                for(Object value : mMap.get(key)){
                    if(value instanceof MyHashMultiMap<?,?>){
                       lst.add(fixMMap((MyHashMultiMap<String, Object>) value));
                    } else{
                        obj.put(key, value);
                    }
                }
                for(Map<String, Object> lstObj: lst){
                    for(String lstKey : lstObj.keySet()){
                        obj.put(lstKey, lstObj.get(lstKey));
                    }
                }
            }
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

}
