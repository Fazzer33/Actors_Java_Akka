package at.fhv.sysarch.lab3;

import akka.actor.typed.ActorSystem;
import akka.japi.Pair;
import at.fhv.sysarch.lab3.mediaStation.MediaStation;
import at.fhv.sysarch.lab3.mediaStation.MediaStationNotification;
import at.fhv.sysarch.lab3.refrigerator.*;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) throws InterruptedException {
        boolean running = true;
        //#actor-system
        final ActorSystem<INotification> notificationMain = ActorSystem.create(BlackboardMain.create(), "automationSystem");
        //#actor-system
        Scanner sc = new Scanner(new InputStreamReader(System.in));


        int line = 0;
        while (running) {
            // Thread sleep that it waits until everything is printed before doing new actions
            Thread.sleep(1000);
            System.out.println("Please tell me what you wan't to do");
            System.out.println("------------------------------------");
            System.out.println("Press numbers for Options:");
            System.out.println("1 - MediaStation");
            System.out.println("2 - Fridge");
            System.out.println("3 - Show current System State");
            System.out.println("4 - End");

            line = sc.nextInt();

            System.out.println(line);

            switch (line) {
                case 1:
                    System.out.println("MediaStation selected...:");
                    System.out.println("What do you want?");
                    System.out.println("1 - Watch a movie:");
                    System.out.println("2 - Turn off media station:");

                    line = sc.nextInt();

                    switch (line) {
                        case 1:
                            // inform MediaStation
                            notificationMain.tell(new MediaStationNotification(MediaStation.Actions.START.action));
                            break;
                        case 2:
                            notificationMain.tell(new MediaStationNotification(MediaStation.Actions.TURN_OFF.action));
                            break;
                    }

                    break;
                case 2:
                    System.out.println("Fridge selected...:");
                    System.out.println("What do you want?");
                    System.out.println("1 - Order:");
                    System.out.println("2 - Consume:");
                    System.out.println("3 - Show Current Products in Fridge");
                    System.out.println("4 - Show Order History");

                    line = sc.nextInt();


                    switch (line) {
                        // Order
                        case 1:
                            boolean orderComplete = false;
                            HashMap<ProductType, Integer> orders = new HashMap<>();
                            int amount = 0;

                            while (!orderComplete) {
                                System.out.println("What do you want to order?");
                                System.out.println("1 - Apples");
                                System.out.println("2 - Milk");
                                System.out.println("3 - Egg");
                                System.out.println("4 - Ham");
                                System.out.println("5 - Cola");
                                System.out.println("6 - Beer");
                                System.out.println("7 - Complete Order");
                                System.out.println("8 - Abort Order");


                                line = sc.nextInt();

                                switch (line) {
                                    case 1:
                                        System.out.println("How many Apples do you want?");
                                        amount = sc.nextInt();
                                        orders.put(ProductType.APPLE, amount);
                                        break;
                                    case 2:
                                        System.out.println("How many Milk-Packages do you want?");
                                        amount = sc.nextInt();
                                        orders.put(ProductType.MILK, amount);
                                        break;
                                    case 3:
                                        System.out.println("How many eggs do you want?");
                                        amount = sc.nextInt();
                                        orders.put(ProductType.EGGS, amount);
                                        break;
                                    case 4:
                                        System.out.println("How many ham packages (1 package = 0.2 kg)");
                                        amount = sc.nextInt();
                                        orders.put(ProductType.HAM, amount);
                                        break;
                                    case 5:
                                        System.out.println("How many bottles of coke?");
                                        amount = sc.nextInt();
                                        orders.put(ProductType.COKE, amount);
                                        break;
                                    case 6:
                                        System.out.println("How many bottles of beer?");
                                        amount = sc.nextInt();
                                        orders.put(ProductType.BEER, amount);
                                        break;
                                    case 7:
                                        orderComplete = true;
                                        System.out.println("Order completed");
                                        notificationMain.tell(new OrderNotification(orders));
                                        break;
                                    case 8:
                                        break;
                                }
                            }
                            break;

                        // Consume
                        case 2:
                            int consumeAmount = 0;

                            System.out.println("What do you want from the fridge:");
                            System.out.println("1 - Apple");
                            System.out.println("2 - Milk");
                            System.out.println("3 - Egg");
                            System.out.println("4 - Ham");
                            System.out.println("5 - Cola");
                            System.out.println("6 - Beer");

                            line = sc.nextInt();

                            switch (line) {
                                case 1:
                                    System.out.println("How many Apples?");
                                    consumeAmount = sc.nextInt();
                                    notificationMain.tell( new ConsumeNotification(new Pair<>(ProductType.APPLE, consumeAmount)));
                                    break;

                                case 2:
                                    System.out.println("How many Milk-Packages?");
                                    consumeAmount = sc.nextInt();
                                    notificationMain.tell( new ConsumeNotification(new Pair<>(ProductType.APPLE, consumeAmount)));
                                    break;

                                case 3:
                                    System.out.println("How many Eggs?");
                                    consumeAmount = sc.nextInt();
                                    notificationMain.tell( new ConsumeNotification(new Pair<>(ProductType.EGGS, consumeAmount)));
                                    break;

                                case 4:
                                    System.out.println("How much ham (0,1kg per consume) ?");
                                    consumeAmount = sc.nextInt();
                                    notificationMain.tell( new ConsumeNotification(new Pair<>(ProductType.HAM, consumeAmount)));
                                    break;

                                case 5:
                                    System.out.println("How many Coke bottles?");
                                    consumeAmount = sc.nextInt();
                                    notificationMain.tell( new ConsumeNotification(new Pair<>(ProductType.COKE, consumeAmount)));
                                    break;
                                case 6:
                                    System.out.println("How much Beer bottles?");
                                    consumeAmount = sc.nextInt();
                                    notificationMain.tell( new ConsumeNotification(new Pair<>(ProductType.BEER, consumeAmount)));
                                    break;
                            }
                            break;

                        // Show Products in Fridge
                        case 3:
                            notificationMain.tell(new FridgeStatusNotification(FridgeStatusNotification.Actions.CURRENT_PRODUCTS.action));
                            break;

                        // Order History
                        case 4:
                            notificationMain.tell(new FridgeStatusNotification(FridgeStatusNotification.Actions.ORDER_HISTORY.action));
                            break;
                    }
                    break;

                case 3:
                    System.out.println("Current System State:");
                    System.out.println("------------------------------------");
                    System.out.println("The current temperature is: "+BlackboardMain.actorStates.getCurrentTemperature() +" °C");
                    System.out.println("The weather is: " +BlackboardMain.actorStates.getWeather());
                    System.out.println("Blinds are " +BlackboardMain.actorStates.getBlindsState());
                    System.out.println("Media station is " +BlackboardMain.actorStates.getMediaStationState());
                    System.out.println("AC is " +BlackboardMain.actorStates.getACState());
                    System.out.println("------------------------------------");
                    System.out.println();
                    break;
                case 4:
                    System.out.println("Program ending...");
                    running = false;
                    System.exit(0);
                default:
                    System.out.println("Not a valid option!");
                    break;
            }
        }
    }
}
