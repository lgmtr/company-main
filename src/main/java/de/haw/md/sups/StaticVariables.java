package de.haw.md.sups;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class StaticVariables {

	//Akka-Framework Channel
	public final static String CHANNEL = "MarketSim";
	
	//Helper statics
	public final static BigDecimal MONTH = new BigDecimal("30");
	public final static BigDecimal HUNDRED = new BigDecimal("100");
	
	//Converting statics
	public final static BigDecimal US_TO_EURO = new BigDecimal("0.898284");
	public final static BigDecimal OZ_TO_KG = new BigDecimal("0.0283495");
	public final static BigDecimal OZ_TO_GRAMM = new BigDecimal("28.3495");
	public static final BigDecimal BARREL_IN_KG = new BigDecimal("119.2404717");
	public static final BigDecimal KG_IN_GRAMM = new BigDecimal("1000");
	public static final BigDecimal T_IN_GRAMM = new BigDecimal("1000000");
	
	//Date Formatter
	public final static DateTimeFormatter US_DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
	public final static DateTimeFormatter DE_DATE_FORMATTER = DateTimeFormat.forPattern("dd.MM.yyyy");
	
	//Market statics
	public final static BigDecimal FIXED_MARKET_SHARE = new BigDecimal("30");
	public final static BigDecimal MARKET_VOLUME = new BigDecimal("10000000");
	public final static BigDecimal PRICE_ADJUSTMENT = new BigDecimal("0.001");
	public final static BigDecimal ESTIMATED_MARKT_VOLUME = MARKET_VOLUME.divide(HUNDRED).multiply(new BigDecimal("5"));
	
	//Company statics
	public final static BigDecimal HIGHEST_ACCEPTEBLE_DEFICIT = new BigDecimal("-100000000");;
	
	/**
	 * Convert String to BigDecimal
	 * 
	 * @param value
	 * @return
	 */
	public static BigDecimal convertToBigDecimal(String value) {
		return new BigDecimal(value);
	}
	
	//Random BigDecimal with Range and Scale
	public static BigDecimal generateRandomBigDecimalFromRange(BigDecimal min, BigDecimal max) {
		BigDecimal randomBigDecimal = min.add(new BigDecimal(Math.random()).multiply(max.subtract(min)));
		return randomBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public static BigDecimal generateRandomBigDecimalFromRangeScale0(BigDecimal min, BigDecimal max) {
		return generateRandomBigDecimalFromRange(min, max).setScale(0, RoundingMode.HALF_UP);
	}
}
