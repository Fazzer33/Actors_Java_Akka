package at.fhv.sysarch.lab3;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;

public class BlackboardMain extends AbstractBehavior<String> {

    public BlackboardMain(ActorContext<String> context) {
        super(context);
    }

    @Override
    public Receive<String> createReceive() {
        return null;
    }
}
