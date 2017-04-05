package de.haw.md.akka.main.msg;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "type", "companyShare", "date"})
public class MarketShareMsgModel {
	
	@JsonProperty("type")
	private String type;
	
	@JsonProperty("companyShare")
	private List<CompanyShareMsgModel> companyShareMsgModels;
	
	@JsonProperty("date")
	private String date;

	@JsonProperty("companyShare")
	public List<CompanyShareMsgModel> getCompanyShareMsgModels() {
		return companyShareMsgModels;
	}

	@JsonProperty("companyShare")
	public void setCompanyShareMsgModels(List<CompanyShareMsgModel> companyShareMsgModels) {
		this.companyShareMsgModels = companyShareMsgModels;
	}

	@JsonProperty("date")
	public String getDate() {
		return date;
	}

	@JsonProperty("date")
	public void setDate(String date) {
		this.date = date;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}
	
	public CompanyShareMsgModel findShareByCompanyName(String name){
		for (CompanyShareMsgModel companyShareMsgModel : companyShareMsgModels) 
			if(companyShareMsgModel.getCompany().equals(name))
				return companyShareMsgModel;
		return null;
	}

}
