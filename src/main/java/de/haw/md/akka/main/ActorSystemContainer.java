package de.haw.md.akka.main;

import akka.actor.ActorSystem;

public class ActorSystemContainer {

	private ActorSystem sys;
    private ActorSystemContainer() {
        sys = ActorSystem.create("ClusterSystem");
    }

    public ActorSystem getSystem() {
        return sys;
    }

    private static ActorSystemContainer instance = null;

    public static synchronized ActorSystemContainer getInstance() {
        if (instance == null) {
            instance = new ActorSystemContainer();
        }
        return instance;
    }
    
}
