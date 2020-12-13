package at.fhv.sysarch.lab3.mediaStation;

import at.fhv.sysarch.lab3.INotification;

public class MediaStationNotification implements INotification {
    public final String action;

    public MediaStationNotification(String action) {
        this.action = action;
    }
}
