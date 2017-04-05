package de.haw.md.sups;

import io.github.miraclefoxx.math.BigDecimalMath;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import de.haw.md.company.main.CompanyMainTwo;

public class ResourceCalc {

	public static BigDecimal nextRandomStockPrice(Map<DateTime, BigDecimal> resourceMap, int max) {
		final BigDecimal[] analysisValues = stockAnalysis(resourceMap, max);
		final BigDecimal[] calcPosibleValues = { getFinalCloseValue(resourceMap).multiply(analysisValues[1]),
				getFinalCloseValue(resourceMap).multiply(analysisValues[0]) };
		return CompanyMainTwo.generateRandomBigDecimalFromRange(calcPosibleValues[0], calcPosibleValues[1]);
	}
	
	public static BigDecimal nextRandomStockPrice(Map<DateTime, BigDecimal> resourceMap) {
		return nextRandomStockPrice(resourceMap, resourceMap.size() - 1);
	}

	public static BigDecimal[] stockAnalysis(Map<DateTime, BigDecimal> resourceMap, int max) {
		BigDecimal average = averageValue(resourceMap, max);
		BigDecimal deviation = deviationValue(average, resourceMap, max);
		BigDecimal maxValue = BigDecimalMath.exp(average.add(deviation));
		BigDecimal minValue = BigDecimalMath.exp(average.subtract(deviation));
		BigDecimal[] array = { maxValue, minValue };
		return array;
	}

	private static BigDecimal deviationValue(BigDecimal average, Map<DateTime, BigDecimal> resourceMap, int max) {
		final BigDecimal size = new BigDecimal(resourceMap.size());
		BigDecimal sum = BigDecimal.ZERO;
		List<DateTime> keySet = new ArrayList<>();
		keySet.addAll(resourceMap.keySet());
		Collections.sort(keySet);
		for (int i = ((keySet.size() - 1) - max); i < (keySet.size() - 1); i++) {
			final BigDecimal divide = resourceMap.get(keySet.get(i)).divide(resourceMap.get(keySet.get(i + 1)), 10, RoundingMode.HALF_DOWN);
			final BigDecimal log = BigDecimalMath.log(divide);
			BigDecimal substract = log.subtract(average);
			if (substract.compareTo(BigDecimal.ZERO) < 0)
				substract = substract.multiply(BigDecimal.ZERO.subtract(BigDecimal.ONE));
			final BigDecimal pow = BigDecimalMath.powRound(substract, 2);
			sum = sum.add(pow);
		}
		final BigDecimal divide = sum.divide(size, 10, RoundingMode.HALF_DOWN);
		final BigDecimal sqrt = BigDecimalMath.sqrt(divide);
		return sqrt;
	}

	private static BigDecimal averageValue(Map<DateTime, BigDecimal> resourceMap, int max) {
		final BigDecimal size = new BigDecimal(resourceMap.size());
		BigDecimal sum = BigDecimal.ZERO;
		List<DateTime> keySet = new ArrayList<>();
		keySet.addAll(resourceMap.keySet());
		Collections.sort(keySet);
		for (int i = ((keySet.size() - 1) - max); i < (keySet.size() - 1); i++) {
			final BigDecimal divide = resourceMap.get(keySet.get(i)).divide(resourceMap.get(keySet.get(i + 1)), 10, RoundingMode.HALF_DOWN);
			sum = sum.add(BigDecimalMath.log(divide));
		}
		return sum.divide(size, 10, RoundingMode.HALF_DOWN);
	}

	public static BigDecimal getFinalCloseValue(Map<DateTime, BigDecimal> resourceMap) {
		List<DateTime> keySet = new ArrayList<>();
		keySet.addAll(resourceMap.keySet());
		Collections.sort(keySet);
		final DateTime lastDate = keySet.get(keySet.size() - 1);
		return resourceMap.get(lastDate);
	}
	
	public static DateTime getFinalDate(Map<DateTime, BigDecimal> resourceMap) {
		List<DateTime> keySet = new ArrayList<>();
		keySet.addAll(resourceMap.keySet());
		Collections.sort(keySet);
		return keySet.get(keySet.size() - 1);
	}
}
