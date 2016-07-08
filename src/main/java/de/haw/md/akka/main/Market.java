package de.haw.md.akka.main;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import de.haw.md.akka.main.msg.CompanyShareMsgModel;
import de.haw.md.akka.main.msg.MarketResponseMsgModel;
import de.haw.md.akka.main.msg.MarketShareMsgModel;
import de.haw.md.akka.main.msg.ResourceMsgModel;
import de.haw.md.sups.ResourceCalc;
import de.haw.md.sups.Resources;
import de.haw.md.sups.StaticVariables;

public class Market extends UntypedActor {

	// private LoggingAdapter log = Logging.getLogger(getContext().system(),
	// this);

	private ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();

	private String channel;

	private Resources res = new Resources();

	private Map<String, BigDecimal> companyMarketPrices = new HashMap<>();
	
	private BigDecimal counter = BigDecimal.ZERO;
	
	private BigDecimal currentMarketVolume;

	public Market(String channel) {
		res.readAllPrices();
		this.channel = channel;
		currentMarketVolume = StaticVariables.MARKET_VOLUME;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof String) {
			if (msg.equals("Tick")) {
				final String resOilMsg = mapToJson("Oil", res.getOilPrice());
				publish(resOilMsg);
				final String resCopperMsg = mapToJson("Copper", res.getCopperPrice());
				publish(resCopperMsg);
				final String resAluminiumMsg = mapToJson("Aluminium", res.getAluminiumPrice());
				publish(resAluminiumMsg);
				final String resGoldMsg = mapToJson("Gold", res.getGoldPrice());
				publish(resGoldMsg);
				final String resNickelMsg = mapToJson("Nickel", res.getNickelPrice());
				publish(resNickelMsg);
				final String resPalladiumMsg = mapToJson("Palladium", res.getPalladiumPrice());
				publish(resPalladiumMsg);
				final String resPlatinMsg = mapToJson("Platin", res.getPlatinPrice());
				publish(resPlatinMsg);
				final String resSilberMsg = mapToJson("Silber", res.getSilberPrice());
				publish(resSilberMsg);
				final String resZinnMsg = mapToJson("Zinn", res.getZinnPrice());
				publish(resZinnMsg);
				if(counter.compareTo(BigDecimal.ZERO) != 0)
					currentMarketVolume = currentMarketVolume.divide(counter, 0, RoundingMode.HALF_DOWN);
				if (companyMarketPrices.size() > 0) {
					final String generateShares = generateShares();
					System.out.println(generateShares);
					publish(generateShares);
				}
				System.out.println("");
			} else {
				// log.info("Publish Channel: {}, got: {}", channel, msg);
				try {
					ObjectMapper om = new ObjectMapper();
					MarketResponseMsgModel mrmm = om.readValue((String) msg, MarketResponseMsgModel.class);
					if (mrmm.getType().equals("Mobile_Phone")) {
						if (companyMarketPrices.containsKey(mrmm.getCompany())) {
							companyMarketPrices.replace(mrmm.getCompany(), StaticVariables.convertToBigDecimal(mrmm.getValue()));
						} else {
							companyMarketPrices.put(mrmm.getCompany(), StaticVariables.convertToBigDecimal(mrmm.getValue()));
						}
					}
					System.out.println(msg);
					publish((String) msg);
				} catch (UnrecognizedPropertyException e) {
				}
			}
		} else {
			unhandled(msg);
		}
	}

	private String generateShares() throws JsonGenerationException, JsonMappingException, IOException, CloneNotSupportedException {
		ObjectMapper om = new ObjectMapper();
		BigDecimal fixedMarketSharePerCompany = StaticVariables.FIXED_MARKET_SHARE.divide(new BigDecimal(companyMarketPrices.size()), RoundingMode.HALF_DOWN);
		BigDecimal sumPrice = BigDecimal.ZERO;
		BigDecimal variableShare = StaticVariables.HUNDRED.subtract(StaticVariables.FIXED_MARKET_SHARE);
		List<CompanyShareMsgModel> companyShareMsgModels = new ArrayList<>();
		for (String company : companyMarketPrices.keySet())
			sumPrice = sumPrice.add(companyMarketPrices.get(company));
		for (String company : companyMarketPrices.keySet()) {
			BigDecimal sumPriceOnePercent = sumPrice.divide(StaticVariables.HUNDRED, 10, RoundingMode.HALF_DOWN);
			BigDecimal percentPerPrice = companyMarketPrices.get(company).divide(sumPriceOnePercent, 10, RoundingMode.HALF_DOWN);
			BigDecimal variableShareOnePercent = variableShare.divide(StaticVariables.HUNDRED, 10, RoundingMode.HALF_DOWN);
			BigDecimal variableSharePerComp = percentPerPrice.multiply(variableShareOnePercent);
			BigDecimal perCompMarketShare = (fixedMarketSharePerCompany.add(variableSharePerComp));
			companyShareMsgModels.add(new CompanyShareMsgModel(company, perCompMarketShare.toString()));
		}
		companyShareMsgModels = sortShares(companyShareMsgModels);
		calculateShareVolume(companyShareMsgModels);
		MarketShareMsgModel msmm = new MarketShareMsgModel();
		msmm.setType("Market_Share");
		msmm.setCompanyShareMsgModels(companyShareMsgModels);
		msmm.setDate(ResourceCalc.getFinalDate(res.getOilPrice()).toString(StaticVariables.DE_DATE_FORMATTER));
		return om.writeValueAsString(msmm);
	}

	private void calculateShareVolume(List<CompanyShareMsgModel> companyShareMsgModels) {
		for (CompanyShareMsgModel companyShareMsgModel : companyShareMsgModels)
			companyShareMsgModel.setShareVolume(currentMarketVolume.divide(StaticVariables.HUNDRED, RoundingMode.HALF_DOWN).multiply(
					StaticVariables.convertToBigDecimal(companyShareMsgModel.getShareValue())).setScale(0, RoundingMode.HALF_DOWN).toString());
	}

	private List<CompanyShareMsgModel> sortShares(List<CompanyShareMsgModel> companyShareMsgModels) throws CloneNotSupportedException {
		Collections.sort(companyShareMsgModels);
		CompanyShareMsgModel[] csmmArray = new CompanyShareMsgModel[companyShareMsgModels.size()];
		for (int i = 0; i < companyShareMsgModels.size(); i++) {
			csmmArray[i] = (CompanyShareMsgModel) companyShareMsgModels.get(i).clone();
		}
		Arrays.sort(csmmArray, Collections.reverseOrder());
		for (int i = 0; i < csmmArray.length; i++)
			csmmArray[i].setShareValue(companyShareMsgModels.get(i).getShareValue());
		List<CompanyShareMsgModel> newCompanyShareMsgModels = new ArrayList<>();
		for (int i = 0; i < csmmArray.length; i++) {
			newCompanyShareMsgModels.add(csmmArray[i]);
		}
		return newCompanyShareMsgModels;
	}

	private void publish(String msg) {
		mediator.tell(new DistributedPubSubMediator.Publish(channel, msg), getSelf());
	}

	public String mapToJson(String type, Map<DateTime, BigDecimal> ressource) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		ResourceMsgModel rmm = new ResourceMsgModel();
		final BigDecimal newPrice = ResourceCalc.nextRandomStockPrice(ressource, 50);
		final DateTime date = ResourceCalc.getFinalDate(ressource).plusDays(1);
		rmm.setDate(date.toString(StaticVariables.DE_DATE_FORMATTER));
		rmm.setValue(newPrice.setScale(2, RoundingMode.HALF_DOWN).toString());
		rmm.setType(type);
		ressource.put(date, newPrice);
		return mapper.writeValueAsString(rmm);
	}

}
