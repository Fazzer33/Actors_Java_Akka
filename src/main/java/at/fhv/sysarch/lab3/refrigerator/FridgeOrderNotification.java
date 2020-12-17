package at.fhv.sysarch.lab3.refrigerator;

import akka.japi.Pair;
import at.fhv.sysarch.lab3.INotification;
import java.util.HashMap;

public class FridgeOrderNotification implements INotification {
    public HashMap<ProductType, Pair<Product, Integer>> productMap;
    public double orderWeight;

    public FridgeOrderNotification(HashMap<ProductType, Pair<Product, Integer>> productMap, double orderWeight) {
        this.productMap = productMap;
        this.orderWeight = orderWeight;
    }
}
