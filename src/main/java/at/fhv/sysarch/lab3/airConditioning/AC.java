package at.fhv.sysarch.lab3.airConditioning;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab3.actors.Actor;

public class AC extends AbstractBehavior<ACNotification> {
    public enum Actions {
        AC_ON("acOn"), AC_OFF("acOff");

        public final String action;
        Actions(String action) {
            this.action = action;
        }
    }

    private boolean running = false;
    private Actor actor;

    public AC(ActorContext<ACNotification> context) {
        super(context);
        this.actor = Actor.AC;
    }

    public static Behavior<ACNotification> create() {
        return Behaviors.setup(AC::new);
    }

    @Override
    public Receive<ACNotification> createReceive() {
        return newReceiveBuilder().onMessage(ACNotification.class, this::onReceiveNotification).build();
    }

    private Behavior<ACNotification> onReceiveNotification(ACNotification command) {
        if (command.action.equals(Actions.AC_ON.action) && !running) {
            running = true;
            System.out.println("AC is running now...");
            return this;
        }  else if (command.action.equals(Actions.AC_OFF.action) && running) {
            running = false;
            System.out.println("AC turning off...");
            return this;
        } else {
            return this;
        }
    }
}
