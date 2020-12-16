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

public class OrderProcessor extends AbstractBehavior<OrderNotification> {
    private ActorRef<INotification> forwardTo;
    private HashMap<ProductType, Pair<Product, Integer>> productMap = new HashMap<>();
    private double orderWeight = 0;

    public OrderProcessor(ActorContext<OrderNotification> context, ActorRef<INotification> forwardTo) {
        super(context);
        this.forwardTo = forwardTo;
    }

    @Override
    public Receive<OrderNotification> createReceive() {
        return newReceiveBuilder().onMessage(OrderNotification.class, this::onNotified).build();
    }

    public static Behavior<OrderNotification> create(ActorRef<INotification> forwardTo) {
        return Behaviors.setup(context -> new OrderProcessor(context, forwardTo));
    }

    private Behavior<OrderNotification> onNotified(OrderNotification notification) {
        for (ProductType key : notification.orderMap.keySet()) {
            Product product = new Product(key);
            orderWeight = orderWeight + (product.getWeight() * notification.orderMap.get(key));
            productMap.put(product.getType(), new Pair<>(product, notification.orderMap.get(key)));
        }

        forwardTo.tell(new FridgeNotification(productMap, orderWeight));
        return Behaviors.stopped();
    }
}
