package de.haw.md.sups;

import java.math.BigDecimal;

public enum CompanyValuesEnum {

	COST_MAN_HOUR(new BigDecimal("20"), new BigDecimal("40"), true),
	PROD_MAN_HOUR(new BigDecimal("2"), new BigDecimal("4"), false),
	BONUS(new BigDecimal("1.6"), new BigDecimal("1.9"), false),
	SUPPLIER_DISCOUNT(new BigDecimal("1.1"), new BigDecimal("1.3"), false),
	FIX_COST(new BigDecimal("5000000"), new BigDecimal("7000000"), false),
	PRODUCTION_LINES(new BigDecimal("4"), new BigDecimal("7"), true),
	PRODUCTION_LINES_CAP(new BigDecimal("3000"), new BigDecimal("4000"), true),
	MONTHLY_COSTS(new BigDecimal("10000000"), new BigDecimal("30000000"), false);
	
	private BigDecimal min;
	private BigDecimal max;
	private boolean shortScale;
	
	private CompanyValuesEnum(BigDecimal min, BigDecimal max, boolean shortScale) {
		this.min = min;
		this.max = max;
		this.shortScale = shortScale;
	}
	
	public BigDecimal getRandomValue(){
		if(shortScale)
			return StaticVariables.generateRandomBigDecimalFromRangeScale0(min, max);
		return StaticVariables.generateRandomBigDecimalFromRange(min, max);
	}
}
