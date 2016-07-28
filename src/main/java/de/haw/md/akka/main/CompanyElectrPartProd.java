package de.haw.md.akka.main;

import java.math.BigDecimal;
import java.math.RoundingMode;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import de.haw.md.akka.main.msg.MarketResponseMsgModel;
import de.haw.md.akka.main.msg.ResourceMsgModel;
import de.haw.md.sups.StaticVariables;

public class CompanyElectrPartProd extends UntypedActor {

	private String channel;

	private String nameOfSubscriber;

	private BigDecimal kupferPrice;
	private BigDecimal aluminiumPrice;
	private BigDecimal goldPrice;
	private BigDecimal nickelPrice;
	private BigDecimal palladiumPrice;
	private BigDecimal platinPrice;
	private BigDecimal silberPrice;
	private BigDecimal zinnPrice;
	private BigDecimal plasticPrice;

	private static final BigDecimal POP_PLASTIC_IN_G = StaticVariables.convertToBigDecimal("39");
	private static final BigDecimal POP_KUPFER_IN_G = StaticVariables.convertToBigDecimal("16.9");
	private static final BigDecimal POP_ALUMINIUM_IN_G = StaticVariables.convertToBigDecimal("2.6");
	private static final BigDecimal POP_NICKEL_IN_G = StaticVariables.convertToBigDecimal("2.6");
	private static final BigDecimal POP_ZINN_IN_G = StaticVariables.convertToBigDecimal("1.3");
	private static final BigDecimal POP_GOLD_IN_G = StaticVariables.convertToBigDecimal("0.0025");
	private static final BigDecimal POP_SILBER_IN_G = StaticVariables.convertToBigDecimal("0.0025");
	private static final BigDecimal POP_PLATIN_IN_G = StaticVariables.convertToBigDecimal("0.0025");
	private static final BigDecimal POP_PALLADIUM_IN_G = StaticVariables.convertToBigDecimal("0.0025");

	private BigDecimal costManHour;
	private BigDecimal fixCost;
	private BigDecimal prodManHour;
	private BigDecimal bonus;

