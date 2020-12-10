package at.fhv.sysarch.lab3;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab3.actors.Actor;

public class MediaStation extends AbstractBehavior<Notifier.Notified> {
    private Actor actor;
    private int notifyCounter;

    public static Behavior<Notifier.Notified> create() {
        return Behaviors.setup(context -> new MediaStation(context));
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
        notifyCounter++;
        getContext().getLog().info(message.whom);
        if (notifyCounter == 1) {
            return Behaviors.stopped();
        } else {
            message.from.tell(new Notifier.Notify(message.whom, getContext().getSelf()));
            return this;
        }
    }
}
