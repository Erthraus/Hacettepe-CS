import java.util.ArrayList;
import java.util.Date;

public class Customer {
    private String name;
    private String membershipType;
    private Date purchaseDate;
    private ArrayList<Product> orders;
    private double totalCost;

    public Customer(String name, String membershipType, Date purchaseDate, ArrayList<Product> orders) {
        this.name = name;
        this.membershipType = membershipType;
        this.purchaseDate = purchaseDate;
        this.orders = orders;
        calculateTotalCost();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public ArrayList<Product> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Product> orders) {
        this.orders = orders;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public void calculateTotalCost() {
        totalCost = 0;
        for(Product product : orders) totalCost += product.getPrice() * product.getNumOfOrders();
    }

    public String calculateSubTotalCost(String name) {
        for(Product product : orders) {
            if(product.getName().equalsIgnoreCase(name)) return Double.toString(product.getPrice() * product.getNumOfOrders());
        }
        return null;
    }
}
