package at.fhv.sysarch.lab3.refrigerator;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.List;

// Request-Response with ask between two actors
// The user orders products.
// https://doc.akka.io/docs/akka/current/typed/interaction-patterns.html

public class ProductSensor extends AbstractBehavior<ProductSensor.Command> {
    private List<Product> productList;

    public interface Command{}

    private static final class ReturnStoredProducts implements Command {
        public final ActorRef<ReturnStoredProductsResponse> respondTo;

        public ReturnStoredProducts(ActorRef<ReturnStoredProductsResponse> respondTo) {
            this.respondTo = respondTo;
        }
    }

    public static final class ReturnStoredProductsResponse {
        public final List<Product> products;

        public ReturnStoredProductsResponse(List<Product> products) {
            this.products = products;
        }
    }

    public ProductSensor(ActorContext<ProductSensor.Command> context) {
        super(context);
    }

    @Override
    public Receive<ProductSensor.Command> createReceive() {
        return newReceiveBuilder().onMessage(ReturnStoredProducts.class, this::onReturnStoredProducts).build();
    }

    public static Behavior<ProductSensor.Command> create() {
        return Behaviors.setup(ProductSensor::new);
    }

    private Behavior<Command> onReturnStoredProducts(ReturnStoredProducts message) {

        message.respondTo.tell(new ReturnStoredProductsResponse(this.productList));
        return this;
    }
}
