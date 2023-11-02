import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

public class PriceCatalog {
    private static ArrayList<Product> products = null;

    public PriceCatalog(String path) {
        products = new ArrayList<>();
        readCatalog(path);
    }

    public void readCatalog(String path) {
        String[] lines = ReadFromFile.readFile(path);

        for(String line : lines) {
            String[] parts = line.split("\t");

            if (parts.length < 5) {
                System.out.println("Invalid data in" + path + ": " + line);
                continue;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            String name = parts[0];
            String membershipType = parts[1];
            Date startDate;
            Date endDate;
            try {
                startDate = dateFormat.parse(parts[2]);
                endDate = dateFormat.parse(parts[3]);
            } catch(ParseException e) {
                throw new RuntimeException(e);
            }
            double price = Double.parseDouble(parts[4]);

            Product product = new Product(name, membershipType, startDate, endDate, price);
            products.add(product);
        }
    }

    public void testOutput() {
        for(Product product : products) product.output();
    }

    public static Product checkProduct(String name, String type, Date date) {
        for(Product product : products) {
            if(!name.equalsIgnoreCase(product.getName())) continue;
            if(!type.equalsIgnoreCase(product.getType())) continue;
            if((date.after(product.getStartDate()) || date.equals(product.getStartDate())) && (date.before(product.getEndDate()) || date.equals(product.getEndDate())))
                    return product;
        }
        return null;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }
}