package at.fhv.sysarch.lab3.blinds;

import at.fhv.sysarch.lab3.INotification;

public class BlindsNotification implements INotification {
    public final String action;
    private boolean isMediaStationOn = false;
    private boolean isMSNotification = false;

    public BlindsNotification(String action) {
        this.action = action;
    }

    public boolean checkIfMediaStationOn() {
        return isMediaStationOn;
    }

    public void setMediaStationOn() {
        isMediaStationOn = true;
    }

    public void setMediaStationOff() {
        isMediaStationOn = false;
    }

    public boolean checkOnMSNotification() {
        return isMSNotification;
    }

    public void setMSNotification() {
        isMSNotification = true;
    }
}
