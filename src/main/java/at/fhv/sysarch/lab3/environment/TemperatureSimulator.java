package at.fhv.sysarch.lab3.environment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.DispatcherSelector;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.time.Duration;

public class TemperatureSimulator extends AbstractBehavior<Double> {

    private ActorRef<Double> tempSensor;

    public static Behavior<Double> create(ActorRef<Double> tempSensor) {
        return Behaviors.setup(context -> new TemperatureSimulator(context, tempSensor));
    }

    private TemperatureSimulator(ActorContext<Double> context, ActorRef<Double> tempSensor) {
        super(context);
        this.tempSensor = tempSensor;
    }

    @Override
    public Receive<Double> createReceive() {
        return newReceiveBuilder().onMessage(Double.class, this::onReadTemperatureValue).build();
    }

    private Behavior<Double> onReadTemperatureValue(Double temp) {
        getContext().getSystem().scheduler().scheduleOnce(
                Duration.ofMillis(5000),
                () -> {getContext().getSelf().tell(15.5d);},
                getContext().getSystem().dispatchers().lookup(DispatcherSelector.defaultDispatcher())
        );
        tempSensor.tell(temp);
        return Behaviors.same();
    }
}
