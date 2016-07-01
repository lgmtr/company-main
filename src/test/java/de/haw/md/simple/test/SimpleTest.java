package de.haw.md.simple.test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.haw.md.akka.main.msg.ResourceMsgModel;
import de.haw.md.sups.Resources;

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
		for (Map<DateTime, BigDecimal> ressourceElement: res.getListOfResources()) {
			for (DateTime dateTime : res.sortKeyList(ressourceElement.keySet())) {
				System.out.println("Time: " + dateTime.toString("dd.MM.yyyy") + "\tPrice: " + ressourceElement.get(dateTime));
			}
			System.out.println("=========================================================================");
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

}
