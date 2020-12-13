package at.fhv.sysarch.lab3;

import akka.actor.typed.ActorSystem;
import at.fhv.sysarch.lab3.blinds.Blinds;
import at.fhv.sysarch.lab3.blinds.BlindsNotification;
import at.fhv.sysarch.lab3.mediaStation.MediaStation;
import at.fhv.sysarch.lab3.mediaStation.MediaStationNotification;

import java.io.InputStreamReader;
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

            switch(line) {
                case 1:
                    System.out.println("MediaStation selected...:");
                    System.out.println("What do you want?");
                    System.out.println("1 - Watch a movie:");
                    System.out.println("2 - Turn off media station:");

                    line = sc.nextInt();

                    switch(line) {
                        case 1:
                            //#main-send-messages
                            // inform MediaStation
                            notificationMain.tell(new MediaStationNotification(MediaStation.Actions.START.action));

                            // inform Blinds
                            notificationMain.tell(new BlindsNotification(Blinds.Actions.CLOSE.action));

                            //#main-send-messages
                            break;
                        case 2:
                            //#main-send-messages
                            notificationMain.tell(new MediaStationNotification(MediaStation.Actions.TURN_OFF.action));

                            // inform Blinds
                            notificationMain.tell(new BlindsNotification(Blinds.Actions.OPEN.action));
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
                    System.exit(0);
                default:
                    System.out.println("Not a valid option!");
                    break;
            }


        }
    }
}
