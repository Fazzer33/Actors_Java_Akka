package at.fhv.sysarch.lab3.refrigerator;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class SpaceSensor extends AbstractBehavior<FridgeNotification> {
    public SpaceSensor(ActorContext<FridgeNotification> context) {
        super(context);
    }

    @Override
    public Receive<FridgeNotification> createReceive() {
        return null;
    }

    public static Behavior<FridgeNotification> create() {
        return Behaviors.setup(SpaceSensor::new);
    }
}
