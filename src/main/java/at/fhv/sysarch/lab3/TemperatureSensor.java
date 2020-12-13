package at.fhv.sysarch.lab3;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab3.environment.TempNotification;

public class TemperatureSensor extends AbstractBehavior<INotification> {

    private ActorRef<INotification> forwardTo;

    public static Behavior<INotification> create(ActorRef<INotification> replyTo){
        return Behaviors.setup(context -> new TemperatureSensor(context, replyTo));
    }

    public TemperatureSensor(ActorContext<INotification> context, ActorRef<INotification> forwardTo) {
        super(context);
        this.forwardTo = forwardTo;
    }

    @Override
    public Receive<INotification> createReceive() {
        return newReceiveBuilder().onMessage(TempNotification.class, this::onReadTemperatureValue).build();
    }

    private Behavior<INotification> onReadTemperatureValue(TempNotification value){

        TempNotification tempNotification = new TempNotification();
        tempNotification.setTemperature(value.getTemperature());
        forwardTo.tell(tempNotification);
        return Behaviors.same();
    }
}
