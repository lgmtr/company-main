package de.haw.md.sups;

import java.util.ArrayList;
import java.util.List;

public enum GUIChartHelperEnum {

	OIL("Oil", "Oil", "", "Preis pro Barrel"),
	ALUMINIUM("Aluminium", "Metall", "", "Preis pro Tonne"),
	GOLD("Gold", "Edelmetalle", "", "Preis pro Feinunze"),
	NICKEL("Nickel", "Metall", "", "Preis pro Tonne"),
	PALLADIUM("Palladium", "Edelmetalle", "", "Preis pro Feinunze"),
	PLATIN("Platin", "Edelmetalle", "", "Preis pro Feinunze"),
	SILBER("Silber", "Edelmetalle", "", "Preis pro Feinunze"),
	ZINN("Zinn", "Metall", "", "Preis pro Tonne"),
	KUPFER("Kupfer", "Metall", "", "Preis pro Tonne"),
	REVENUE("VK Mobiltelefon","Company","revenue", "Preis pro Mobiltelefon"),
	SELLED_PRODUCTS("Anz. Verkaufter Produkte","Company","selledProducts", "Anz. Verkaufter Produkte"),
	PROFIT("Gesammt Gewinn","Company","profit", "Gesammt Gewinn"),
	PRODUCTION_COST("Pruduktionskosten","Company","productionCost", "Produktionskosten pro Mobiltelefon"),
	MARKET_SHARES("Marktanteile", "Marktanteile", "", "Marktanteile pro Unternehmen");
	
	
	private String name;
	private String group;
	private String methodName;
	private String identifier;
	
	private GUIChartHelperEnum(String name, String group, String methodName, String identifier) {
		this.name = name;
		this.group = group;
		this.methodName = methodName;
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public String getGroup() {
		return group;
	}

	public String getMethodName() {
		return methodName;
	}
	
	public static String getIdetifierByName(String name){
		for (GUIChartHelperEnum gche : GUIChartHelperEnum.values()) 
			if(name.equals(gche.name) || name.equals(gche.group))
				return gche.identifier;
		return "";
	}
	
	public static List<GUIChartHelperEnum> getChartElementsWithGroupName(String group){
		List<GUIChartHelperEnum> elementGroup = new ArrayList<>();
		for (GUIChartHelperEnum guiChartHelperEnum : GUIChartHelperEnum.values()) 
			if(guiChartHelperEnum.getGroup().equalsIgnoreCase(group))
				elementGroup.add(guiChartHelperEnum);
		return elementGroup;
	}
	
}