	public CompanyElectrPartProd(String channel, String nameOfSubscriber, BigDecimal costManHour, BigDecimal fixCost, BigDecimal prodManHour, BigDecimal bonus) {
		this.costManHour = costManHour;
		this.fixCost = fixCost;
		this.prodManHour = prodManHour;
		this.bonus = bonus;
		this.nameOfSubscriber = nameOfSubscriber;
		this.channel = channel;
		ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
		mediator.tell(new DistributedPubSubMediator.Subscribe(channel, getSelf()), getSelf());
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof String) {
			ObjectMapper om = new ObjectMapper();
			try {
				ResourceMsgModel rmm = om.readValue((String) msg, ResourceMsgModel.class);
				setResourcePrices(rmm);
			} catch (UnrecognizedPropertyException e) {
			}
			try {
				MarketResponseMsgModel mrmmResponse = om.readValue((String) msg, MarketResponseMsgModel.class);
				if(mrmmResponse.getType().equals("Plastic")){
					setPlasticPrices(om.readValue((String) msg, MarketResponseMsgModel.class));
					if (pricesNotNull()) {
						final BigDecimal prodPrice = calculateProdPrice();
						final BigDecimal bonusedProdPrice = prodPrice.multiply(bonus);
						MarketResponseMsgModel mrmm = new MarketResponseMsgModel();
						mrmm.setCompany(nameOfSubscriber);
						mrmm.setType("Electronic_Part");
						mrmm.setDate(om.readValue((String) msg, MarketResponseMsgModel.class).getDate());
						mrmm.setRevenue(bonusedProdPrice.setScale(2, RoundingMode.UP).toString());
						mrmm.setSelledProducts("0");
						mrmm.setProfit("0");
						ActorRef publisher = MarketContainer.getInstance().getPublisher(channel);
						publisher.tell(om.writeValueAsString(mrmm), getSelf());
					}
				}
			} catch (UnrecognizedPropertyException e) {
			}
		}
	}

	private BigDecimal calculateProdPrice() {
		final BigDecimal platinPartPrice = platinPrice.multiply(StaticVariables.OZ_TO_GRAMM).multiply(POP_PLATIN_IN_G);
		final BigDecimal goldPartPrice = goldPrice.multiply(StaticVariables.OZ_TO_GRAMM).multiply(POP_GOLD_IN_G);
		final BigDecimal silberPartPrice = silberPrice.multiply(StaticVariables.OZ_TO_GRAMM).multiply(POP_SILBER_IN_G);
		final BigDecimal palladiumPartPrice = palladiumPrice.multiply(StaticVariables.OZ_TO_GRAMM).multiply(POP_PALLADIUM_IN_G);
		final BigDecimal plasticPartPrice = plasticPrice.divide(StaticVariables.KG_IN_GRAMM, RoundingMode.HALF_DOWN).multiply(POP_PLASTIC_IN_G);
		final BigDecimal kupferPartPrice = kupferPrice.divide(StaticVariables.T_IN_GRAMM, RoundingMode.HALF_DOWN).multiply(POP_KUPFER_IN_G);
		final BigDecimal aluminiumPartPrice = aluminiumPrice.divide(StaticVariables.T_IN_GRAMM, RoundingMode.HALF_DOWN).multiply(POP_ALUMINIUM_IN_G);
		final BigDecimal nickelPartPrice = nickelPrice.divide(StaticVariables.T_IN_GRAMM, RoundingMode.HALF_DOWN).multiply(POP_NICKEL_IN_G);
		final BigDecimal zinnPartPrice = zinnPrice.divide(StaticVariables.T_IN_GRAMM, RoundingMode.HALF_DOWN).multiply(POP_ZINN_IN_G);
		final BigDecimal compPartPrice = platinPartPrice.add(goldPartPrice).add(silberPartPrice).add(palladiumPartPrice).add(plasticPartPrice)
				.add(kupferPartPrice).add(aluminiumPartPrice).add(nickelPartPrice).add(zinnPartPrice);
		final BigDecimal complManCost = costManHour.multiply(prodManHour);
		return (compPartPrice.add(complManCost)).multiply(fixCost);
	}

	private boolean pricesNotNull() {
		if (kupferPrice == null)
			return false;
		if (aluminiumPrice == null)
			return false;
		if (goldPrice == null)
			return false;
		if (nickelPrice == null)
			return false;
		if (palladiumPrice == null)
			return false;
		if (plasticPrice == null)
			return false;
		if (platinPrice == null)
			return false;
		if (silberPrice == null)
			return false;
		if (zinnPrice == null)
			return false;
		return true;
	}

	private void setPlasticPrices(MarketResponseMsgModel mrmm) {
		if (mrmm.getType().equals("Plastic"))
			plasticPrice = StaticVariables.convertToBigDecimal(mrmm.getRevenue());
	}

	private void setResourcePrices(ResourceMsgModel rmm) {
		switch (rmm.getType()) {
		case "Kupfer":
			kupferPrice = StaticVariables.convertToBigDecimal(rmm.getValue());
			break;
		case "Aluminium":
			aluminiumPrice = StaticVariables.convertToBigDecimal(rmm.getValue());
			break;
		case "Gold":
			goldPrice = StaticVariables.convertToBigDecimal(rmm.getValue());
			break;
		case "Nickel":
			nickelPrice = StaticVariables.convertToBigDecimal(rmm.getValue());
			break;
		case "Palladium":
			palladiumPrice = StaticVariables.convertToBigDecimal(rmm.getValue());
			break;
		case "Platin":
			platinPrice = StaticVariables.convertToBigDecimal(rmm.getValue());
			break;
		case "Silber":
			silberPrice = StaticVariables.convertToBigDecimal(rmm.getValue());
			break;
		case "Zinn":
			zinnPrice = StaticVariables.convertToBigDecimal(rmm.getValue());
			break;
		}

	}

}
