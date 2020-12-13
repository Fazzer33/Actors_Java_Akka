package at.fhv.sysarch.lab3.mediaStation;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab3.actors.Actor;

public class MediaStation extends AbstractBehavior<MediaStationNotification> {
   public enum Actions {
       START("start_film"), TURN_OFF("end");

       public final String action;
       Actions(String action) {
           this.action = action;
       }
   }

    private Actor actor;
    private boolean isPlaying = false;

    public static Behavior<MediaStationNotification> create() {
        return Behaviors.setup(MediaStation::new);
    }

    public MediaStation(ActorContext<MediaStationNotification> context) {
        super(context);
        this.actor = Actor.MEDIA_STATION;
    }

    @Override
    public Receive<MediaStationNotification> createReceive() {
        return newReceiveBuilder().onMessage(MediaStationNotification.class, this::onNotified).build();
    }

    private Behavior<MediaStationNotification> onNotified(MediaStationNotification notification) {
        System.out.println("test");
        System.out.println(notification.action);
        if (notification.action.equals(Actions.TURN_OFF.action) && isPlaying) {
            isPlaying = false;
            System.out.println("MediaStation turning off...");
            getContext().getLog().info(notification.action);
            return this;
        } else if (notification.action.equals(Actions.START.action) && !isPlaying) {
            isPlaying = true;
            System.out.println("MediaStation is starting...");
            getContext().getLog().info(notification.action);
            return this;
        } else {
            return this;
        }
    }
}
