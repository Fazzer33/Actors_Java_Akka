package at.fhv.sysarch.lab3.refrigerator;

import at.fhv.sysarch.lab3.INotification;

import java.util.HashMap;

public class OrderNotification implements INotification {
    public HashMap<ProductType, Integer> orderMap;

    public OrderNotification(HashMap<ProductType, Integer> orderMap) {
        this.orderMap = orderMap;
    }
}
