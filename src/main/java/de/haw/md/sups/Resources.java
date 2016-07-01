package de.haw.md.sups;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import au.com.bytecode.opencsv.CSVReader;

public class Resources {

	private Map<DateTime, BigDecimal> oilPrice = new HashMap<>();

	private Map<DateTime, BigDecimal> aluminiumPrice = new HashMap<>();
	private Map<DateTime, BigDecimal> goldPrice = new HashMap<>();
	private Map<DateTime, BigDecimal> nickelPrice = new HashMap<>();
	private Map<DateTime, BigDecimal> palladiumPrice = new HashMap<>();
	private Map<DateTime, BigDecimal> platinPrice = new HashMap<>();
	private Map<DateTime, BigDecimal> silberPrice = new HashMap<>();
	private Map<DateTime, BigDecimal> zinnPrice = new HashMap<>();
	private Map<DateTime, BigDecimal> copperPrice = new HashMap<>();

	public void readAllPrices() {
		readPrice("DCOILWTICO.csv", oilPrice, true);
		readPrice("Aluminiumpreis.csv", aluminiumPrice, false);
		readPrice("Goldpreis.csv", goldPrice, false);
		readPrice("Nickelpreis.csv", nickelPrice, false);
		readPrice("Palladiumpreis.csv", palladiumPrice, false);
		readPrice("Platinpreis.csv", platinPrice, false);
		readPrice("Silberpreis.csv", silberPrice, false);
		readPrice("Zinnpreis.csv", zinnPrice, false);
		readPrice("Kupferpreis.csv", copperPrice, false);
	}

	public List<Map<DateTime, BigDecimal>> getListOfResources() {
		List<Map<DateTime, BigDecimal>> resourcesList = new ArrayList<>();
		resourcesList.addAll(Arrays.asList(oilPrice, aluminiumPrice, goldPrice, nickelPrice, palladiumPrice, platinPrice, silberPrice, zinnPrice, copperPrice));
		return resourcesList;
	}

	private void readPrice(String fileName, Map<DateTime, BigDecimal> data, boolean usPrices) {
		ClassLoader classLoader = getClass().getClassLoader();
		try {
			final File file = new File(classLoader.getResource(fileName).getFile());
			final BufferedReader in = new BufferedReader(new FileReader(file));
			CSVReader reader = new CSVReader(in);
			String[] nextLine;
			int counter = 0;
			while ((nextLine = reader.readNext()) != null) {
				if (counter != 0) {
					BigDecimal bigDecimal;
					try {
						final BigDecimal price = new BigDecimal(nextLine[1]);
						if (usPrices) {
							bigDecimal = price.multiply(StaticVariables.US_TO_EURO).setScale(2, RoundingMode.HALF_UP);
						} else {
							bigDecimal = price;
						}
					} catch (NumberFormatException e) {
						bigDecimal = BigDecimal.ZERO;
					}
					if (usPrices) {
						data.put(DateTime.parse(nextLine[0], StaticVariables.US_DATE_FORMATTER), bigDecimal);
					} else {
						data.put(DateTime.parse(nextLine[0], StaticVariables.DE_DATE_FORMATTER), bigDecimal);
					}
				}
				counter++;
			}
			reader.close();
			if (data.size() > 0) {
				List<DateTime> date = sortKeyList(data.keySet());
				for (int i = 0; i < date.size(); i++) {
					if (i == 0 && data.get(date.get(i)).compareTo(BigDecimal.ZERO) == 0) {
						data.replace(date.get(i), data.get(date.get(i + 1)));
					} else if (data.get(date.get(i)).compareTo(BigDecimal.ZERO) == 0) {
						final BigDecimal newValue = data.get(date.get(i - 1)).add(data.get(date.get(i + 1)))
								.divide(new BigDecimal("2"), 2, RoundingMode.HALF_DOWN);
						data.replace(date.get(i), newValue);
					}
				}
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public BigDecimal getOilPriceByCount(int i) {
		if (this.oilPrice.size() <= 0)
			return BigDecimal.ZERO;
		return this.oilPrice.get(sortKeyList(this.oilPrice.keySet()).get(i));
	}

	public List<DateTime> sortKeyList(Set<DateTime> keySet) {
		List<DateTime> dateTimeList = new ArrayList<>();
		dateTimeList.addAll(keySet);
		Collections.sort(dateTimeList);
		return dateTimeList;
	}

	public Map<DateTime, BigDecimal> getCopperPrice() {
		return this.copperPrice;
	}

	public Map<DateTime, BigDecimal> getOilPrice() {
		return this.oilPrice;
	}

	public Map<DateTime, BigDecimal> getAluminiumPrice() {
		return aluminiumPrice;
	}

	public Map<DateTime, BigDecimal> getGoldPrice() {
		return goldPrice;
	}

	public Map<DateTime, BigDecimal> getNickelPrice() {
		return nickelPrice;
	}

	public Map<DateTime, BigDecimal> getPalladiumPrice() {
		return palladiumPrice;
	}

	public Map<DateTime, BigDecimal> getPlatinPrice() {
		return platinPrice;
	}

	public Map<DateTime, BigDecimal> getSilberPrice() {
		return silberPrice;
	}

	public Map<DateTime, BigDecimal> getZinnPrice() {
		return zinnPrice;
	}

}
