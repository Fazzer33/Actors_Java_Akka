package at.fhv.sysarch.lab3;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab3.environment.WeatherNotification;

public class WeatherSensor extends AbstractBehavior<INotification> {

    private ActorRef<INotification> forwardTo;

    public static Behavior<INotification> create(ActorRef<INotification> replyTo) {
        return Behaviors.setup(context -> new WeatherSensor(context, replyTo));
    }

    public WeatherSensor(ActorContext<INotification> context, ActorRef<INotification> forwardTo) {
        super(context);
        this.forwardTo = forwardTo;
    }

    @Override
    public Receive<INotification> createReceive() {
        return newReceiveBuilder().onMessage(WeatherNotification.class, this::onReadWeather).build();
    }

    private Behavior<INotification> onReadWeather(WeatherNotification value) {

        WeatherNotification weatherNotification = new WeatherNotification();
        weatherNotification.setWeather(value.getWeather());
        forwardTo.tell(weatherNotification);
        return Behaviors.same();
    }
}
