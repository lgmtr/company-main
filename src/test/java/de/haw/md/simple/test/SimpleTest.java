package de.haw.md.simple.test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.haw.md.akka.main.msg.CompanyShareMsgModel;
import de.haw.md.akka.main.msg.ResourceMsgModel;
import de.haw.md.sups.ResourceCalc;
import de.haw.md.sups.Resources;
import de.haw.md.sups.StaticVariables;

public class SimpleTest {

	@Test
	public void simpleTest() {
		Map<String, String> map = new HashMap<>();
		System.out.println(map.size());
		map.put("Some", "Some");
		System.out.println(map.size());
		map.clear();
		System.out.println(map.size());
	}

	@Test
	public void resourcesAllTest() {
		Resources res = new Resources();
		res.readAllPrices();
		for (Map<DateTime, BigDecimal> ressourceElement : res.getListOfResources()) {
			for (DateTime dateTime : res.sortKeyList(ressourceElement.keySet())) {
				System.out.println("Time: " + dateTime.toString("dd.MM.yyyy") + "\tPrice: " + ressourceElement.get(dateTime));
			}
			System.out.println("=========================================================================");
		}

	}

	@Test
	public void resourcesAllNextTest() {
		Resources res = new Resources();
		res.readAllPrices();
		System.out.println("=========================================================================");
		for (Map<DateTime, BigDecimal> ressourceElement : res.getListOfResources()) {
			System.out.println("Before Price: " + ResourceCalc.getFinalCloseValue(ressourceElement));
			System.out.println("After Price: " + ResourceCalc.nextRandomStockPrice(ressourceElement));
			System.out.println("=========================================================================");
		}

	}

	@Test
	public void resourcesOneNextTest() {
		Resources res = new Resources();
		res.readAllPrices();
		System.out.println("Start Price: " + ResourceCalc.getFinalCloseValue(res.getOilPrice()) + "\tStart Date: "
				+ ResourceCalc.getFinalDate(res.getOilPrice()).toString(StaticVariables.DE_DATE_FORMATTER));
		System.out.println("=========================================================================");
		for (int i = 0; i < 100; i++) {
			final DateTime newDate = ResourceCalc.getFinalDate(res.getOilPrice()).plusDays(1);
			final BigDecimal newPrice = ResourceCalc.nextRandomStockPrice(res.getOilPrice());
			System.out.println("New Price: " + newPrice + "\tNew Date: " + newDate.toString(StaticVariables.DE_DATE_FORMATTER));
			System.out.println("=========================================================================");
			res.getOilPrice().put(newDate, newPrice);
		}
	}

	@Test
	public void mapToJson() {
		Resources res = new Resources();
		res.readAllPrices();
		DateTime dateTime = res.sortKeyList(res.getOilPrice().keySet()).get(15);
		ObjectMapper mapper = new ObjectMapper();
		ResourceMsgModel rmm = new ResourceMsgModel();
		rmm.setDate(dateTime.toString("dd.MM.yyyy"));
		rmm.setValue(res.getOilPrice().get(dateTime).toString());
		rmm.setType("Oil");
		try {
			String mapped = mapper.writeValueAsString(rmm);
			System.out.println(mapped);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void moduloTest() {
		for (int i = 0; i < 5; i++)
			System.out.println(i % 5);
	}

	@Test
	public void compareToTest() {
		CompanyShareMsgModel csmmSmall = new CompanyShareMsgModel("1", "10");
		CompanyShareMsgModel csmmBig = new CompanyShareMsgModel("2", "20");
		System.out.println(csmmSmall.compareTo(csmmBig));
		List<CompanyShareMsgModel> csmmList = Arrays.asList(csmmSmall, csmmBig, new CompanyShareMsgModel("3", "30"), new CompanyShareMsgModel("4", "40"),
				new CompanyShareMsgModel("5", "50"), new CompanyShareMsgModel("6", "60"), new CompanyShareMsgModel("7", "70"), new CompanyShareMsgModel("8",
						"80"), new CompanyShareMsgModel("9", "90"), new CompanyShareMsgModel("10", "100"));
		for (CompanyShareMsgModel companyShareMsgModel : csmmList) {
			System.out.println(companyShareMsgModel.getShareValue());
		}
		CompanyShareMsgModel[] csmmArray = (CompanyShareMsgModel[]) csmmList.toArray();
		Arrays.sort(csmmArray, Collections.reverseOrder());
		for (CompanyShareMsgModel companyShareMsgModel : csmmArray) {
			System.out.println(companyShareMsgModel.getShareValue());
		}

	}

}
