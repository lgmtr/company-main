package de.haw.md.company.main;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CompanyMainTwo {

	public static final int ANZ_ZYKLEN = 135;

	public static final BigDecimal PROGNOSE = new BigDecimal("10000000");

	public static final BigDecimal MAX_PERCENT = new BigDecimal("100");

	public int currentZyklus = 0;

	private void simulate() {
		Company c1 = new Company(this, new BigDecimal("300"), new BigDecimal("450"), new BigDecimal("4000000"), new BigDecimal("1000000"), new BigDecimal("0.2"), new BigDecimal("0.0001"));
		Company c2 = new Company(this, new BigDecimal("200"), new BigDecimal("300"), new BigDecimal("2500000"), new BigDecimal("1000000"), new BigDecimal("0.1"), new BigDecimal("0.0001"));
		for (int i = 0; i < ANZ_ZYKLEN; i++) {
			BigDecimal marketShare;
			if(i == 0) {
				marketShare = generateRandomBigDecimalFromRange(new BigDecimal("0.4"), new BigDecimal("0.6"));
			}else {
				marketShare = calculateFromVK(c1.getCalcPreis()[currentZyklus - 1], c2.getCalcPreis()[currentZyklus - 1]);
			}
			c1.simulate(marketShare);
			c2.simulate(BigDecimal.ONE.subtract(marketShare));
			System.out.println("Woche: " + currentZyklus);
			System.out.println("C1 | Preis p.P.: " + c1.getCalcPreis()[currentZyklus] + "\t | Kosten p.P.: "
					+ c1.getPkPreis()[currentZyklus] + "\t | Gewinn p.P.: " + c1.getGewinn()[currentZyklus] + "\t | Verk. p.W: " + c1.getVerkaufteProd()[currentZyklus]
					+ "\t | Ges.Gewinn: " + c1.getGesGew() + "\t | Preisaenderung: " + c1.getPa());
			System.out.println("C2 | Preis p.P.: " + c2.getCalcPreis()[currentZyklus] + "\t | Kosten p.P.: "
					+ c2.getPkPreis()[currentZyklus] + "\t | Gewinn p.P.: " + c2.getGewinn()[currentZyklus] + "\t | Verk. p.W: " + c2.getVerkaufteProd()[currentZyklus]
					+ "\t | Ges.Gewinn: " + c2.getGesGew() + "\t | Preisaenderung: " + c2.getPa());
			System.out.println("=========================================================================================================================================================");
			currentZyklus++;
		}
	}

	private BigDecimal calculateFromVK(BigDecimal vk1, BigDecimal vk2) {
		if(vk1.compareTo(vk2) <= 0){
			return calcMarketShare(vk1, vk2);
		} else {
			return BigDecimal.ONE.subtract(calcMarketShare(vk2, vk1));
		}
	}

	private BigDecimal calcMarketShare(BigDecimal small, BigDecimal big) {
		BigDecimal diff = big.divide(small, 5, RoundingMode.HALF_EVEN);
		if(diff.compareTo(new BigDecimal("1.005")) <=0){
			return new BigDecimal("0.5");
		} else if(diff.compareTo(new BigDecimal("1.1")) <=0){
			return new BigDecimal("0.6");
		} else if(diff.compareTo(new BigDecimal("1.2")) <=0){
			return new BigDecimal("0.7");
		} else if(diff.compareTo(new BigDecimal("1.4")) <=0){
			return new BigDecimal("0.8");
		} else if(diff.compareTo(new BigDecimal("1.8")) <=0){
			return new BigDecimal("0.9");
		} else{
			return new BigDecimal("0.95");
		}
	}

	public static void main(String[] args) {
		CompanyMainTwo cmt = new CompanyMainTwo();
		cmt.simulate();
	}

	public static BigDecimal generateRandomBigDecimalFromRange(BigDecimal min, BigDecimal max) {
		BigDecimal randomBigDecimal = min.add(new BigDecimal(Math.random()).multiply(max.subtract(min)));
		return randomBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
}
