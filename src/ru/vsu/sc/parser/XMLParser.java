package ru.vsu.sc.parser;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
public class XMLParser {
    private String filePath;

    public XMLParser(String filePath) {
        this.filePath = filePath;
    }

    public List<Product> parse() {
        List<Product> products = new ArrayList<>();

        try {
            File file = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("product");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                String id = element.getElementsByTagName("id").item(0).getTextContent();
                String name = element.getElementsByTagName("name").item(0).getTextContent();
                String price = element.getElementsByTagName("price").item(0).getTextContent();

                Product product = new Product(id, name, price);
                products.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return products;
    }

}
