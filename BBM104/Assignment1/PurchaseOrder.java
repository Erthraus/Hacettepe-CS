import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class PurchaseOrder {
    private static ArrayList<Customer> customers = null;

    public PurchaseOrder(String path) {
        customers = new ArrayList<>();
        readOrders(path);
    }

    public void readOrders(String path) {
        String[] lines = ReadFromFile.readFile(path);

        for(String line : lines) {
            String[] parts = line.split("\t");

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            String name = parts[0];
            String membershipType = parts[1];
            Date orderDate;
            try {
                orderDate = dateFormat.parse(parts[2]);

            } catch(ParseException e) {
                throw new RuntimeException(e);
            }

            ArrayList<Product> orders = new ArrayList<>();
            for (int i = 3; i < parts.length; i++) {
                Product product = PriceCatalog.checkProduct(parts[i], membershipType, orderDate);
                if(product != null) {
                    int numOfOrders = Integer.parseInt(parts[++i]);
                    product.setNumOfOrders(numOfOrders);
                    orders.add(product);
                }
            }

            Customer customer = new Customer(name, membershipType, orderDate, orders);
            customers.add(customer);
            if(customers.size() > 10) System.out.println("There are more than 10 customers!");
        }
    }

    public void testOutput() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        for(Customer customer : customers) {
            System.out.println(customer.getName() + " " + customer.getMembershipType() + " " + simpleDateFormat.format(customer.getPurchaseDate()));
            for(Product product : customer.getOrders()) {
                product.output();
                System.out.println(product.getNumOfOrders() + " " + customer.calculateSubTotalCost(product.getName()));
            }
            System.out.println(customer.getTotalCost());
        }
    }

    public void billOutput() {
        String filePath = "output.txt";

        try {
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            int i = 0;

            for(Customer customer : customers) {
                bufferedWriter.write("------------Bill for Customer " + ++i + "-------------");
                bufferedWriter.newLine();
                bufferedWriter.write("Customer: " + customer.getName());
                bufferedWriter.newLine();
                bufferedWriter.write("Membership Type: " + customer.getMembershipType());
                bufferedWriter.newLine();
                bufferedWriter.write("Date: " + simpleDateFormat.format(customer.getPurchaseDate()));
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.write("Items Purchased:");

                for(Product product : customer.getOrders()) {
                    bufferedWriter.newLine();
                    bufferedWriter.write(product.getName() + " (Qty: " + product.getNumOfOrders() + ") - "
                                        + product.getPrice() + " each");
                    bufferedWriter.newLine();
                    bufferedWriter.write("(Valid from " + simpleDateFormat.format(product.getStartDate()) + " to " + simpleDateFormat.format(product.getEndDate()) + ")");
                    bufferedWriter.newLine();
                    bufferedWriter.write("Subtotal: " + customer.calculateSubTotalCost(product.getName()));
                }

                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.write("Total Cost: " + customer.getTotalCost());
                bufferedWriter.newLine();
                bufferedWriter.newLine();
            }

            bufferedWriter.close();

            System.out.println("Data has been written to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
