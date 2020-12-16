package at.fhv.sysarch.lab3.environment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.DispatcherSelector;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab3.INotification;

import java.time.Duration;
import java.util.Random;

public class WeatherSimulator extends AbstractBehavior<WeatherNotification> {

    private ActorRef<INotification> forwardTo;

    public static Behavior<WeatherNotification> create(ActorRef<INotification> replyTo){
        return Behaviors.setup(context -> new WeatherSimulator(context, replyTo));
    }

    public WeatherSimulator(ActorContext<WeatherNotification> context, ActorRef<INotification> forwardTo) {
        super(context);
        this.forwardTo = forwardTo;
        onReadWeather(new WeatherNotification());
    }

    @Override
    public Receive<WeatherNotification> createReceive() {
        return newReceiveBuilder().onMessage(WeatherNotification.class, this::onReadWeather).build();
    }

    private Behavior<WeatherNotification> onReadWeather(WeatherNotification value){

        WeatherNotification weatherNotification = new WeatherNotification();
        Weather weather = getRandomWeather();
        weatherNotification.setWeather(weather);

        getContext().getSystem().scheduler().scheduleOnce(
                Duration.ofMillis(15000),
                () -> getContext().getSelf().tell(weatherNotification),
                getContext().getSystem().dispatchers().lookup(DispatcherSelector.defaultDispatcher()));

        value.setWeather(weather);
        forwardTo.tell(value);

        System.out.println(weather);

        return Behaviors.same();
    }

    private Weather getRandomWeather() {
        Random random = new Random();

        if(random.nextBoolean()) {
            return Weather.SUNNY;
        } else {
            return Weather.CLOUDY;
        }
    }
}
