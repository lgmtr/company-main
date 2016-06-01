package de.haw.md.company.main;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Company {

	private BigDecimal ekMatX;

	private BigDecimal ekMatY;

	private BigDecimal fixKosten;

	private BigDecimal anzProdZyk;

	private BigDecimal aufschlag0;

	private BigDecimal pas;

	private BigDecimal maxPK;

	private CompanyMainTwo cmt;

	private BigDecimal currentMarketShare;

	private BigDecimal[] verkaufteProd = new BigDecimal[CompanyMainTwo.ANZ_ZYKLEN];

	private BigDecimal[] pkPreis = new BigDecimal[CompanyMainTwo.ANZ_ZYKLEN];

	private BigDecimal gesGew = BigDecimal.ZERO;

	private BigDecimal[] calcPreis = new BigDecimal[CompanyMainTwo.ANZ_ZYKLEN];

	private BigDecimal[] gewinn = new BigDecimal[CompanyMainTwo.ANZ_ZYKLEN];

	private BigDecimal pa = BigDecimal.ZERO;

	private BigDecimal prodStufen;

	public Company(CompanyMainTwo cmt, BigDecimal ekMatX, BigDecimal ekMatY, BigDecimal fixKosten, BigDecimal anzProdZyk, BigDecimal aufschlag0,
			BigDecimal pas, BigDecimal prodStufen) {
		this.cmt = cmt;
		this.ekMatX = ekMatX;
		this.ekMatY = ekMatY;
		this.fixKosten = fixKosten;
		this.anzProdZyk = anzProdZyk;
		this.aufschlag0 = aufschlag0;
		this.pas = pas;
		this.prodStufen = prodStufen;
		this.maxPK = ekMatY.add(fixKosten.divide(anzProdZyk, 2, RoundingMode.HALF_UP));
	}

	private BigDecimal calcNachfrage(BigDecimal pa) {
		if (cmt.currentZyklus == 0)
			return CompanyMainTwo.MAX_PERCENT;
		final BigDecimal divide = BigDecimal.ONE.divide(new BigDecimal(cmt.currentZyklus).multiply(new BigDecimal("3")), 20, RoundingMode.HALF_DOWN);
		final BigDecimal multiply = divide.multiply(CompanyMainTwo.MAX_PERCENT, MathContext.DECIMAL128);
		BigDecimal a = BigDecimal.ONE.add(pa);
		final BigDecimal multiply2 = multiply.multiply(a.multiply(new BigDecimal("2")), MathContext.DECIMAL128);
		return multiply2;
	}

	private BigDecimal calcProdKost(boolean topProd, BigDecimal prognoseVerkauf) {
		BigDecimal varKost = CompanyMainTwo.generateRandomBigDecimalFromRange(ekMatX, ekMatY);
		if (topProd)
			return varKost.add(fixKosten.divide(anzProdZyk, RoundingMode.HALF_UP));
		final BigDecimal prodMprodStufe = prognoseVerkauf.multiply(prodStufen);
		final BigDecimal divideMaxProd = prodMprodStufe.divide(anzProdZyk, 0, RoundingMode.DOWN);
		final BigDecimal addOne = divideMaxProd.add(BigDecimal.ONE);
		final BigDecimal multiplyWithFixKost = fixKosten.multiply(addOne);
		BigDecimal fixKost = multiplyWithFixKost.divide(prodStufen, 0, RoundingMode.UP);
		// return varKost.add(fixKosten.divide(prognoseVerkauf,
		// RoundingMode.HALF_UP));
		return varKost.add(fixKost.divide(prognoseVerkauf, RoundingMode.HALF_UP));
	}

	private BigDecimal calcPreis(BigDecimal pa) {
		BigDecimal nachfrage;
		BigDecimal prognoseVerkauf;
		BigDecimal currentVK;
		boolean stop = false;
		do {
			nachfrage = calcNachfrage(pa);
			final BigDecimal einfProgVerk = CompanyMainTwo.PROGNOSE.multiply(nachfrage.divide(CompanyMainTwo.MAX_PERCENT, 20, RoundingMode.HALF_DOWN))
					.setScale(0, RoundingMode.HALF_DOWN);
			prognoseVerkauf = einfProgVerk.multiply(currentMarketShare).setScale(0, RoundingMode.HALF_DOWN);
			if (prognoseVerkauf.compareTo(anzProdZyk) > 0) {
				pkPreis[cmt.currentZyklus] = calcProdKost(true, prognoseVerkauf);
				BigDecimal preisAenderung = BigDecimal.ONE.add(aufschlag0.subtract(this.pa));
				verkaufteProd[cmt.currentZyklus] = anzProdZyk;
				return maxPK.multiply(preisAenderung).setScale(2, RoundingMode.HALF_DOWN);
			} else {
				pkPreis[cmt.currentZyklus] = calcProdKost(false, prognoseVerkauf);
				BigDecimal preisAenderung = BigDecimal.ONE.add(aufschlag0.subtract(this.pa));
				currentVK = maxPK.multiply(preisAenderung).setScale(2, RoundingMode.HALF_DOWN);
				final BigDecimal abb = this.pa.subtract(aufschlag0);
				if (abb.compareTo(new BigDecimal("0.2")) >= 0 || currentVK.compareTo(pkPreis[cmt.currentZyklus].subtract(BigDecimal.TEN)) > 0) {
					stop = true;
				} else if (currentVK.compareTo(pkPreis[cmt.currentZyklus].subtract(BigDecimal.TEN)) <= 0) {
					this.pa = this.pa.add(pas);
				}
			}

		} while (!stop);
		verkaufteProd[cmt.currentZyklus] = prognoseVerkauf;
		return currentVK;
	}

	public void simulate(BigDecimal currentMarketShare) {
		this.currentMarketShare = currentMarketShare;
		calcPreis[cmt.currentZyklus] = calcPreis(this.pa);
		gewinn[cmt.currentZyklus] = calcPreis[cmt.currentZyklus].subtract(pkPreis[cmt.currentZyklus]);
		gesGew = gesGew.add(gewinn[cmt.currentZyklus].multiply(verkaufteProd[cmt.currentZyklus])).setScale(2, RoundingMode.HALF_DOWN);
		// System.out.println("Woche: " + cmt.currentZyklus +
		// "\t | Preis p.P.: " + calcPreis[cmt.currentZyklus] +
		// "\t | Kosten p.P.: "
		// + pkPreis[cmt.currentZyklus] + "\t | Gewinn p.P.: " +
		// gewinn[cmt.currentZyklus] + "\t | Verk. p.W: " +
		// verkaufteProd[cmt.currentZyklus]
		// + "\t | Ges.Gewinn: " + gesGew + "\t | Preisaenderung: " + this.pa);
	}

	public BigDecimal[] getVerkaufteProd() {
		return verkaufteProd;
	}

	public BigDecimal[] getPkPreis() {
		return pkPreis;
	}

	public BigDecimal getGesGew() {
		return gesGew;
	}

	public BigDecimal[] getCalcPreis() {
		return calcPreis;
	}

	public BigDecimal[] getGewinn() {
		return gewinn;
	}

	public BigDecimal getPa() {
		return pa;
	}

}
