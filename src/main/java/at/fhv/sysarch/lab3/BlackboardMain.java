package at.fhv.sysarch.lab3;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab3.actors.Actor;
import at.fhv.sysarch.lab3.airConditioning.AC;
import at.fhv.sysarch.lab3.environment.*;

import java.util.HashMap;


public class BlackboardMain extends AbstractBehavior<INotification> {

    // save actor refs
    protected HashMap<Actor, ActorRef> actorRefMap = new HashMap<>();

    public static class SendNotification implements INotification{
        public final Actor actor;
        public final String notification;

        public SendNotification(Actor actor, String notification) {
            this.actor = actor;
            this.notification = notification;
        }
    }

    private final ActorRef<Notifier.Notify> notifier;

    public static Behavior<INotification> create() {
        return Behaviors.setup(BlackboardMain::new);
    }

    public BlackboardMain(ActorContext<INotification> context) {
        super(context);
        //#create-actors
        notifier = context.spawn(Notifier.create(), "notifier");

        actorRefMap.put(Actor.TEMPERATURE_SENSOR, getContext().spawn(TemperatureSensor.create(getContext().getSelf()), "temperatureSensor"));
        actorRefMap.put(Actor.TEMPERATURE_SIMULATOR, getContext().spawn(TemperatureSimulator.create(actorRefMap.get(Actor.TEMPERATURE_SENSOR)), "tempSimulator"));

        actorRefMap.put(Actor.WEATHER_SENSOR, getContext().spawn(WeatherSensor.create(getContext().getSelf()), "weatherSensor"));
        actorRefMap.put(Actor.WEATHER_SIMULATOR, getContext().spawn(WeatherSimulator.create(actorRefMap.get(Actor.WEATHER_SENSOR)), "weatherSimulator"));

        actorRefMap.put(Actor.MEDIA_STATION, getContext().spawn(MediaStation.create(), "mediaStation"));
        actorRefMap.put(Actor.BLINDS, getContext().spawn(Blinds.create(), "blinds"));
        actorRefMap.put(Actor.AC, getContext().spawn(AC.create(), "ac"));

        //#create-actors
    }

    @Override
    public Receive<INotification> createReceive() {
        return newReceiveBuilder().onMessage(SendNotification.class, this::onSendNotification).
                onMessage(TempNotification.class, this::onTempNotification).
                onMessage(WeatherNotification.class, this::onWeatherNotification).build();
    }

    private Behavior<INotification> onSendNotification(SendNotification command) {
        //#create-actors
        // MEDIA_STATION
        if (command.actor == Actor.MEDIA_STATION) {
            notifier.tell(new Notifier.Notify(command.notification, actorRefMap.get(Actor.MEDIA_STATION)));
        }

        //BLINDS
        if (command.actor == Actor.BLINDS) {
            notifier.tell(new Notifier.Notify(command.notification, actorRefMap.get(Actor.BLINDS)));
        }
        //#create-actors
        return this;
    }

    private Behavior<INotification> onTempNotification(TempNotification command) {

        if (command.getTemperature() <= 20) {
            notifier.tell(new Notifier.Notify(AC.Actions.AC_ON.action, actorRefMap.get(Actor.AC)));
        }
        if (command.getTemperature() > 20) {
            // turn on AC and start cooling
            notifier.tell(new Notifier.Notify(AC.Actions.AC_OFF.action, actorRefMap.get(Actor.AC)));
        }

        return this;
    }

    private Behavior<INotification> onWeatherNotification(WeatherNotification command) {

        if (command.getWeather() == Weather.SUNNY) {
            notifier.tell(new Notifier.Notify(Blinds.Actions.CLOSE.action, actorRefMap.get(Actor.BLINDS)));
        }
        if (command.getWeather() == Weather.CLOUDY) {
            // TODO: only open if there is no movie running
            notifier.tell(new Notifier.Notify(Blinds.Actions.OPEN.action, actorRefMap.get(Actor.BLINDS)));
        }

        return this;
    }
}
