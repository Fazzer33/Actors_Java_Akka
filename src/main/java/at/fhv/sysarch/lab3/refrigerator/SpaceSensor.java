package at.fhv.sysarch.lab3.refrigerator;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.japi.Pair;
import at.fhv.sysarch.lab3.INotification;

import java.util.HashMap;

// Request-Response with ask between two actors
// https://doc.akka.io/docs/akka/current/typed/interaction-patterns.html

public class SpaceSensor extends AbstractBehavior<INotification> {
    public static final class ReturnNewSpace implements INotification {
        public final ActorRef<ReturnNewSpaceResponse> respondTo;
        private double newWeight = 0;

        public ReturnNewSpace(ActorRef<ReturnNewSpaceResponse> respondTo, HashMap<ProductType, Pair<Product, Integer>> products,
                              double currentWeight) {
            this.respondTo = respondTo;
            newWeight = currentWeight;
            for (ProductType productType : products.keySet()) {
                newWeight += products.get(productType).first().getWeight() * products.get(productType).second();
            }
        }
    }

    public static final class ReturnNewSpaceResponse {
        public final double newWeight;

        public ReturnNewSpaceResponse(double newWeight) {
            this.newWeight = newWeight;
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
        message.respondTo.tell(new ReturnNewSpaceResponse(message.newWeight));

        return this;
    }
}
