package at.fhv.sysarch.lab3.refrigerator;

import at.fhv.sysarch.lab3.INotification;

public class FridgeStatusNotification implements INotification{

    public enum Actions {
        CURRENT_PRODUCTS("current_products"), ORDER_HISTORY("order_history");

        public final String action;
        Actions(String action) {
            this.action = action;
        }
    }

    public final String action;

    public FridgeStatusNotification(String action) {
            this.action = action;
        }

}
