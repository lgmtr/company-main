package de.haw.md.akka.main;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import de.haw.md.akka.main.msg.MarketResponseMsgModel;
import de.haw.md.akka.main.msg.ResourceMsgModel;
import de.haw.md.company.main.CompanyMainTwo;
import de.haw.md.sups.StaticVariables;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;

public class CompanyOil extends UntypedActor {

	private String channel;
	private String nameOfSubscriber;
	private static final BigDecimal PRODUCTION_COST = StaticVariables.convertToBigDecimal("2.3242");
	private static final BigDecimal MARK_UP = CompanyMainTwo.generateRandomBigDecimalFromRange(StaticVariables.convertToBigDecimal("1.05"), StaticVariables.convertToBigDecimal("1.07"));

	public CompanyOil(String channel, String nameOfSubscriber) {
		this.nameOfSubscriber = nameOfSubscriber;
		this.channel = channel;
		ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
		mediator.tell(new DistributedPubSubMediator.Subscribe(channel, getSelf()), getSelf());
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof String) {
			try {
				ObjectMapper om = new ObjectMapper();
				ResourceMsgModel rmm = om.readValue((String) msg, ResourceMsgModel.class);
				if (rmm.getType().equals("Oil")) {
					// log.info("Subscriber Channel: {}, got: {}", channel,
					// msg);
					ActorRef publisher = MarketContainer.getInstance().getPublisher(channel);
					final BigDecimal oilPrice = StaticVariables.convertToBigDecimal(rmm.getValue());
					final BigDecimal oilPriceInKG = oilPrice.divide(StaticVariables.BARREL_IN_KG, 10, RoundingMode.HALF_DOWN);
					final BigDecimal plasticPriceInKG = oilPriceInKG.multiply(PRODUCTION_COST);
					MarketResponseMsgModel mrmm = new MarketResponseMsgModel();
					mrmm.setCompany(nameOfSubscriber);
					mrmm.setDate(rmm.getDate());
					mrmm.setType("Plastic");
					mrmm.setValue(plasticPriceInKG.multiply(MARK_UP).setScale(2, RoundingMode.HALF_UP).toString());
					publisher.tell(om.writeValueAsString(mrmm), getSelf());
				}
			} catch (UnrecognizedPropertyException e) {
			}
		}
	}

}
