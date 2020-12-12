package at.fhv.sysarch.lab3;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab3.actors.Actor;
import at.fhv.sysarch.lab3.environment.TemperatureSimulator;
import java.util.HashMap;


public class BlackboardMain extends AbstractBehavior<BlackboardMain.SendNotification> {

    // save actor ref
    protected HashMap<Actor, ActorRef> actorRefMap = new HashMap<>();

    public static class SendNotification {
        public final Actor actor;
        public final String notification;

        public SendNotification(Actor actor, String notification) {
            this.actor = actor;
            this.notification = notification;
        }
    }

    private final ActorRef<Notifier.Notify> notifier;

    public static Behavior<SendNotification> create() {
        return Behaviors.setup(BlackboardMain::new);
    }

    public BlackboardMain(ActorContext<SendNotification> context) {
        super(context);
        //#create-actors
        notifier = context.spawn(Notifier.create(), "notifier");
        //#create-actors
    }

    @Override
    public Receive<SendNotification> createReceive() {
        return newReceiveBuilder().onMessage(SendNotification.class, this::onSendNotification).build();
    }

    private Behavior<SendNotification> onSendNotification(SendNotification command) {
        //#create-actors
        // MEDIA_STATION
        if (command.actor == Actor.MEDIA_STATION) {
            if (actorRefMap.get(Actor.MEDIA_STATION) == null) {
                System.out.println("Media station don't exist");
                ActorRef<Notifier.Notified> forwardTo =
                        getContext().spawn(MediaStation.create(), command.notification);
                actorRefMap.put(Actor.MEDIA_STATION, forwardTo);

            } else if (actorRefMap.get(Actor.MEDIA_STATION) != null){
                System.out.println("Media station exists");
                notifier.tell(new Notifier.Notify(command.notification, actorRefMap.remove(Actor.MEDIA_STATION)));
            }
        }

        //BLINDS
        if (command.actor == Actor.BLINDS) {
            if (actorRefMap.get(Actor.BLINDS) == null) {
                System.out.println("Blinds don't exist");
                ActorRef<Notifier.Notified> forwardTo =
                        getContext().spawn(Blinds.create(), command.notification);
                actorRefMap.put(Actor.BLINDS, forwardTo);

            } else if (actorRefMap.get(Actor.BLINDS) != null){
                System.out.println("Blinds exists");
                notifier.tell(new Notifier.Notify(command.notification, actorRefMap.remove(Actor.BLINDS)));
            }
        }
        
        if (command.actor == Actor.TEMPERATURE_SIMULATOR) {
            ActorRef<Notifier.Notified> forwardTo =
                    getContext().spawn(TemperatureSimulator.create(), command.notification);
            notifier.tell(new Notifier.Notify(command.notification, forwardTo));
        }
        //#create-actors
        return this;
    }
}
