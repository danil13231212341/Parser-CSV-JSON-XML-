package ru.vsu.sc.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSVParser {
    private String path;
    private String delimiter;

    public CSVParser(String path, String delimiter) {
        this.path = path;
        this.delimiter = delimiter;
    }

    public void parseCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(delimiter);
                for (String value : values) {
                    System.out.print(value + "\t");
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
