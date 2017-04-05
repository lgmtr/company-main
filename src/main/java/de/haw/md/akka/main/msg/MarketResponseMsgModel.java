package de.haw.md.akka.main.msg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "company", "type", "date", "revenue", "selledProducts", "profit", "productionCost" })
public class MarketResponseMsgModel {

	@JsonProperty("company")
	private String company;
	@JsonProperty("type")
	private String type;
	@JsonProperty("date")
	private String date;
	@JsonProperty("revenue")
	private String revenue;
	@JsonProperty("selledProducts")
	private String selledProducts;
	@JsonProperty("profit")
	private String profit;
	@JsonProperty("productionCost")
	private String productionCost;
	
	@JsonProperty("company")
	public String getCompany() {
		return company;
	}

	@JsonProperty("company")
	public void setCompany(String company) {
		this.company = company;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("date")
	public String getDate() {
		return date;
	}

	@JsonProperty("date")
	public void setDate(String date) {
		this.date = date;
	}

	@JsonProperty("revenue")
	public String getRevenue() {
		return revenue;
	}

	@JsonProperty("revenue")
	public void setRevenue(String value) {
		this.revenue = value;
	}

	@JsonProperty("selledProducts")
	public String getSelledProducts() {
		return selledProducts;
	}

	@JsonProperty("selledProducts")
	public void setSelledProducts(String selledProducts) {
		this.selledProducts = selledProducts;
	}

	@JsonProperty("profit")
	public String getProfit() {
		return profit;
	}

	@JsonProperty("profit")
	public void setProfit(String profit) {
		this.profit = profit;
	}

	@JsonProperty("productionCost")
	public String getProductionCost() {
		return productionCost;
	}

	@JsonProperty("productionCost")
	public void setProductionCost(String productionCost) {
		this.productionCost = productionCost;
	}

}
