package ru.vsu.sc.parser;


import ru.vsu.sc.parser.util.MyHashMultiMap;

import java.security.Key;
import java.util.ArrayList;
import java.util.Map;


public class Main {
    public static void main(String[] args) {
        //// чтение файла csv
        //String path = "C:\\Users\\Ideapad Gaming\\IdeaProjects\\Parser\\src\\ru\\vsu\\sc\\parser\\File\\data.csv";
        //String delimiter = ";";
        //CSVParser parser = new CSVParser(path, delimiter);
        //parser.parseCSV();
        //// чтение файла json
        //Map<String, Object> jsonMap = (Map<String, Object>) JSONParser.parseJSON("src/ru/vsu/sc/parser/File/data2.json");
        //List<Object> jsonMap = (List<Object>) JSONParser.parseJSON("src/ru/vsu/sc/parser/File/data1.json");

        // Используем полученный объект jsonMap
        //myPrint(jsonMap, 0);
        //System.out.println(jsonMap);


        // XML parser
        MyHashMultiMap<String, Object> xmlMap = XMLParser.parseXML("src/ru/vsu/sc/parser/File/data2.xml");
        myXmlPrint(xmlMap, 0);
        System.out.println(xmlMap);
        System.out.println("-".repeat(20));
        System.out.println(XMLParser.fixMMap(xmlMap));
        //String keyLine = "PurchaseOrder:1=>Address=>Street";
        //System.out.println(XMLParser.getFromXMLMapByKeyLine(xmlMap, keyLine));

    }

    public static void myJsonPrint(Object o, int n) {
        if (o instanceof Map<?, ?>) {
            Map<String, Object> map = (Map<String, Object>) o;
            System.out.println("\t".repeat(n) + "{");
            for (String key : map.keySet()) {
                System.out.println("\t".repeat(n) + "\"" + key + "\" : ");
                myJsonPrint(map.get(key), n + 1);
            }
            System.out.println("\t".repeat(n) + "}");
        } else if (o instanceof ArrayList<?>) {
            System.out.println("\t".repeat(n) + "[");
            for (Object obj : (ArrayList<Object>) o) {
                myJsonPrint(obj, n + 1);
            }
            System.out.println("\t".repeat(n) + "]");

        } else if (o instanceof String) {
            System.out.println("\t".repeat(n) + "\"" + o + "\",");
        } else System.out.println("\t".repeat(n) + o + ",");
    }

    public static void myXmlPrint(Object o, int n){
        MyHashMultiMap<String, Object> mMap = (MyHashMultiMap<String, Object>) o;
        System.out.println("\t".repeat(n) + "{");
        for(String key : mMap.keySet()){
            System.out.println("\t".repeat(n + 1) + key + ": [");
            for(Object value : mMap.get(key)){
                if(value instanceof MyHashMultiMap<?,?>) {
                    myXmlPrint(value, n + 1);
                } else{
                    System.out.println("\t".repeat(n + 2) + value + ",");
                }
            }
            System.out.println("\t".repeat(n + 1) + "]");

        }
        System.out.println("\t".repeat(n) + "}");
    }
}
