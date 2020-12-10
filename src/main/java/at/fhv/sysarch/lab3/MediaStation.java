package at.fhv.sysarch.lab3;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab3.actors.Actor;

public class MediaStation extends AbstractBehavior<Notifier.Notified> {
    private Actor actor;
    private boolean isPlaying = false;

    public static Behavior<Notifier.Notified> create() {
        return Behaviors.setup(MediaStation::new);
    }

    public MediaStation(ActorContext<Notifier.Notified> context) {
        super(context);
        this.actor = Actor.MEDIA_STATION;
    }

    @Override
    public Receive<Notifier.Notified> createReceive() {
        return newReceiveBuilder().onMessage(Notifier.Notified.class, this::onNotified).build();
    }

    private Behavior<Notifier.Notified> onNotified(Notifier.Notified message) {
        if (message.whom.equals("stop")) {
            isPlaying = false;
            return Behaviors.stopped();
        } else if (message.whom.equals("movie") && !isPlaying) {
            isPlaying = true;
            getContext().getLog().info(message.whom);
            message.from.tell(new Notifier.Notify(message.whom, getContext().getSelf()));
            return this;
        } else {
            System.out.println("movie already playing");
            return this;
        }
    }
}
