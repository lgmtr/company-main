package de.haw.md.akka.main.msg;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "company", "shareValue", "shareVolume"})
public class CompanyShareMsgModel implements Comparable<CompanyShareMsgModel>, Cloneable {
	
	@JsonProperty("company")
	private String company;
	
	@JsonProperty("shareValue")
	private String shareValue;
	
	@JsonProperty("shareVolume")
	private String shareVolume;
	
	public CompanyShareMsgModel() {
		this("", "");
	}

	public CompanyShareMsgModel(String company, String shareValue) {
		this.company = company;
		this.shareValue = shareValue;
	}
	
	@JsonProperty("company")
	public String getCompany() {
		return company;
	}

	@JsonProperty("company")
	public void setCompany(String company) {
		this.company = company;
	}
	
	@JsonProperty("shareValue")
	public String getShareValue() {
		return shareValue;
	}

	@JsonProperty("shareValue")
	public void setShareValue(String shareValue) {
		this.shareValue = shareValue;
	}

	@Override
	public int compareTo(CompanyShareMsgModel scmm) {
		return new BigDecimal(shareValue).compareTo(new BigDecimal(scmm.getShareValue()));
	}
	
	public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

	@JsonProperty("shareVolume")
	public String getShareVolume() {
		return shareVolume;
	}

	@JsonProperty("shareVolume")
	public void setShareVolume(String shareVolume) {
		this.shareVolume = shareVolume;
	}

}
