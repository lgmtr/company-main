package de.haw.md.akka.main;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.joda.time.DateTime;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import de.haw.md.akka.main.msg.MarketResponseMsgModel;
import de.haw.md.akka.main.msg.ResourceMsgModel;
import de.haw.md.sups.StaticVariables;

public class CompanyMobile extends UntypedActor {

	private String channel;

	private String nameOfSubscriber;

	private String supplier;
	private BigDecimal supDiscount;

	private BigDecimal copperPrice;
	private BigDecimal aluminiumPrice;
	private BigDecimal goldPrice;
	private BigDecimal nickelPrice;
	private BigDecimal palladiumPrice;
	private BigDecimal platinPrice;
	private BigDecimal silberPrice;
	private BigDecimal zinnPrice;
	private BigDecimal plasticPrice;
	private BigDecimal electronicPartPrice;

	private static final BigDecimal POP_PLASTIC_IN_G = new BigDecimal("55.9");
	private static final BigDecimal POP_COPPER_IN_G = new BigDecimal("7.67");
	private static final BigDecimal POP_ALUMINIUM_IN_G = new BigDecimal("1.3");
	private static final BigDecimal POP_NICKEL_IN_G = new BigDecimal("1.3");
	private static final BigDecimal POP_ZINN_IN_G = new BigDecimal("1.3");
	private static final BigDecimal POP_GOLD_IN_G = new BigDecimal("0.0005");
	private static final BigDecimal POP_SILBER_IN_G = new BigDecimal("0.0005");
	private static final BigDecimal POP_PLATIN_IN_G = new BigDecimal("0.0005");
	private static final BigDecimal POP_PALLADIUM_IN_G = new BigDecimal("0.0005");

	private BigDecimal costManHour;
	private BigDecimal fixCost;
	private BigDecimal prodManHour;
	private BigDecimal bonus;

	private DateTime dateTicker;

	public CompanyMobile(String channel, String nameOfSubscriber, BigDecimal costManHour, BigDecimal fixCost, BigDecimal prodManHour, BigDecimal bonus,
			String supplier, BigDecimal supDiscount) {
		this.supDiscount = supDiscount;
		this.supplier = supplier;
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
			if (((String) msg).contains("company")) {
				MarketResponseMsgModel mrmmInput = om.readValue((String) msg, MarketResponseMsgModel.class);
				setPlasticPrices(mrmmInput);
				if (mrmmInput.getCompany().equals(supplier))
					setPlasticPrices(mrmmInput);
				if (dateTicker == null)
					dateTicker = DateTime.parse(mrmmInput.getDate(), StaticVariables.DE_DATE_FORMATTER);
				if (pricesNotNull()) {
					final BigDecimal prodPrice = calculateProdPrice();
					final BigDecimal bonusedProdPrice = prodPrice.multiply(bonus);
					MarketResponseMsgModel mrmm = new MarketResponseMsgModel();
					mrmm.setCompany(nameOfSubscriber);
					mrmm.setType("Mobile_Phone");
					mrmm.setDate(om.readValue((String) msg, MarketResponseMsgModel.class).getDate());
					mrmm.setValue(bonusedProdPrice.setScale(2, RoundingMode.UP).toString());
					if (dateTicker.isBefore(DateTime.parse(mrmmInput.getDate(), StaticVariables.DE_DATE_FORMATTER))) {
						dateTicker = DateTime.parse(mrmmInput.getDate(), StaticVariables.DE_DATE_FORMATTER);
						ActorRef publisher = MarketContainer.getInstance().getPublisher(channel);
						publisher.tell(om.writeValueAsString(mrmm), getSelf());
					}
				}
			}
		}
	}

	private BigDecimal calculateProdPrice() {
		final BigDecimal platinPartPrice = platinPrice.multiply(StaticVariables.OZ_TO_GRAMM).multiply(POP_PLATIN_IN_G);
		final BigDecimal goldPartPrice = goldPrice.multiply(StaticVariables.OZ_TO_GRAMM).multiply(POP_GOLD_IN_G);
		final BigDecimal silberPartPrice = silberPrice.multiply(StaticVariables.OZ_TO_GRAMM).multiply(POP_SILBER_IN_G);
		final BigDecimal palladiumPartPrice = palladiumPrice.multiply(StaticVariables.OZ_TO_GRAMM).multiply(POP_PALLADIUM_IN_G);
		final BigDecimal plasticPartPrice = plasticPrice.divide(StaticVariables.KG_IN_GRAMM, RoundingMode.HALF_DOWN).multiply(POP_PLASTIC_IN_G);
		final BigDecimal copperPartPrice = copperPrice.divide(StaticVariables.T_IN_GRAMM, RoundingMode.HALF_DOWN).multiply(POP_COPPER_IN_G);
		final BigDecimal aluminiumPartPrice = aluminiumPrice.divide(StaticVariables.T_IN_GRAMM, RoundingMode.HALF_DOWN).multiply(POP_ALUMINIUM_IN_G);
		final BigDecimal nickelPartPrice = nickelPrice.divide(StaticVariables.T_IN_GRAMM, RoundingMode.HALF_DOWN).multiply(POP_NICKEL_IN_G);
		final BigDecimal zinnPartPrice = zinnPrice.divide(StaticVariables.T_IN_GRAMM, RoundingMode.HALF_DOWN).multiply(POP_ZINN_IN_G);
		final BigDecimal compPartWOSupPrice = platinPartPrice.add(goldPartPrice).add(silberPartPrice).add(palladiumPartPrice).add(plasticPartPrice)
				.add(copperPartPrice).add(aluminiumPartPrice).add(nickelPartPrice).add(zinnPartPrice);
		final BigDecimal supPriceWithDisc = electronicPartPrice.divide(supDiscount, RoundingMode.HALF_UP);
		final BigDecimal complManCost = costManHour.multiply(prodManHour);
		return (compPartWOSupPrice.add(complManCost).add(supPriceWithDisc)).multiply(fixCost);
	}

	private boolean pricesNotNull() {
		if (copperPrice == null)
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
		if (electronicPartPrice == null)
			return false;
		return true;
	}

	private void setPlasticPrices(MarketResponseMsgModel mrmm) {
		if (mrmm.getType().equals("Plastic"))
			plasticPrice = new BigDecimal(mrmm.getValue());
		if (mrmm.getType().equals("Electronic_Part"))
			electronicPartPrice = new BigDecimal(mrmm.getValue());
	}

	private void setResourcePrices(ResourceMsgModel rmm) {
		switch (rmm.getType()) {
		case "Copper":
			copperPrice = new BigDecimal(rmm.getValue());
			break;
		case "Aluminium":
			aluminiumPrice = new BigDecimal(rmm.getValue());
			break;
		case "Gold":
			goldPrice = new BigDecimal(rmm.getValue());
			break;
		case "Nickel":
			nickelPrice = new BigDecimal(rmm.getValue());
			break;
		case "Palladium":
			palladiumPrice = new BigDecimal(rmm.getValue());
			break;
		case "Platin":
			platinPrice = new BigDecimal(rmm.getValue());
			break;
		case "Silber":
			silberPrice = new BigDecimal(rmm.getValue());
			break;
		case "Zinn":
			zinnPrice = new BigDecimal(rmm.getValue());
			break;
		}

	}

}
