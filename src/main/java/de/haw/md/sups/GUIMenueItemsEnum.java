package de.haw.md.sups;

public enum GUIMenueItemsEnum {
	
	OIL("Oil"),
	METAL("Metall"),
	NOBLE_METALS("Edelmetalle"),
	PROFIT("Gesammt Gewinn"),
	REVENUE("VK Mobiltelefon"),
	SELLED_PRODUCTS("Anz. Verkaufter Produkte"),
	PRODUCTION_COST("Pruduktionskosten"),
	MARKET_SHARES("Marktanteile");
	
	private String name;
	
	private GUIMenueItemsEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
