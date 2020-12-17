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

public class ProductSensor extends AbstractBehavior<INotification> {

    public static final class ReturnCurrentAmount implements INotification {
        public final ActorRef<ReturnStoredProductsResponse> respondTo;

        public ReturnCurrentAmount(ActorRef<ReturnStoredProductsResponse> respondTo) {
            this.respondTo = respondTo;
        }
    }

    public static final class ReturnStoredProductsResponse {
        public final String message;

        public ReturnStoredProductsResponse(String message) {
            this.message = message;
        }
    }

    public ProductSensor(ActorContext<INotification> context) {
        super(context);
    }

    @Override
    public Receive<INotification> createReceive() {
        return newReceiveBuilder().onMessage(ReturnCurrentAmount.class, this::onReturnStoredProducts).build();
    }

    public static Behavior<INotification> create() {
        return Behaviors.setup(ProductSensor::new);
    }

    private Behavior<INotification> onReturnStoredProducts(ReturnCurrentAmount message) {
        message.respondTo.tell(new ReturnStoredProductsResponse("test of product sensor"));

        return this;
    }
}
