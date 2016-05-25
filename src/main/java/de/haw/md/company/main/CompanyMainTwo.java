package de.haw.md.company.main;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CompanyMainTwo {

	private static final int ANZ_ZYKLEN = 1000;

	private static final BigDecimal PROGNOSE = new BigDecimal("10000000");

	private static final BigDecimal EK_MAT_X = new BigDecimal("200");

	private static final BigDecimal EK_MAT_Y = new BigDecimal("300");

	private static final BigDecimal FIX_KOSTEN = new BigDecimal("1000000");

	private static final BigDecimal ANZ_PRODUKTE_ZYKLUS = new BigDecimal("100000");

	private static final BigDecimal AUFSCHLAG_0 = new BigDecimal("0.2");

	private static final BigDecimal PAS = new BigDecimal("0.001");

	private static final BigDecimal MAX_PERCENT = new BigDecimal("100");

	private BigDecimal[] nachfrage = new BigDecimal[ANZ_ZYKLEN];

	private BigDecimal[] saettigung = new BigDecimal[ANZ_ZYKLEN];

	private BigDecimal[] ekMat = new BigDecimal[ANZ_ZYKLEN];

	private BigDecimal[] pkPreis = new BigDecimal[ANZ_ZYKLEN];

	private BigDecimal[] vkPreis = new BigDecimal[ANZ_ZYKLEN];

	private BigDecimal sumProdukte = BigDecimal.ZERO;

	private BigDecimal pa = BigDecimal.ZERO;

	private int currentZyklus = 0;

	private boolean stop = false;

	private BigDecimal calcSaettigung() {
		final BigDecimal multiply = sumProdukte.multiply(new BigDecimal("100"));
		final BigDecimal divide = multiply.divide(PROGNOSE, 4, RoundingMode.HALF_UP);
		return divide;
	}

	private BigDecimal calcNachfrage(BigDecimal pa, BigDecimal saettigung) {
		BigDecimal a = BigDecimal.ONE.add(AUFSCHLAG_0);
		final BigDecimal add = a.add(pa);
		final BigDecimal multiply = MAX_PERCENT.multiply(add);
		final BigDecimal divide = multiply.divide(a, RoundingMode.HALF_DOWN);
		return divide.subtract(saettigung);
	}

	private BigDecimal calcProdKost() {
		BigDecimal varKost = generateRandomBigDecimalFromRange(EK_MAT_X, EK_MAT_Y);
		return varKost.add(FIX_KOSTEN.divide(ANZ_PRODUKTE_ZYKLUS));
	}

	private BigDecimal calcPreis(BigDecimal pa) {
		BigDecimal nachfrage = calcNachfrage(pa, calcSaettigung());
		if (nachfrage.compareTo(BigDecimal.ZERO) <= 0) {
			this.pa = this.pa.add(PAS);
			calcPreis(this.pa);
		} else if (AUFSCHLAG_0.subtract(this.pa).compareTo(BigDecimal.ZERO) <= 0) {
			stop = true;
			return BigDecimal.ZERO;
		} else if (nachfrage.compareTo(MAX_PERCENT) > 0) {
			this.pa = this.pa.subtract(PAS);
			calcPreis(this.pa);
		}
		pkPreis[currentZyklus] = calcProdKost();
		BigDecimal preisAenderung = BigDecimal.ONE.add(AUFSCHLAG_0.subtract(this.pa));
		return EK_MAT_Y.multiply(preisAenderung).setScale(2, RoundingMode.HALF_DOWN);
	}

	private void simulate() {
		for (int i = 0; i < ANZ_ZYKLEN; i++) {
			final BigDecimal calcPreis = calcPreis(this.pa);
			final BigDecimal gewinn = calcPreis.subtract(pkPreis[i]);
			System.out.println("Zyklus: " + i + "\t | VK Preis: " + calcPreis + "\t | PK Preis: " + pkPreis[i] + "\t | Gewinn: "
					+ gewinn + "\t | Preisaenderung: " + this.pa);
			sumProdukte = sumProdukte.add(ANZ_PRODUKTE_ZYKLUS);
			currentZyklus++;
			if(gewinn.compareTo(new BigDecimal("-50")) <= 0)
				break;
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
