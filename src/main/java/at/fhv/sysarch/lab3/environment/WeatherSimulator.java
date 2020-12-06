package at.fhv.sysarch.lab3.environment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;

public class WeatherSimulator extends AbstractBehavior<Weather> {

    private ActorRef<Weather> weatherSensor;

    public WeatherSimulator(ActorContext<Weather> context, ActorRef<Weather> weather) {
        super(context);
        this.weatherSensor = weather;
    }

    @Override
    public Receive<Weather> createReceive() {
        return null;
    }
}
