package ru.vsu.sc.parser;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Main {

    public static void testJSON1() {
        Object jsonMap = JSONParser.parseJSON("src/ru/vsu/sc/parser/File/data1.json");
        myPrint(jsonMap, 0);
    }

    public static void testJSON2() {
        Object jsonMap = JSONParser.parseJSON("src/ru/vsu/sc/parser/File/data2.json");
        myPrint(jsonMap, 0);
    }

    public static void testCSV1() {
        String path = "src/ru/vsu/sc/parser/File/data.csv";
        String delimiter = ";";
        CSVParser parser = new CSVParser(path, delimiter);
        parser.parseCSV();
    }

    public static void testXML1() {
        XMLParser xmlParser = new XMLParser("src/ru/vsu/sc/parser/File/data2.xml");
        myPrint(xmlParser.getMap(), 0);

        String keyLine = "PurchaseOrder=>Address:0=>Street";
        System.out.println('"' + keyLine + '"' + " : " +  '"' +xmlParser.getByKeyLine(keyLine) + '"');

        keyLine = "PurchaseOrder=>Address:1=>Street";
        System.out.println('"' + keyLine + '"' + " : " +  '"' +xmlParser.getByKeyLine(keyLine) + '"');

        keyLine = "PurchaseOrder=>DeliveryNotes";
        System.out.println('"' + keyLine + '"' + " : " +  '"' +xmlParser.getByKeyLine(keyLine) + '"');

        keyLine = "PurchaseOrder=>Items=>Item:1=>ProductName:0";
        System.out.println('"' + keyLine + '"' + " : " +  '"' +xmlParser.getByKeyLine(keyLine) + '"');
    }

    public static void main(String[] args) {
        testXML1();
        //testCSV1();
        //testJSON1();
        //testJSON2();

    }

    public static void myPrint(Object o, int n) {
        if (o instanceof Map<?, ?>) {
            Map<String, Object> map = (Map<String, Object>) o;
            System.out.println("\t".repeat(n) + "{");
            for (String key : map.keySet()) {
                System.out.println("\t".repeat(n) + "\"" + key + "\" : ");
                myPrint(map.get(key), n + 1);
            }
            System.out.println("\t".repeat(n) + "}");
        } else if (o instanceof List<?>) {
            System.out.println("\t".repeat(n) + "[");
            for (Object obj : (List<?>) o) {
                myPrint(obj, n + 1);
            }
            System.out.println("\t".repeat(n) + "]");

        } else if (o instanceof String) {
            System.out.println("\t".repeat(n) + "\"" + o + "\",");
        } else System.out.println("\t".repeat(n) + o + ",");
    }
}
