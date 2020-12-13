package at.fhv.sysarch.lab3.environment;

import at.fhv.sysarch.lab3.INotification;

public class TempNotification implements INotification {

    private Double temperature = 22d;
    private boolean isACon = false;

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void turnOnAc() {
        isACon = true;
    }

    public void turnOffAc() {
        isACon = false;
    }
}
