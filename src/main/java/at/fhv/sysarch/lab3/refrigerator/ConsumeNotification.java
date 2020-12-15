package at.fhv.sysarch.lab3.refrigerator;

import akka.japi.Pair;
import at.fhv.sysarch.lab3.INotification;

public class ConsumeNotification implements INotification {
    public Pair<ProductType, Integer> product;

    public ConsumeNotification(Pair<ProductType, Integer> product) {
        this.product = product;
    }
}
