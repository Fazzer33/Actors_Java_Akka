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

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Fridge extends AbstractBehavior<INotification> {

    private ActorRef<INotification> productSensor;
    private ActorRef<INotification> spaceSensor;

    private Actor actor;
    private boolean isEmpty = false;
    private final int MAX_PRODUCTS = 60;
    private final double MAX_WEIGHT = 60;
    private double currentWeight = 0;
    private int currentProductAmount = 0;
    private int orderCounter = 0;
    private List<ProductType> essentialProducts = new LinkedList<>();
    private HashMap<ProductType, Pair<Product,Integer>> productsInFridge = new HashMap<>();
    private List<Receipt> previousOrders = new LinkedList<>();

    public static Behavior<INotification> create() {
        return Behaviors.setup(Fridge::new);
    }

    public Fridge(ActorContext<INotification> context) {
        super(context);
        actor = Actor.FRIDGE;
        // essential products get reordered if empty
        essentialProducts.add(ProductType.APPLE);
        essentialProducts.add(ProductType.MILK);
        essentialProducts.add(ProductType.EGGS);
        essentialProducts.add(ProductType.HAM);

        // add fridge sensors
        productSensor = getContext().spawn(ProductSensor.create(), "productSensor");
        spaceSensor = getContext().spawn(SpaceSensor.create(), "spaceSensor");
    }

    @Override
    public Receive<INotification> createReceive() {
        return newReceiveBuilder().
                onMessage(FridgeOrderNotification.class, this::onFridgeOrderNotification).
                onMessage(ConsumeNotification.class, this::onConsume).
                onMessage(FridgeStatusNotification.class, this::onStatusNotification).
                onMessage(ProductSensorResponse.class, this::onProductsSensorResponse).
                onMessage(SpaceSensorResponse.class, this::onSpaceSensorResponse).build();
    }

    private static final class ProductSensorResponse implements INotification {
        public final int newAmount;

        public ProductSensorResponse(int newAmount) {
            this.newAmount = newAmount;
        }
    }

    private static final class SpaceSensorResponse implements INotification {
        public final double newWeight;

        public SpaceSensorResponse(double newWeight) {
            this.newWeight = newWeight;
        }
    }

    private Behavior<INotification> onFridgeOrderNotification(FridgeOrderNotification notification) {
        int size = 0;
        for (Pair i : notification.productMap.values()) {
            size += (int) i.second();
        }

        if (size <= (MAX_PRODUCTS - currentProductAmount)
                && notification.orderWeight <= (MAX_WEIGHT - currentWeight)) {
            System.out.println("Order gets placed into the fridge");
            for (ProductType product : notification.productMap.keySet()) {
                if (productsInFridge.get(product) != null) {
                    int oldValue = productsInFridge.get(product).second();
                    productsInFridge.put(product, new Pair(notification.productMap.get(product).first(),
                            oldValue + notification.productMap.get(product).second()));
                } else {
                    productsInFridge.put(product, notification.productMap.get(product));
                }
            }

            Receipt receipt = createReceipt(notification.productMap);
            receipt.printAllPrizes();
            final Duration timeout = Duration.ofSeconds(3);
            getContext().ask(
                    ProductSensor.ReturnStoredProductsResponse.class,
                    productSensor,
                    timeout,
                    (ActorRef<ProductSensor.ReturnStoredProductsResponse> ref) ->
                            new ProductSensor.ReturnCurrentAmount(ref, notification.productMap, currentProductAmount),
                    // adapt the response (or failure to respond)
                    (response, throwable) -> {
                        if (response != null) {
                            return new ProductSensorResponse(response.newAmount);
                        } else {
                            return new ProductSensorResponse(currentProductAmount);
                        }
                    });
            getContext().ask(
                    SpaceSensor.ReturnNewSpaceResponse.class,
                    spaceSensor,
                    timeout,
                    (ActorRef<SpaceSensor.ReturnNewSpaceResponse> ref) ->
                            new SpaceSensor.ReturnNewSpace(ref, notification.productMap, currentWeight),
                    // adapt the response (or failure to respond)
                    (response, throwable) -> {
                        if (response != null) {
                            return new SpaceSensorResponse(response.newWeight);
                        } else {
                            return new SpaceSensorResponse(currentWeight);
                        }
                    });

            previousOrders.add(receipt);

        } else {
            System.out.println("Too much weight or too less space in the fridge");
            System.out.println("Available Weight: " + (MAX_WEIGHT - currentWeight));
            System.out.println("Available Space: " + (MAX_PRODUCTS - currentProductAmount));
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

    private Behavior<INotification> onStatusNotification(FridgeStatusNotification notification) {
        if (notification.action.equals(FridgeStatusNotification.Actions.CURRENT_PRODUCTS.action)) {
            System.out.println("Products in fridge:");
            for (ProductType type : productsInFridge.keySet()) {
                String key = type.toString();
                int value = productsInFridge.get(type).second();
                System.out.println("Product: "+key +" - Amount: "+value);
            }
        }
        else if (notification.action.equals(FridgeStatusNotification.Actions.ORDER_HISTORY.action)) {
            System.out.println("Order History: ");
            int i = 1;
            for (Receipt receipt : previousOrders) {
                System.out.println("Order "+i +":");
                receipt.printAllPrizes();
                i++;
                System.out.println();
            }
        }
        return this;
    }

    /**
     * Reorders an Product if nothing is left in the fridge
     * @param type type of the product
     */
    private void reorderEmptyProduct(ProductType type) {
        System.out.println("Re-order 5 * "+type +" because nothing left in fridge");
        HashMap<ProductType, Integer> orderMap = new HashMap();
        orderMap.put(type, 5);
        getContext().spawn(OrderProcessor.create(getContext().getSelf()), "re-order"+orderCounter).tell(new OrderNotification(orderMap));
        orderCounter++;
    }

    private Receipt createReceipt(HashMap<ProductType, Pair<Product, Integer>> productMap) {
        List<Pair<Product, Integer>> products = new LinkedList<>();

        for (ProductType type : productMap.keySet()){
            Pair<Product, Integer> pair = productMap.get(type);
            products.add(pair);
        }
        return new Receipt(products);
    }

    private Behavior<INotification> onProductsSensorResponse(ProductSensorResponse response) {
        currentProductAmount = response.newAmount;
        getContext().getLog().info("Got response from ProductSensor - new Amount of Products: {}", currentProductAmount);
        return this;
    }

    private Behavior<INotification> onSpaceSensorResponse(SpaceSensorResponse response) {
        currentWeight = response.newWeight;
        getContext().getLog().info("Got response from SpaceSensor - new Weight in the fridge: {} kg", currentWeight);
        return this;
    }
}
