package ru.vsu.sc.parser;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String filePath = "C:\\Users\\Ideapad Gaming\\IdeaProjects\\Parser\\src\\ru\\vsu\\sc\\parser\\File\\Product2.csv";
        CSVParser csvParser = new CSVParser(filePath);
        List<Product> products = csvParser.parseCsv();

        // Использование списка товаров
        for (Product product : products) {
            System.out.println("ID: " + product.getId());
            System.out.println("Название: " + product.getName());
            System.out.println("Цена: " + product.getPrice());
            System.out.println();
        }

    }
    }
