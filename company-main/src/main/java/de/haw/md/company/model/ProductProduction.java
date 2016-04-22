package de.haw.md.company.model;

import java.math.BigDecimal;

public class ProductProduction {
	
	private BigDecimal inProduction;
	
	private BigDecimal productionInOneMonth;
	
	public BigDecimal getInProduction() {
		return inProduction;
	}

	public void setInProduction(BigDecimal inProduction) {
		this.inProduction = inProduction;
	}

	public BigDecimal getProductionInOneMonth() {
		return productionInOneMonth;
	}

	public void setProductionInOneMonth(BigDecimal productionInOneMonth) {
		this.productionInOneMonth = productionInOneMonth;
	}

}
