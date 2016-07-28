package de.haw.md.sups;

import java.util.ArrayList;
import java.util.List;

public enum GUIChartHelperEnum {

	OIL("Oil", "Oil", ""),
	ALUMINIUM("Aluminium", "Metall", ""),
	GOLD("Gold", "Edelmetalle", ""),
	NICKEL("Nickel", "Metall", ""),
	PALLADIUM("Palladium", "Edelmetalle", ""),
	PLATIN("Platin", "Edelmetalle", ""),
	SILBER("Silber", "Edelmetalle", ""),
	ZINN("Zinn", "Metall", ""),
	KUPFER("Kupfer", "Metall", ""),
	REVENUE("VK Mobiltelefon","Company","getRevenue"),
	SELLED_PRODUCTS("Anz. Verkaufter Produkte","Company","getSelledProducts"),
	PROFIT("Gesammt Gewinn","Company","getProfit"),
	PRODUCTION_COST("Pruduktionskosten","Company","getProductionCost");
	
	
	private String name;
	private String group;
	private String methodName;
	
	private GUIChartHelperEnum(String name, String group, String methodName) {
		this.name = name;
		this.group = group;
		this.methodName = methodName;
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
	
	public static List<GUIChartHelperEnum> getChartElementsWithGroupName(String group){
		List<GUIChartHelperEnum> elementGroup = new ArrayList<>();
		for (GUIChartHelperEnum guiChartHelperEnum : GUIChartHelperEnum.values()) 
			if(guiChartHelperEnum.getGroup().equalsIgnoreCase(group))
				elementGroup.add(guiChartHelperEnum);
		return elementGroup;
	}
	
}
