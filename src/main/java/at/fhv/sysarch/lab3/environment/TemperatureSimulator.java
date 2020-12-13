package at.fhv.sysarch.lab3.environment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.DispatcherSelector;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab3.actors.Actor;

import java.time.Duration;
import java.util.Random;

public class TemperatureSimulator extends AbstractBehavior<TempNotification> {

    private ActorRef<TempNotification> forwardTo;
    private Actor actor;
    private boolean isAcOn = false;
    private double currentTemp = 22;


    public static Behavior<TempNotification> create(ActorRef<TempNotification> forwardTo) {
        return Behaviors.setup(context -> new TemperatureSimulator(context, forwardTo));
    }

    private TemperatureSimulator(ActorContext<TempNotification> context, ActorRef<TempNotification> forwardTo) {
        super(context);
        this.actor = Actor.TEMPERATURE_SIMULATOR;
        this.forwardTo = forwardTo;
        onReadTemperatureValue(new TempNotification());
    }

    @Override
    public Receive<TempNotification> createReceive() {
        return newReceiveBuilder().onMessage(TempNotification.class, this::onReadTemperatureValue).build();
    }

    private Behavior<TempNotification> onReadTemperatureValue(TempNotification temp) {

        if (temp.checkOnAcNotification()) {
            isAcOn = temp.checkIfAcOn();
        } else {
            System.out.println(temp.checkIfAcOn());

            TempNotification tempNotification = new TempNotification();
            currentTemp = currentTemp + calcRandomTemp();
            tempNotification.setTemperature(currentTemp);
            getContext().getSystem().scheduler().scheduleOnce(
                    Duration.ofMillis(20000),
                    () -> getContext().getSelf().tell(tempNotification),
                    getContext().getSystem().dispatchers().lookup(DispatcherSelector.defaultDispatcher()));
            forwardTo.tell(temp);

            System.out.println(currentTemp);
        }
        return Behaviors.same();
    }

    private Double calcRandomTemp() {
        System.out.println("ac on?: "+isAcOn);
        double MIN = -1;
        double MAX = 1;
        Random random = new Random();
        if (!isAcOn) {
            return (MIN + (MAX - MIN) * random.nextDouble());
        } else {
            // reduce bei 0.2 d because AC is on
            return (MIN + (MAX - MIN) * random.nextDouble()) - 0.2d;
        }
    }
}
