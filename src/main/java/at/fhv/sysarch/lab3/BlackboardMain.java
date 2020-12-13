package at.fhv.sysarch.lab3;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab3.actors.Actor;
import at.fhv.sysarch.lab3.airConditioning.AC;
import at.fhv.sysarch.lab3.airConditioning.ACNotification;
import at.fhv.sysarch.lab3.blinds.Blinds;
import at.fhv.sysarch.lab3.blinds.BlindsNotification;
import at.fhv.sysarch.lab3.environment.*;
import at.fhv.sysarch.lab3.mediaStation.MediaStation;
import at.fhv.sysarch.lab3.mediaStation.MediaStationNotification;

import java.util.HashMap;

public class BlackboardMain extends AbstractBehavior<INotification> {

    // save actor refs
    public HashMap<Actor, ActorRef> actorRefMap = new HashMap<>();
    public ActorStates actorStates = new ActorStates();

    public static class SendNotification implements INotification {
        public final Actor actor;
        public final String notification;

        public SendNotification(Actor actor, String notification) {
            this.actor = actor;
            this.notification = notification;
        }
    }

    public static Behavior<INotification> create() {
        return Behaviors.setup(BlackboardMain::new);
    }

    public BlackboardMain(ActorContext<INotification> context) {
        super(context);
        //#create-actors

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
                onMessage(WeatherNotification.class, this::onWeatherNotification).
                onMessage(BlindsNotification.class, this::onBlindsNotification).
                onMessage(MediaStationNotification.class, this::onMediaStationNotification).
                onMessage(ACNotification.class, this::onACNotification).build();
    }

    private Behavior<INotification> onSendNotification(SendNotification command) {


        return this;
    }

    private Behavior<INotification> onTempNotification(TempNotification command) {
        actorStates.setCurrentTemperature(command.getTemperature());

        if (command.getTemperature() >= 20) {
            TempNotification tempNotification = new TempNotification();
            tempNotification.acIsOn();
            tempNotification.setAcNotification();
            actorRefMap.get(Actor.TEMPERATURE_SIMULATOR).tell(tempNotification);
            actorRefMap.get(Actor.AC).tell(new ACNotification(AC.Actions.AC_ON.action));

        }
        if (command.getTemperature() < 20) {
            TempNotification tempNotification = new TempNotification();
            tempNotification.acIsOff();
            tempNotification.setAcNotification();
            actorRefMap.get(Actor.TEMPERATURE_SIMULATOR).tell(tempNotification);
            actorRefMap.get(Actor.AC).tell(new ACNotification(AC.Actions.AC_OFF.action));
        }

        return this;
    }

    private Behavior<INotification> onWeatherNotification(WeatherNotification command) {
        actorStates.setWeather(command.getWeather());

        if (command.getWeather() == Weather.SUNNY) {
            actorRefMap.get(Actor.BLINDS).tell(new BlindsNotification(Blinds.Actions.CLOSE.action));
        }
        if (command.getWeather() == Weather.CLOUDY) {
            actorRefMap.get(Actor.BLINDS).tell(new BlindsNotification(Blinds.Actions.OPEN.action));
        }

        return this;
    }

    private Behavior<INotification> onBlindsNotification(BlindsNotification command) {

        BlindsNotification blindsNotification = new BlindsNotification(command.action);

        if (actorStates.isMediaStationOn()) {
            blindsNotification.setMediaStationOn();
            blindsNotification.setMSNotification();
            actorRefMap.get(Actor.WEATHER_SIMULATOR).tell(blindsNotification);
        }
        blindsNotification.setMediaStationOff();
        blindsNotification.setMSNotification();
        actorRefMap.get(Actor.WEATHER_SIMULATOR).tell(blindsNotification);


        actorRefMap.get(Actor.BLINDS).tell(new BlindsNotification(command.action));


        if (command.action.equals(Blinds.Actions.OPEN.action)) {
            actorStates.setAreBlindsClosed(false);
        } else {
            actorStates.setAreBlindsClosed(false);
        }

        return this;
    }

    private Behavior<INotification> onMediaStationNotification(MediaStationNotification command) {

        actorRefMap.get(Actor.MEDIA_STATION).tell(new MediaStationNotification(command.action));

        return this;
    }

    private Behavior<INotification> onACNotification(ACNotification notification) {
        // should the user be able to turn ac on/off ? or just automatic
        return this;
    }
}
