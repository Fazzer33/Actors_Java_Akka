package at.fhv.sysarch.lab3.refrigerator;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;

public class OrderProcessor extends AbstractBehavior<FridgeNotification> {
    public OrderProcessor(ActorContext<FridgeNotification> context) {
        super(context);
    }

    @Override
    public Receive<FridgeNotification> createReceive() {
        return null;
    }
}
