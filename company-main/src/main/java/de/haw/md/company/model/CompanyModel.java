package de.haw.md.company.model;

import java.math.BigDecimal;
import java.util.List;

public class CompanyModel {
	
	private String companyID;
	
	private List<CompanyProduct> products;
	
	private List<CompanyInvetion> inventions;
	
	private BigDecimal reputation;
	
	private BigDecimal budget;
	
	private BigDecimal stockKap;
	
	private BigDecimal markedVolume;

	public List<CompanyProduct> getProducts() {
		return products;
	}

	public void setProducts(List<CompanyProduct> products) {
		this.products = products;
	}

	public List<CompanyInvetion> getInventions() {
		return inventions;
	}

	public void setInventions(List<CompanyInvetion> inventions) {
		this.inventions = inventions;
	}

	public BigDecimal getReputation() {
		return reputation;
	}

	public void setReputation(BigDecimal reputation) {
		this.reputation = reputation;
	}

	public String getCompanyID() {
		return companyID;
	}

	public void setCompanyID(String companyID) {
		this.companyID = companyID;
	}

	public BigDecimal getBudget() {
		return budget;
	}

	public void setBudget(BigDecimal budget) {
		this.budget = budget;
	}

	public BigDecimal getStockKap() {
		return stockKap;
	}

	public void setStockKap(BigDecimal stockKap) {
		this.stockKap = stockKap;
	}

	public BigDecimal getMarkedVolume() {
		return markedVolume;
	}

	public void setMarkedVolume(BigDecimal markedVolume) {
		this.markedVolume = markedVolume;
	}

}
