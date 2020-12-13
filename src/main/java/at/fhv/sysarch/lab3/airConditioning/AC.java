package at.fhv.sysarch.lab3.airConditioning;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab3.Notifier;
import at.fhv.sysarch.lab3.actors.Actor;

public class AC extends AbstractBehavior<Notifier.Notified> {
    public enum Actions {
        AC_ON("acOn"), AC_OFF("acOff");

        public final String action;
        Actions(String action) {
            this.action = action;
        }
    }

    private boolean running = false;
    private Actor actor;

    public AC(ActorContext<Notifier.Notified> context) {
        super(context);
        this.actor = Actor.AC;
    }

    public static Behavior<Notifier.Notified> create() {
        return Behaviors.setup(AC::new);
    }

    @Override
    public Receive<Notifier.Notified> createReceive() {
        return newReceiveBuilder().onMessage(Notifier.Notified.class, this::onReceiveNotification).build();
    }

    private Behavior<Notifier.Notified> onReceiveNotification(Notifier.Notified command) {
        if (command.whom.equals(Actions.AC_ON.action) && !running) {
            running = true;
            System.out.println("AC is running now...");
            return this;
        }  else if (command.whom.equals(Actions.AC_OFF.action) && running) {
            running = false;
            System.out.println("AC turning off...");
            return this;
        } else {
            return this;
        }
    }
}
