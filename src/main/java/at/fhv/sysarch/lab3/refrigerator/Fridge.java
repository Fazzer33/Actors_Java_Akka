package at.fhv.sysarch.lab3.refrigerator;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.japi.Pair;
import at.fhv.sysarch.lab3.INotification;
import at.fhv.sysarch.lab3.actors.Actor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

// In this case the fridge should relay this request to a separate OrderProcessor actor (see Per session child Actor).
// https://doc.akka.io/docs/akka/current/typed/interaction-patterns.html

public class Fridge extends AbstractBehavior<INotification> {
    private ActorRef<ProductSensor.Command> productSensor;
    private ActorRef<FridgeNotification> spaceSensor;

    private Actor actor;
    private boolean isEmpty = false;
    private final int MAX_PRODUCTS = 30;
    private final double MAX_WEIGHT = 60;
    private double currentWeight = 0;
    private int currentProducts = 0;
    private int orderCounter = 0;
    private List<ProductType> essentialProducts = new LinkedList<>();
    private HashMap<ProductType, Pair<Product,Integer>> productsInFridge = new HashMap<>();
    private List<HashMap<ProductType, Pair<Product,Integer>>> previousOrders = new LinkedList<>();

    public static Behavior<INotification> create() {
        return Behaviors.setup(Fridge::new);
    }

    public Fridge(ActorContext<INotification> context) {
        super(context);
        actor = Actor.FRIDGE;
        // essential products get reordered if empty
        essentialProducts.add(ProductType.APPLE);
        essentialProducts.add(ProductType.MILK);

        // initial products in fridge
        productsInFridge.put(ProductType.APPLE, new Pair<>(new Product(ProductType.APPLE), 5));
        productsInFridge.put(ProductType.MILK, new Pair<>(new Product(ProductType.APPLE), 5));

        // add fridge sensors
        productSensor = getContext().spawn(ProductSensor.create(), "productSensor");
        spaceSensor = getContext().spawn(SpaceSensor.create(), "spaceSensor");
    }

    @Override
    public Receive<INotification> createReceive() {
        return newReceiveBuilder().
                onMessage(FridgeNotification.class, this::onNotified).
                onMessage(ConsumeNotification.class, this::onConsume).build();
    }

    private Behavior<INotification> onNotified(FridgeNotification notification) {
        int size = 0;
        for (Pair i : notification.productMap.values()) {
            size += (int) i.second();
        }

        System.out.println(size);

        if (size <= (MAX_PRODUCTS - currentProducts)
                && notification.orderWeight <= (MAX_WEIGHT - currentWeight)) {
            System.out.println("Order gets placed into the fridge");
            productsInFridge = notification.productMap;
            previousOrders.add(notification.productMap);

        } else {
            System.out.println("Too much weight or too less space in the fridge");
            System.out.println("Available Weight: " + (MAX_WEIGHT - currentWeight));
            System.out.println("Available Space: " + (MAX_PRODUCTS - currentProducts));
            System.out.println();
            System.out.println("Order: Weight: " + notification.orderWeight);
            System.out.println("Space: " + size);
        }
        return this;
    }

    private Behavior<INotification> onConsume(ConsumeNotification notification) {
        ProductType type = notification.product.first();
        if (productsInFridge.get(type) != null) {
            int amount = productsInFridge.get(type).second();
            int consumeAmount = notification.product.second();

            if (productsInFridge.get(notification.product.first()) == null || amount < consumeAmount) {
                System.out.println("Too less "+type.name());
                System.out.println("Amount in fridge: "+amount);
            } else {
                amount = amount - consumeAmount;
                if (amount == 0) {
                    // remove if product from map if empty
                    productsInFridge.remove(type);
                    if (essentialProducts.contains(type)) {
                       reorderEmptyProduct(type);
                    }
                } else {
                    // new entry in map with updated amount
                    productsInFridge.put(type, new Pair<>(new Product(type), amount));
                }
            }
        } else {
            System.out.println("Product is not in the fridge");
        }
        return this;
    }

    /**
     * Reorders an Product if nothing is left in the fridge
     * @param type type of the product
     */
    private void reorderEmptyProduct(ProductType type) {
        System.out.println("Reorder 5 * "+type +" because nothing left in fridge");
        HashMap<ProductType, Integer> orderMap = new HashMap();
        orderMap.put(type, 5);
        getContext().spawn(OrderProcessor.create(getContext().getSelf()), "re-order"+orderCounter).tell(new OrderNotification(orderMap));
        orderCounter++;
    }
}
