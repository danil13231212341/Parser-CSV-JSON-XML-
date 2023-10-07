package ru.vsu.sc.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {
    private String filePath;

    public CSVParser(String filePath) {
        this.filePath = filePath;
    }

    public List<Product> parseCsv() {
        List<Product> products = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Разделение строки на поля по разделителю ";"
                String[] fields = line.split(";");

                // Создание объекта Product из полей и добавление его в список
                int id = Integer.parseInt(fields[0]);
                String name = fields[1];
                double price = Double.parseDouble(fields[2]);
                Product product = new Product(id, name, price);
                products.add(product);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return products;
    }
}