package de.haw.md.company.main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CompanyMainTwo {

	public static final int ANZ_ZYKLEN = 168;

	public static final BigDecimal PROGNOSE = new BigDecimal("10000000");

	public static final BigDecimal MAX_PERCENT = new BigDecimal("100");

	public int currentZyklus = 0;

	private Company c1;

	private Company c2;

	private Company c3;

	private Company c4;

	private Company c5;

	public void simulate() {
		c1 = new Company(this, new BigDecimal("280"), new BigDecimal("320"), new BigDecimal("17500000"), new BigDecimal("1000000"), new BigDecimal("0.32"),
				new BigDecimal("0.0001"), new BigDecimal("3"));
		c2 = new Company(this, new BigDecimal("220"), new BigDecimal("260"), new BigDecimal("13500000"), new BigDecimal("1000000"), new BigDecimal("0.33"),
				new BigDecimal("0.0001"), new BigDecimal("5"));
		c3 = new Company(this, new BigDecimal("280"), new BigDecimal("320"), new BigDecimal("17500000"), new BigDecimal("1000000"), new BigDecimal("0.31"),
				new BigDecimal("0.0001"), new BigDecimal("3"));
		c4 = new Company(this, new BigDecimal("220"), new BigDecimal("250"), new BigDecimal("20500000"), new BigDecimal("1000000"), new BigDecimal("0.37"),
				new BigDecimal("0.0001"), new BigDecimal("8"));
		c5 = new Company(this, new BigDecimal("290"), new BigDecimal("320"), new BigDecimal("17500000"), new BigDecimal("1000000"), new BigDecimal("0.35"),
				new BigDecimal("0.0001"), new BigDecimal("5"));
		for (int i = 0; i < ANZ_ZYKLEN; i++) {
			List<BigDecimal> marketShare;
			if (i == 0) {
				c1.simulate(new BigDecimal("0.2"));
				c2.simulate(new BigDecimal("0.2"));
				c3.simulate(new BigDecimal("0.2"));
				c4.simulate(new BigDecimal("0.2"));
				c5.simulate(new BigDecimal("0.2"));
			} else {
				marketShare = calculateFromVK(c1.getCalcPreis()[currentZyklus - 1], c2.getCalcPreis()[currentZyklus - 1], c3.getCalcPreis()[currentZyklus - 1],
						c4.getCalcPreis()[currentZyklus - 1], c5.getCalcPreis()[currentZyklus - 1]);
				c1.simulate(marketShare.get(0));
				c2.simulate(marketShare.get(1));
				c3.simulate(marketShare.get(2));
				c4.simulate(marketShare.get(3));
				c5.simulate(marketShare.get(4));
			}
			System.out.println("Woche: " + currentZyklus);
			System.out.println("C1 | Preis p.P.: " + c1.getCalcPreis()[currentZyklus] + "\t | Kosten p.P.: " + c1.getPkPreis()[currentZyklus]
					+ "\t | Gewinn p.P.: " + c1.getGewinn()[currentZyklus] + "\t | Verk. p.W: " + c1.getVerkaufteProd()[currentZyklus] + "\t | Ges.Gewinn: "
					+ c1.getGesGew() + "\t | Preisaenderung: " + c1.getPa());
			System.out.println("C2 | Preis p.P.: " + c2.getCalcPreis()[currentZyklus] + "\t | Kosten p.P.: " + c2.getPkPreis()[currentZyklus]
					+ "\t | Gewinn p.P.: " + c2.getGewinn()[currentZyklus] + "\t | Verk. p.W: " + c2.getVerkaufteProd()[currentZyklus] + "\t | Ges.Gewinn: "
					+ c2.getGesGew() + "\t | Preisaenderung: " + c2.getPa());
			System.out.println("C3 | Preis p.P.: " + c3.getCalcPreis()[currentZyklus] + "\t | Kosten p.P.: " + c3.getPkPreis()[currentZyklus]
					+ "\t | Gewinn p.P.: " + c3.getGewinn()[currentZyklus] + "\t | Verk. p.W: " + c3.getVerkaufteProd()[currentZyklus] + "\t | Ges.Gewinn: "
					+ c3.getGesGew() + "\t | Preisaenderung: " + c3.getPa());
			System.out.println("C4 | Preis p.P.: " + c4.getCalcPreis()[currentZyklus] + "\t | Kosten p.P.: " + c4.getPkPreis()[currentZyklus]
					+ "\t | Gewinn p.P.: " + c4.getGewinn()[currentZyklus] + "\t | Verk. p.W: " + c4.getVerkaufteProd()[currentZyklus] + "\t | Ges.Gewinn: "
					+ c4.getGesGew() + "\t | Preisaenderung: " + c4.getPa());
			System.out.println("C5 | Preis p.P.: " + c5.getCalcPreis()[currentZyklus] + "\t | Kosten p.P.: " + c5.getPkPreis()[currentZyklus]
					+ "\t | Gewinn p.P.: " + c5.getGewinn()[currentZyklus] + "\t | Verk. p.W: " + c5.getVerkaufteProd()[currentZyklus] + "\t | Ges.Gewinn: "
					+ c5.getGesGew() + "\t | Preisaenderung: " + c5.getPa());
			System.out
					.println("=========================================================================================================================================================");
			currentZyklus++;
		}
	}

	private List<BigDecimal> calculateFromVK(BigDecimal... vk) {
		List<BigDecimal> marketShareList = new ArrayList<>();
		BigDecimal sumPrice = BigDecimal.ZERO;
		BigDecimal fixShare = (new BigDecimal("100").multiply(new BigDecimal("2"))).divide(new BigDecimal("3"), 5, RoundingMode.HALF_DOWN);
		BigDecimal variableShare = new BigDecimal("100").subtract(fixShare);
		for (int i = 0; i < vk.length; i++) {
			sumPrice = sumPrice.add(vk[i]);
		}
		for (int i = 0; i < vk.length; i++) {
			BigDecimal fixSharePerComp = fixShare.divide(new BigDecimal(vk.length), 5, RoundingMode.HALF_DOWN);
			BigDecimal sumPriceOnePercent = sumPrice.divide(new BigDecimal("100"), 10, RoundingMode.HALF_DOWN);
			BigDecimal percentPerPrice = vk[i].divide(sumPriceOnePercent, 10, RoundingMode.HALF_DOWN);
			BigDecimal variableShareOnePercent = variableShare.divide(new BigDecimal("100"), 10, RoundingMode.HALF_DOWN);
			BigDecimal variableSharePerComp = percentPerPrice.multiply(variableShareOnePercent);
			marketShareList.add((fixSharePerComp.add(variableSharePerComp)).divide(new BigDecimal("100"), 10, RoundingMode.HALF_DOWN));
		}
		return marketShareList;
	}

	public static void main(String[] args) {
		CompanyMainTwo cmt = new CompanyMainTwo();
		cmt.simulate();
	}

	public static BigDecimal generateRandomBigDecimalFromRange(BigDecimal min, BigDecimal max) {
		BigDecimal randomBigDecimal = min.add(new BigDecimal(Math.random()).multiply(max.subtract(min)));
		return randomBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public Company getC1() {
		return c1;
	}

	public Company getC2() {
		return c2;
	}

	public Company getC3() {
		return c3;
	}

	public Company getC4() {
		return c4;
	}

	public Company getC5() {
		return c5;
	}
}
