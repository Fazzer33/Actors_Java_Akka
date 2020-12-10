package at.fhv.sysarch.lab3;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;

public class TemperatureSensor extends AbstractBehavior<Double> {

    public TemperatureSensor(ActorContext<Double> context) {
        super(context);
    }

    @Override
    public Receive<Double> createReceive() {
        return null;
    }
}
