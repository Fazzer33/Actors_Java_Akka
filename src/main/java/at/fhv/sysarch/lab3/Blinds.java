package at.fhv.sysarch.lab3;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab3.actors.Actor;

public class Blinds extends AbstractBehavior<Notifier.Notified> {
    public enum Actions {
        OPEN("open"), CLOSE("close");

        public final String action;
        Actions(String action) {
            this.action = action;
        }
    }

    private Actor actor;
    private boolean areOpen = true;

    public static Behavior<Notifier.Notified> create() {
        return Behaviors.setup(Blinds::new);
    }

    public Blinds(ActorContext<Notifier.Notified> context) {
        super(context);
        this.actor = Actor.BLINDS;
    }

    @Override
    public Receive<Notifier.Notified> createReceive() {
        return newReceiveBuilder().onMessage(Notifier.Notified.class, this::onNotified).build();
    }

    private Behavior<Notifier.Notified> onNotified(Notifier.Notified message) {
        if (message.whom.equals(Actions.CLOSE.action) && areOpen) {
            areOpen = false;
            getContext().getLog().info(message.whom);
            System.out.println("Blinds are getting closed");
            message.from.tell(new Notifier.Notify(message.whom, getContext().getSelf()));
            return this;

        } else if (message.whom.equals(Actions.OPEN.action)){
            System.out.println("Blinds are getting opened");
            return Behaviors.stopped();
        } else {
            System.out.println("Blinds are already closed");
            return this;
        }
    }
}
