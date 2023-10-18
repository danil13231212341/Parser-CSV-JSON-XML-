package ru.vsu.sc.parser;



public class Main {
    public static void main(String[] args) {
        // чтение файла csv
        String path = "C:\\Users\\Ideapad Gaming\\IdeaProjects\\Parser\\src\\ru\\vsu\\sc\\parser\\File\\data.csv";
        String delimiter = ";";
        CSVParser parser = new CSVParser(path, delimiter);
        parser.parseCSV();
        // чтение файла json


    }
    }
