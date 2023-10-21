package ru.vsu.sc.parser;


import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        //// чтение файла csv
        //String path = "C:\\Users\\Ideapad Gaming\\IdeaProjects\\Parser\\src\\ru\\vsu\\sc\\parser\\File\\data.csv";
        //String delimiter = ";";
        //CSVParser parser = new CSVParser(path, delimiter);
        //parser.parseCSV();
        //// чтение файла json
        //Map<String, Object> jsonMap = (Map<String, Object>) JSONParser.parseJSON("src/ru/vsu/sc/parser/File/data1.json");
        List<Object> jsonMap = (List<Object>) JSONParser.parseJSON("src/ru/vsu/sc/parser/File/data1.json");

        // Используем полученный объект jsonMap
        System.out.println(jsonMap);
    }
}
