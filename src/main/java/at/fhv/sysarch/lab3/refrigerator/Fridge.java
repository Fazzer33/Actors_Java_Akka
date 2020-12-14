package at.fhv.sysarch.lab3.refrigerator;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab3.INotification;
import at.fhv.sysarch.lab3.actors.Actor;

public class Fridge extends AbstractBehavior<FridgeNotification> {
    public enum Actions {
        START("start_film"), TURN_OFF("end");

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

    public static Behavior<FridgeNotification> create() {
        return Behaviors.setup(Fridge::new);
    }

    public Fridge(ActorContext<FridgeNotification> context) {
        super(context);
        this.actor = Actor.FRIDGE;
        productSensor = getContext().spawn(ProductSensor.create(), "productSensor");
        spaceSensor = getContext().spawn(SpaceSensor.create(), "productSensor");

    }

    @Override
    public Receive<FridgeNotification> createReceive() {
        return newReceiveBuilder().onMessage(FridgeNotification.class, this::onNotified).build();
    }

    private Behavior<FridgeNotification> onNotified(FridgeNotification notification) {
        return this;
    }
}
