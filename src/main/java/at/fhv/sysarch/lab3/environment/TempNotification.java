package at.fhv.sysarch.lab3.environment;

import at.fhv.sysarch.lab3.INotification;

public class TempNotification implements INotification {

    private Double temperature = 22d;
    private boolean isAcOn = true;
    private boolean isAcNotification = false;

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public boolean checkIfAcOn() {
        return isAcOn;
    }

    public void acIsOn() {
        isAcOn = true;
    }

    public void acIsOff() {
        isAcOn = false;
    }

    public boolean checkOnAcNotification() {
        return isAcNotification;
    }

    public void setAcNotification() {
        isAcNotification = true;
    }
}
