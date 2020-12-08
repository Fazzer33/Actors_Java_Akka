package at.fhv.sysarch.lab3;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class BlackboardMain extends AbstractBehavior<BlackboardMain.SendNotification> {

    public static class SendNotification {
        public final String notification;

        public SendNotification(String notification) {
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
        ActorRef<Notifier.Notified> forwardTo =
                getContext().spawn(MediaStation.create(), command.notification);
        notifier.tell(new Notifier.Notify(command.notification, forwardTo));
        //#create-actors
        return this;
    }
}
