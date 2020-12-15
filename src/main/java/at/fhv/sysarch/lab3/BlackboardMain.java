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
import at.fhv.sysarch.lab3.refrigerator.*;

import java.util.HashMap;

public class BlackboardMain extends AbstractBehavior<INotification> {

    // save actor refs
    public HashMap<Actor, ActorRef> actorRefMap = new HashMap<>();
    public ActorStates actorStates = new ActorStates();
    private int orderCounter = 1;

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

        actorRefMap.put(Actor.FRIDGE, getContext().spawn(Fridge.create(), "fridge"));
        //#create-actors
    }

    @Override
    public Receive<INotification> createReceive() {
        return newReceiveBuilder().
                onMessage(TempNotification.class, this::onTempNotification).
                onMessage(WeatherNotification.class, this::onWeatherNotification).
                onMessage(BlindsNotification.class, this::onBlindsNotification).
                onMessage(MediaStationNotification.class, this::onMediaStationNotification).
                onMessage(ACNotification.class, this::onACNotification).
                onMessage(OrderNotification.class, this::onOrderNotification).
                onMessage(ConsumeNotification.class, this::onConsumeNotification).build();
    }

    private Behavior<INotification> onTempNotification(TempNotification command) {
        actorStates.setCurrentTemperature(command.getTemperature());

        System.out.println(command.getTemperature());

        if (command.getTemperature() >= 20) {
            TempNotification tempNotification = new TempNotification();
            tempNotification.acIsOn();
            tempNotification.setAcNotification();
            tempNotification.setTemperature(command.getTemperature());
            actorRefMap.get(Actor.TEMPERATURE_SIMULATOR).tell(tempNotification);
            actorRefMap.get(Actor.AC).tell(new ACNotification(AC.Actions.AC_ON.action));

        }
        if (command.getTemperature() < 20) {
            TempNotification tempNotification = new TempNotification();
            tempNotification.acIsOff();
            tempNotification.setAcNotification();
            tempNotification.setTemperature(command.getTemperature());
            actorRefMap.get(Actor.TEMPERATURE_SIMULATOR).tell(tempNotification);
            actorRefMap.get(Actor.AC).tell(new ACNotification(AC.Actions.AC_OFF.action));
        }

        return this;
    }

    private Behavior<INotification> onWeatherNotification(WeatherNotification command) {
        actorStates.setWeather(command.getWeather());

        if (command.getWeather() == Weather.SUNNY && !actorStates.isMediaStationOn()) {
            actorRefMap.get(Actor.BLINDS).tell(new BlindsNotification(Blinds.Actions.CLOSE.action));
        }
        if (command.getWeather() == Weather.CLOUDY && !actorStates.isMediaStationOn()) {
            actorRefMap.get(Actor.BLINDS).tell(new BlindsNotification(Blinds.Actions.OPEN.action));
        }

        return this;
    }

    private Behavior<INotification> onBlindsNotification(BlindsNotification command) {

        System.out.println(command.action);
        BlindsNotification blindsNotification = new BlindsNotification(command.action);

        if (actorStates.isMediaStationOn()) {
            blindsNotification.setMediaStationOn();
            blindsNotification.setMSNotification();
            actorRefMap.get(Actor.BLINDS).tell(blindsNotification);
        }

        actorRefMap.get(Actor.BLINDS).tell(blindsNotification);

        if (command.action.equals(Blinds.Actions.OPEN.action)) {
            actorStates.setAreBlindsClosed(false);
        } else {
            actorStates.setAreBlindsClosed(true);
        }

        return this;
    }

    private Behavior<INotification> onMediaStationNotification(MediaStationNotification command) {
        if (command.action.equals(MediaStation.Actions.START.action)) {
            actorStates.setMediaStationOn(true);
        } else {
            actorStates.setMediaStationOn(false);
        }
        actorRefMap.get(Actor.MEDIA_STATION).tell(new MediaStationNotification(command.action));

        return this;
    }

    private Behavior<INotification> onACNotification(ACNotification notification) {
        // should the user be able to turn ac on/off ? or just automatic
        return this;
    }

    private Behavior<INotification> onOrderNotification(OrderNotification notification) {
        getContext().spawn(OrderProcessor.create(actorRefMap.get(Actor.FRIDGE)), "order"+orderCounter).tell(new OrderNotification(notification.orderMap));
        orderCounter++;
        return this;
    }

    private Behavior<INotification> onConsumeNotification(ConsumeNotification notification) {
        actorRefMap.get(Actor.FRIDGE).tell(new ConsumeNotification(notification.product));
        return this;
    }
}
