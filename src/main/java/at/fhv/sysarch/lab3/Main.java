package at.fhv.sysarch.lab3;

import akka.actor.typed.ActorSystem;
import at.fhv.sysarch.lab3.actors.Actor;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.Random;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) {
        boolean running = true;
        //#actor-system
        final ActorSystem<BlackboardMain.SendNotification> notificationMain = ActorSystem.create(BlackboardMain.create(), "automationSystem");
        //#actor-system
        Scanner sc = new Scanner(new InputStreamReader(System.in));


        notificationMain.tell(new BlackboardMain.SendNotification(Actor.TEMPERATURE_SIMULATOR, "start"));

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

            switch(line) {
                case 1:
                    System.out.println("MediaStation selected...:");
                    System.out.println("What do you want?");
                    System.out.println("1 - Watch a movie:");
                    System.out.println("2 - Turn off media station:");

                    line = sc.nextInt();

                    switch(line) {
                        case 1:
                            System.out.println("MediaStation is starting...");
                            //#main-send-messages
                            notificationMain.tell(new BlackboardMain.SendNotification(Actor.MEDIA_STATION, "movie"));
                            //#main-send-messages
                            break;
                        case 2:
                            System.out.println("MediaStation turning off...");
                            //#main-send-messages
                            notificationMain.tell(new BlackboardMain.SendNotification(Actor.MEDIA_STATION, "stop"));
                            //#main-send-messages
                            break;
                    }

                    break;
                case 2:
                    System.out.println("Blinds");
                    break;
                case 5:
                    System.out.println("Program ending...");
                    running = false;
                    notificationMain.terminate();
                default:
                    System.out.println("Not a valid option!");
                    break;
            }


                double MIN = -5;
            double MAX = 5;
            Random random = new Random();
            double rand = (MIN + (MAX - MIN) * random.nextDouble());
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);
            nf.format(rand);
            System.out.println(nf.format(rand));


        }
    }
}