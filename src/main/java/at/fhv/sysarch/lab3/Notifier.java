package at.fhv.sysarch.lab3;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Notifier extends AbstractBehavior<Notifier.Notify> {

    public Notifier(ActorContext<Notify> context) {
        super(context);
    }

    @Override
    public Receive<Notify> createReceive() {
        return newReceiveBuilder().onMessage(Notify.class, this::onNotify).build();
    }

    public static final class Notify {
        public final String whom;
        public final ActorRef<Notified> forwardTo;

        public Notify(String whom, ActorRef<Notified> forwardTo) {
            this.whom = whom;
            this.forwardTo = forwardTo;
        }
    }

    public static final class Notified {
        public final String whom;
        public final ActorRef<Notify> from;

        public Notified(String whom, ActorRef<Notify> from) {
            this.whom = whom;
            this.from = from;
        }
    }


    public static Behavior<Notify> create() {
        return Behaviors.setup(Notifier::new);
    }

    private Behavior<Notify> onNotify(Notify command) {

        command.forwardTo.tell(new Notified(command.whom, getContext().getSelf()));
        return this;
    }


}
