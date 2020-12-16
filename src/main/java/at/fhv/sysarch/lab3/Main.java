package at.fhv.sysarch.lab3;

import akka.actor.typed.ActorSystem;
import akka.japi.Pair;
import at.fhv.sysarch.lab3.blinds.Blinds;
import at.fhv.sysarch.lab3.blinds.BlindsNotification;
import at.fhv.sysarch.lab3.mediaStation.MediaStation;
import at.fhv.sysarch.lab3.mediaStation.MediaStationNotification;
import at.fhv.sysarch.lab3.refrigerator.*;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) {
        boolean running = true;
        //#actor-system
        final ActorSystem<INotification> notificationMain = ActorSystem.create(BlackboardMain.create(), "automationSystem");
        //#actor-system
        Scanner sc = new Scanner(new InputStreamReader(System.in));


        int line = 0;
        while (running) {
            System.out.println("Please tell me what you wan't to do");
            System.out.println("------------------------------------");
            System.out.println("Press numbers for Options:");
            System.out.println("1 - MediaStation");
            System.out.println("2 - Blinds");
            System.out.println("3 - Fridge");
            System.out.println("4 - AC");
            System.out.println("5 - End");

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
                    System.out.println("Blinds");
                    break;
                case 3:
                    System.out.println("Fridge selected...:");
                    System.out.println("What do you want?");
                    System.out.println("1 - Order:");
                    System.out.println("2 - Consume:");

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
                                System.out.println("3 - Complete Order");
                                System.out.println("4 - Abort Order");


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
                                        orderComplete = true;
                                        System.out.println("Order completed");
                                        notificationMain.tell(new OrderNotification(orders));
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
                            }
                            break;
                    }
                    break;

                case 5:
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
