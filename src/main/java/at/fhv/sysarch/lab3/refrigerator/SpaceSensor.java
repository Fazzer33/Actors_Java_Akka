package at.fhv.sysarch.lab3.refrigerator;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab3.INotification;

// Request-Response with ask between two actors
// https://doc.akka.io/docs/akka/current/typed/interaction-patterns.html

public class SpaceSensor extends AbstractBehavior<INotification> {
    public static final class ReturnNewSpace implements INotification {
        public final ActorRef<ReturnNewSpaceResponse> respondTo;

        public ReturnNewSpace(ActorRef<ReturnNewSpaceResponse> respondTo) {
            this.respondTo = respondTo;
        }
    }

    public static final class ReturnNewSpaceResponse {
        public final String message;

        public ReturnNewSpaceResponse(String message) {
            this.message = message;
        }
    }

    public SpaceSensor(ActorContext<INotification> context) {
        super(context);
    }

    @Override
    public Receive<INotification> createReceive() {
        return newReceiveBuilder().onMessage(ReturnNewSpace.class, this::onReturnNewSpace).build();
    }

    public static Behavior<INotification> create() {
        return Behaviors.setup(SpaceSensor::new);
    }

    private Behavior<INotification> onReturnNewSpace(ReturnNewSpace message) {
        message.respondTo.tell(new ReturnNewSpaceResponse("test of space sensor"));

        return this;
    }
}
