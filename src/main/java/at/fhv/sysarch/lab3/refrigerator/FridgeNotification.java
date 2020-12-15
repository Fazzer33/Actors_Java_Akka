package at.fhv.sysarch.lab3.refrigerator;

import at.fhv.sysarch.lab3.INotification;

public class FridgeNotification implements INotification {
    public String action;

    public FridgeNotification(String action) {
        this.action = action;
    }

}
