package de.haw.md.company.main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CompanyMain {

	private BigDecimal prognose = new BigDecimal("10000000");

	private List<BigDecimal> angebot;

	private List<BigDecimal> nachfrage;

	private List<BigDecimal> saettigung;

	private List<BigDecimal> lieferbarkeit;

	private List<BigDecimal> produktion;

	private List<BigDecimal> lagerbestand;
	
	private List<BigDecimal> verkaufteProdukte;

	private BigDecimal werbung = new BigDecimal("1.0005");

	private int currentZyklus = 0;

	private BigDecimal prodCap = new BigDecimal("100000");

	private BigDecimal lagerProdCap = new BigDecimal("200000");

	private void calcAngebot() {
		if (currentZyklus == 0) 
			angebot = new ArrayList<>();
		angebot.add(lagerbestand.get(currentZyklus));
		
	}

	private void calcProduktion() {
		if (currentZyklus == 0) {
			produktion = new ArrayList<>();
			produktion.add(prodCap);
		} else {
			final BigDecimal nachfrageInProzent = nachfrage.get(currentZyklus - 1).divide(new BigDecimal("100"));
			final BigDecimal prognoseCalcNachfrage = prognose.multiply(nachfrageInProzent);
			prognoseCalcNachfrage.setScale(0, RoundingMode.HALF_UP);
			final boolean b = prognoseCalcNachfrage.compareTo(prodCap) > 0;
			if (b) {
				produktion.add(prodCap.setScale(0, RoundingMode.HALF_UP));
			} else {
				produktion.add(prognoseCalcNachfrage.setScale(0, RoundingMode.HALF_EVEN));
			}
		}
	}

	private void calcNachfrage() {
		if (currentZyklus == 0) {
			nachfrage = new ArrayList<>();
			nachfrage.add(new BigDecimal("100"));
		} else {
			final BigDecimal subtract = new BigDecimal("100").subtract(saettigung.get(currentZyklus - 1));
			final BigDecimal multiply = subtract.multiply(werbung);
			if (multiply.compareTo(new BigDecimal("100")) > 0) {
				nachfrage.add(new BigDecimal("100").setScale(4));
			} else if (multiply.compareTo(new BigDecimal("1")) < 0) {
				nachfrage.add(new BigDecimal("1.0000").setScale(4));
			} else {
				nachfrage.add(multiply.setScale(4, RoundingMode.HALF_UP));
			}
		}
	}

	private void calcSaettigung() {
		if (currentZyklus == 0) {
			saettigung = new ArrayList<>();
			saettigung.add(BigDecimal.ZERO.setScale(4));
		} else {
			BigDecimal sumVerkauft = BigDecimal.ZERO;
			for (BigDecimal verkauft : verkaufteProdukte) {
				sumVerkauft = sumVerkauft.add(verkauft);
			}
			final BigDecimal calcSaettigung = sumVerkauft.divide(prognose).multiply(new BigDecimal("100")).setScale(4, RoundingMode.HALF_DOWN);
			if (calcSaettigung.compareTo(new BigDecimal("99")) > 0) {
				saettigung.add(new BigDecimal("99.0000").setScale(4));
			} else {
				saettigung.add(calcSaettigung);
			}
		}
	}

	private void calcLager() {
		if (currentZyklus == 0) {
			lagerbestand = new ArrayList<>();
			lagerbestand.add(prodCap);
		} else {
			if (lagerbestand.get(currentZyklus - 1).compareTo(lagerProdCap) >= 0) {
				lagerbestand.add(lagerProdCap);
				produktion.set(currentZyklus, BigDecimal.ZERO.setScale(0));
			} else {
				final BigDecimal b = lagerbestand.get(currentZyklus-1).add(produktion.get(currentZyklus));
				if(b.compareTo(lagerProdCap) >= 0){
					lagerbestand.add(lagerProdCap);
					produktion.set(currentZyklus, BigDecimal.ZERO.setScale(0));
				}else{
					lagerbestand.add(b);
				}
			}
		}
	}
	
	private void simSale(){
		if(currentZyklus == 0)
			verkaufteProdukte = new ArrayList<>();
		verkaufteProdukte.add(lagerbestand.get(currentZyklus).multiply(nachfrage.get(currentZyklus).divide(new BigDecimal("100"))).setScale(0, RoundingMode.HALF_DOWN));
		lagerbestand.set(currentZyklus, lagerbestand.get(currentZyklus).subtract(verkaufteProdukte.get(currentZyklus)));
		
	}

	private void simulate() {
		for (int i = 0; i < 200; i++) {
			calcProduktion();
			calcNachfrage();
			calcSaettigung();
			calcLager();
			calcAngebot();
			simSale();
			currentZyklus++;
		}
	}

	private void print() {
		for (int i = 0; i < currentZyklus; i++) {
			System.out.println("Zyklus: " + i + "\t / Produktion: " + produktion.get(i) + "\t / Nachfrage: " + nachfrage.get(i) + "\t / Sättigung: "
					+ saettigung.get(i) + "\t / Lager: " + lagerbestand.get(i) + "\t / Angebot: " + angebot.get(i) + "\t / Verkauft: " + verkaufteProdukte.get(i));
		}
		BigDecimal verkauft = BigDecimal.ZERO;
		for (BigDecimal verkauf : verkaufteProdukte) {
			verkauft = verkauft.add(verkauf);
		}
		System.out.println("Verkauft: " + verkauft);
		System.out.println("Prognose: " + prognose);
	}

	public static void main(String[] args) {
		CompanyMain cm = new CompanyMain();
		cm.simulate();
		cm.print();
	}

}
