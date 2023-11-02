public class Main {
    public static void main(String[] args) {
        PriceCatalog priceCatalog = new PriceCatalog(args[1]);
        //priceCatalog.testOutput();

        PurchaseOrder purchaseOrder = new PurchaseOrder(args[0]);
        //purchaseOrder.testOutput();
        purchaseOrder.billOutput();
    }
}
