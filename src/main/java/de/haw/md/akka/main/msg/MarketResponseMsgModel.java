package de.haw.md.akka.main.msg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "company", "type", "date", "value" })
public class MarketResponseMsgModel {

	@JsonProperty("company")
	private String company;
	@JsonProperty("type")
	private String type;
	@JsonProperty("date")
	private String date;
	@JsonProperty("value")
	private String value;

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

	@JsonProperty("value")
	public String getValue() {
		return value;
	}

	@JsonProperty("value")
	public void setValue(String value) {
		this.value = value;
	}

}
