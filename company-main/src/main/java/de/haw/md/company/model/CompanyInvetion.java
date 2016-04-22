package de.haw.md.company.model;

import java.math.BigDecimal;

public class CompanyInvetion {
	
	private String name;
	
	private BigDecimal developmentTime;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getDevelopmentTime() {
		return developmentTime;
	}

	public void setDevelopmentTime(BigDecimal developmentTime) {
		this.developmentTime = developmentTime;
	}
	
}
