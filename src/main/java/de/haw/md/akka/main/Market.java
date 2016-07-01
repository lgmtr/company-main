package de.haw.md.akka.main;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import org.joda.time.DateTime;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.haw.md.akka.main.msg.ResourceMsgModel;
import de.haw.md.sups.Resources;

public class Market extends UntypedActor {

	// private LoggingAdapter log = Logging.getLogger(getContext().system(),
	// this);

	private ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();

	private String channel;

	private Resources res = new Resources();

	private int counter = 0;

	public Market(String channel) {
		res.readAllPrices();
		this.channel = channel;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof String) {
			if (msg.equals("Tick")) {
				final String resOilMsg = mapToJson(res.sortKeyList(res.getOilPrice().keySet()).get(counter), "Oil", res.getOilPrice());
				publish(resOilMsg);
				final String resCopperMsg = mapToJson(res.sortKeyList(res.getCopperPrice().keySet()).get(counter), "Copper", res.getCopperPrice());
				publish(resCopperMsg);
				final String resAluminiumMsg = mapToJson(res.sortKeyList(res.getAluminiumPrice().keySet()).get(counter), "Aluminium", res.getAluminiumPrice());
				publish(resAluminiumMsg);
				final String resGoldMsg = mapToJson(res.sortKeyList(res.getGoldPrice().keySet()).get(counter), "Gold", res.getGoldPrice());
				publish(resGoldMsg);
				final String resNickelMsg = mapToJson(res.sortKeyList(res.getNickelPrice().keySet()).get(counter), "Nickel", res.getNickelPrice());
				publish(resNickelMsg);
				final String resPalladiumMsg = mapToJson(res.sortKeyList(res.getPalladiumPrice().keySet()).get(counter), "Palladium", res.getPalladiumPrice());
				publish(resPalladiumMsg);
				final String resPlatinMsg = mapToJson(res.sortKeyList(res.getPlatinPrice().keySet()).get(counter), "Platin", res.getPlatinPrice());
				publish(resPlatinMsg);
				final String resSilberMsg = mapToJson(res.sortKeyList(res.getSilberPrice().keySet()).get(counter), "Silber", res.getSilberPrice());
				publish(resSilberMsg);
				final String resZinnMsg = mapToJson(res.sortKeyList(res.getZinnPrice().keySet()).get(counter), "Zinn", res.getZinnPrice());
				publish(resZinnMsg);
				counter++;
				System.out.println("");
			} else {
				// log.info("Publish Channel: {}, got: {}", channel, msg);
				if (((String) msg).contains("company")) {
					System.out.println(msg);
					publish((String) msg);
				}
			}
		} else {
			unhandled(msg);
		}
	}

	private void publish(String msg) {
		mediator.tell(new DistributedPubSubMediator.Publish(channel, msg), getSelf());
	}

	public String mapToJson(DateTime dateTime, String type, Map<DateTime, BigDecimal> object) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		ResourceMsgModel rmm = new ResourceMsgModel();
		rmm.setDate(dateTime.toString("dd.MM.yyyy"));
		rmm.setValue(object.get(dateTime).toString());
		rmm.setType(type);
		return mapper.writeValueAsString(rmm);
	}

}
