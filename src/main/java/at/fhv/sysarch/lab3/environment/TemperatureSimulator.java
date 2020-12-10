package at.fhv.sysarch.lab3.environment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.DispatcherSelector;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab3.Notifier;
import at.fhv.sysarch.lab3.actors.Actor;

import java.time.Duration;

public class TemperatureSimulator extends AbstractBehavior<Notifier.Notified> {

//    private ActorRef<Double> tempSensor;
    private Actor actor;


    public static Behavior<Notifier.Notified> create() {
        return Behaviors.setup(TemperatureSimulator::new);
    }

    private TemperatureSimulator(ActorContext<Notifier.Notified> context) {
        super(context);
        this.actor = Actor.TEMPERATURE_SIMULATOR;
    }

    @Override
    public Receive<Notifier.Notified> createReceive() {
        return newReceiveBuilder().onMessage(Notifier.Notified.class, this::onReadTemperatureValue).build();
    }

    private Behavior<Notifier.Notified> onReadTemperatureValue(Notifier.Notified temp) {
//        getContext().getLog().info(temp.whom);
        getContext().getSystem().scheduler().scheduleOnce(
                Duration.ofMillis(5000),
                () -> {
                    temp.from.tell(new Notifier.Notify(temp.whom, getContext().getSelf()));
                    },
                getContext().getSystem().dispatchers().lookup(DispatcherSelector.defaultDispatcher())
        );
//        System.out.println("Temperature is:" +temp.whom);
        return Behaviors.same();
    }
}
