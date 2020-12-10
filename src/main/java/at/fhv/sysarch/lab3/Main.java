package at.fhv.sysarch.lab3;

import akka.actor.typed.ActorSystem;
import at.fhv.sysarch.lab3.actors.Actor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.Random;


public class Main {

    public static void main(String[] args) {
        boolean running = true;
        //#actor-system
        final ActorSystem<BlackboardMain.SendNotification> notificationMain = ActorSystem.create(BlackboardMain.create(), "automationSystem");
        //#actor-system
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please tell me what you wan't to do");

        notificationMain.tell(new BlackboardMain.SendNotification(Actor.TEMPERATURE_SIMULATOR, "start"));

        while (running) {
            double MIN = -5;
            double MAX = 5;
            Random random = new Random();
            double rand = (MIN + (MAX - MIN) * random.nextDouble());
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);
            nf.format(rand);
            System.out.println(nf.format(rand));

            String line = "";
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (line.equals("media")) {
                System.out.println("in media");
                //#main-send-messages
                notificationMain.tell(new BlackboardMain.SendNotification(Actor.MEDIA_STATION, "movie"));
                //#main-send-messages
            }

            if (line.equals("stop")) {
                System.out.println("in media");
                //#main-send-messages
                notificationMain.tell(new BlackboardMain.SendNotification(Actor.MEDIA_STATION, "stop"));
                //#main-send-messages
            }

            if (line.equals("end")) {
                System.out.println("ending");
                running = false;
                notificationMain.terminate();
            }
        }
    }
}
