package de.haw.md.sups;

import java.math.BigDecimal;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class StaticVariables {

	public final static BigDecimal US_TO_EURO = new BigDecimal("0.898284");
	
	public final static BigDecimal OZ_TO_KG = new BigDecimal("0.0283495");
	
	public final static BigDecimal OZ_TO_GRAMM = new BigDecimal("28.3495");
	
	public static final BigDecimal BARREL_IN_KG = new BigDecimal("119.2404717");
	
	public static final BigDecimal KG_IN_GRAMM = new BigDecimal("1000");
	
	public static final BigDecimal T_IN_GRAMM = new BigDecimal("1000000");

	public final static DateTimeFormatter US_DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");

	public final static DateTimeFormatter DE_DATE_FORMATTER = DateTimeFormat.forPattern("dd.MM.yyyy");
	
}
