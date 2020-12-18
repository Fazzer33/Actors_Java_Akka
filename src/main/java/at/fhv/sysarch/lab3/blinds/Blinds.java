package at.fhv.sysarch.lab3.blinds;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab3.actors.Actor;

public class Blinds extends AbstractBehavior<BlindsNotification> {
    public enum Actions {
        OPEN("open"), CLOSE("close");

        public final String action;
        Actions(String action) {
            this.action = action;
        }
    }

    private Actor actor;
    private boolean areOpen = true;
    private boolean isMediaStationOn = false;

    public static Behavior<BlindsNotification> create() {
        return Behaviors.setup(Blinds::new);
    }

    public Blinds(ActorContext<BlindsNotification> context) {
        super(context);
        this.actor = Actor.BLINDS;
    }

    @Override
    public Receive<BlindsNotification> createReceive() {
        return newReceiveBuilder().onMessage(BlindsNotification.class, this::onNotified).build();
    }

    private Behavior<BlindsNotification> onNotified(BlindsNotification message) {
        if (message.checkOnMSNotification()) {
            isMediaStationOn = message.checkIfMediaStationOn();
        }
        if (message.action.equals(Actions.CLOSE.action) && areOpen) {
            areOpen = false;
//            getContext().getLog().info(message.action);
            System.out.println("Blinds are getting closed");
            return this;

        } else if (message.action.equals(Actions.OPEN.action) && !isMediaStationOn && !areOpen){
            System.out.println("Blinds are getting opened");
//            getContext().getLog().info(message.action);
            areOpen = true;
            return this;
        } else {
            return this;
        }
    }
}
