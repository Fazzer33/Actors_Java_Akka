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

public class ProductSensor extends AbstractBehavior<INotification> {

    public static final class ReturnCurrentAmount implements INotification {
        public final ActorRef<ReturnStoredProductsResponse> respondTo;
        private int newProductAmount = 0;

        public ReturnCurrentAmount(ActorRef<ReturnStoredProductsResponse> respondTo, HashMap<ProductType, Pair<Product, Integer>> products,
                                   int currentProductsAmount) {
            newProductAmount = currentProductsAmount;
            this.respondTo = respondTo;
            for (ProductType productType : products.keySet()) {
                newProductAmount += products.get(productType).second();
            }

        }
    }

    public static final class ReturnStoredProductsResponse {
        public final int newAmount;

        public ReturnStoredProductsResponse(int newAmount) {
            this.newAmount = newAmount;
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

        message.respondTo.tell(new ReturnStoredProductsResponse(message.newProductAmount));

        return this;
    }
}
