package at.fhv.sysarch.lab3.refrigerator;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab3.actors.Actor;

import java.util.List;

// In this case the fridge should relay this request to a separate OrderProcessor actor (see Per session child Actor).
// https://doc.akka.io/docs/akka/current/typed/interaction-patterns.html

public class Fridge extends AbstractBehavior<FridgeNotification> {
    public enum Actions {
        CONSUME("consume"), SHOW_PRODUCTS("showProducts"), ORDER_HISTORY("orderHistory"), ORDER("order");

        public final String action;
        Actions(String action) {
            this.action = action;
        }
    }

    private ActorRef<ProductSensor.Command> productSensor;
    private ActorRef<FridgeNotification> spaceSensor;

    private Actor actor;
    private boolean isEmpty = false;
    private final int MAX_PRODUCTS = 50;
    private final double MAX_WEIGHT = 80;
    private double currentWeight;
    private int currentProducts;
    private List<Product> productList;

    public static Behavior<FridgeNotification> create() {
        return Behaviors.setup(Fridge::new);
    }

    public Fridge(ActorContext<FridgeNotification> context) {
        super(context);
        this.actor = Actor.FRIDGE;
        productSensor = getContext().spawn(ProductSensor.create(), "productSensor");
        spaceSensor = getContext().spawn(SpaceSensor.create(), "spaceSensor");

    }

    @Override
    public Receive<FridgeNotification> createReceive() {
        return newReceiveBuilder().onMessage(FridgeNotification.class, this::onNotified).build();
    }

    private Behavior<FridgeNotification> onNotified(FridgeNotification notification) {

        if (notification.action.equals(Actions.ORDER.action)) {
            System.out.println("order");
        } else if(notification.action.equals(Actions.CONSUME.action)) {
            System.out.println("consume");
        }

        return this;
    }
}
