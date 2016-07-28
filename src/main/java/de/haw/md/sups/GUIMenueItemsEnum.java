package de.haw.md.sups;

public enum GUIMenueItemsEnum {
	
	OIL("Oil"),
	METAL("Metall"),
	NOBLE_METALS("Edelmetalle"),
	PROFIT("Gesammt Gewinn");
	
	private String name;
	
	private GUIMenueItemsEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
