package de.haw.md.akka.main;

import java.util.HashMap;
import java.util.Map;

import akka.actor.ActorRef;
import akka.actor.Props;

public class MarketContainer {

	private static MarketContainer instance = null;

	private Map<String, ActorRef> publisher;

	public MarketContainer() {
		publisher = new HashMap<>();
	}

	public ActorRef getPublisher(String channel) {
		ActorRef publishActor = null;
		if (publisher.containsKey(channel)) {
			publishActor = publisher.get(channel);
		} else {
			publishActor = ActorSystemContainer.getInstance().getSystem().actorOf(Props.create(Market.class, channel), channel);
			publisher.put(channel, publishActor);
		}
		return publishActor;
	}

	public static synchronized MarketContainer getInstance() {
		if (instance == null) {
			instance = new MarketContainer();
		}
		return instance;
	}
}
