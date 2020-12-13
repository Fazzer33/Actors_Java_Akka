package at.fhv.sysarch.lab3.airConditioning;

import at.fhv.sysarch.lab3.INotification;

public class ACNotification implements INotification {
    public final String action;

    public ACNotification(String action) {
        this.action = action;
    }
}
