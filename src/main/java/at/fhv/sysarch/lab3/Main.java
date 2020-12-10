package at.fhv.sysarch.lab3;

import akka.actor.typed.ActorSystem;
import at.fhv.sysarch.lab3.actors.Actor;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        //#actor-system
        final ActorSystem<BlackboardMain.SendNotification> notificationMain = ActorSystem.create(BlackboardMain.create(), "automationSystem");
        //#actor-system

        //#main-send-messages
        notificationMain.tell(new BlackboardMain.SendNotification(Actor.MEDIA_STATION, "test"));
        //#main-send-messages

        try {
            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ignored) {
        } finally {
            notificationMain.terminate();
        }
    }
}
