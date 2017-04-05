<<<<<<< HEAD
package de.haw.md.company.main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.haw.md.company.model.CompanyInvetion;
import de.haw.md.company.model.CompanyModel;
import de.haw.md.company.model.CompanyProduct;
import de.haw.md.company.model.ProductProduction;
import de.haw.md.company.simulator.Simulator;

public class CompanyMain {

	private BigDecimal population;

	private CompanyModel initSim(int startCase, CompanyModel cm) {
		this.population = new BigDecimal("80000000");
		// this.cm = new CompanyModel();
		cm.setCompanyID(UUID.randomUUID().toString());
		switch (startCase) {
		case 1:
			cm.setProducts(createProducts(3));
			cm.setInventions(createInvetions(0));
			cm.setReputation(new BigDecimal("1"));
			cm.setBudget(new BigDecimal("25000"));
			cm.setStockKap(new BigDecimal(("100000")));
			cm.setMarkedVolume(new BigDecimal("3"));
			break;
		case 2:
			cm.setProducts(createProducts(10));
			cm.setInventions(createInvetions(2));
			cm.setReputation(new BigDecimal("25"));
			cm.setBudget(new BigDecimal("500000"));
			cm.setStockKap(new BigDecimal(("1000000")));
			cm.setMarkedVolume(new BigDecimal("7"));
			break;
		case 3:
			cm.setProducts(createProducts(20));
			cm.setInventions(createInvetions(5));
			cm.setReputation(new BigDecimal("50"));
			cm.setBudget(new BigDecimal("1000000"));
			cm.setStockKap(new BigDecimal(("10000000")));
			cm.setMarkedVolume(new BigDecimal("15"));
			break;
		}
		return cm;
	}

	private List<CompanyInvetion> createInvetions(int i) {
		List<CompanyInvetion> cil = new ArrayList<>();
		for (int j = 0; j < i; j++) {
			CompanyInvetion ci = new CompanyInvetion();
			ci.setDevelopmentTime(rand(new BigDecimal("6"), new BigDecimal("18"),0));
			ci.setName(UUID.randomUUID().toString());
			cil.add(ci);
		}
		return cil;
	}

	private List<CompanyProduct> createProducts(int i) {
		List<CompanyProduct> cpl = new ArrayList<>();
		for (int j = 0; j < i; j++) {
			CompanyProduct cp = new CompanyProduct();
			cp.setName(UUID.randomUUID().toString());
			BigDecimal cpSatiation = rand(new BigDecimal("0.15"), new BigDecimal("0.75"));
			BigDecimal cpDemand = rand(new BigDecimal("1.5"), new BigDecimal("15")).subtract(cpSatiation);
			BigDecimal cpCost = rand(cpDemand.multiply(new BigDecimal("10")), cpDemand.multiply(new BigDecimal("100")));
			BigDecimal cpPrice = cpCost.multiply(rand(new BigDecimal("1.05"), new BigDecimal("1.5")));
			BigDecimal cpInventory = setInitInventory(cpCost);
			ProductProduction pp = new ProductProduction();
			pp.setInProduction(new BigDecimal("0"));
			pp.setProductionInOneMonth(setProductionInOneMonth(cpCost));
			cp.setDemand(cpDemand);
			cp.setSatiation(cpSatiation);
			cp.setInInventory(cpInventory);
			cp.setProductProduction(pp);
			cp.setCost(cpCost);
			cp.setPrice(cpPrice);
			cpl.add(cp);
		}
		return cpl;
	}

	private BigDecimal setInitInventory(BigDecimal cpCost) {
		if (cpCost.compareTo(new BigDecimal("100")) < 0) {
			return new BigDecimal("10000");
		} else if (cpCost.compareTo(new BigDecimal("300")) < 0) {
			return new BigDecimal("8000");
		} else if (cpCost.compareTo(new BigDecimal("500")) < 0) {
			return new BigDecimal("6000");
		} else if (cpCost.compareTo(new BigDecimal("700")) < 0) {
			return new BigDecimal("4000");
		} else if (cpCost.compareTo(new BigDecimal("900")) < 0) {
			return new BigDecimal("2000");
		} else {
			return new BigDecimal("1000");
		}
	}

	private BigDecimal setProductionInOneMonth(BigDecimal cpCost) {
		if (cpCost.compareTo(new BigDecimal("100")) < 0) {
			return new BigDecimal("2500");
		} else if (cpCost.compareTo(new BigDecimal("300")) < 0) {
			return new BigDecimal("2000");
		} else if (cpCost.compareTo(new BigDecimal("500")) < 0) {
			return new BigDecimal("1500");
		} else if (cpCost.compareTo(new BigDecimal("700")) < 0) {
			return new BigDecimal("1000");
		} else if (cpCost.compareTo(new BigDecimal("900")) < 0) {
			return new BigDecimal("500");
		} else {
			return new BigDecimal("250");
		}
	}

	public static BigDecimal rand(BigDecimal min, BigDecimal max) {
		return rand(min, max, 5);
	}

	public static BigDecimal rand(BigDecimal min, BigDecimal max, int scale) {
		BigDecimal randomBigDecimal = min.add(new BigDecimal(Math.random()).multiply(max.subtract(min)));
		return randomBigDecimal.setScale(scale, BigDecimal.ROUND_DOWN);
	}

	private void startSim(BigDecimal growthRateOfPopInMonth, CompanyModel cm, int yearsOfSim) {
		Simulator sim = new Simulator(growthRateOfPopInMonth, population, cm);
		for (int i = 0; i < yearsOfSim; i++) {
			sim.simOneMonth();
		}
		printComplex(cm);
	}

	public static void main(String[] args) {
		CompanyMain main = new CompanyMain();
		CompanyModel cm = main.initSim(3, new CompanyModel());
		main.printComplex(cm);
		main.startSim(new BigDecimal("0.0043692"), cm, 24);
	}

	private void printComplex(CompanyModel cm) {
		System.out.println("Company ID: " + cm.getCompanyID() + " / Reputation: " + cm.getReputation() + " / Products: " + cm.getProducts().size()
				+ " / Invetions: " + cm.getInventions().size() + " / Budget: " + cm.getBudget().setScale(2, RoundingMode.HALF_DOWN));
		System.out
				.println("=======================================================================================================================================================================");
		for (CompanyProduct cp : cm.getProducts()) {
			System.out.println("Product ID: " + cp.getName() + "\tDemand: " + cp.getDemand().setScale(2, RoundingMode.HALF_DOWN) + "\tSatiation: "
					+ cp.getSatiation().setScale(2, RoundingMode.HALF_DOWN) + "\tIn Inventory: " + cp.getInInventory() + "\tIn Production: "
					+ cp.getProductProduction().getInProduction() + "\tPrice: " + cp.getPrice().setScale(2, RoundingMode.HALF_DOWN) + "\tCost: "
					+ cp.getCost().setScale(2, RoundingMode.HALF_DOWN));

		}
		System.out
				.println("=======================================================================================================================================================================");
		for (CompanyInvetion ci : cm.getInventions()) {
			System.out.println("Invention ID: " + ci.getName() + " / Invention Time: " + ci.getDevelopmentTime());

		}
		System.out
				.println("=======================================================================================================================================================================");
	}

}
=======
package de.haw.md.company.main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.haw.md.company.model.CompanyInvetion;
import de.haw.md.company.model.CompanyModel;
import de.haw.md.company.model.CompanyProduct;
import de.haw.md.company.model.ProductProduction;
import de.haw.md.company.simulator.Simulator;

public class CompanyMain {

	private BigDecimal population;

	private CompanyModel initSim(int startCase, CompanyModel cm) {
		this.population = new BigDecimal("80000000");
		// this.cm = new CompanyModel();
		cm.setCompanyID(UUID.randomUUID().toString());
		switch (startCase) {
		case 1:
			cm.setProducts(createProducts(3));
			cm.setInventions(createInvetions(0));
			cm.setReputation(new BigDecimal("1"));
			cm.setBudget(new BigDecimal("25000"));
			cm.setStockKap(new BigDecimal(("100000")));
			cm.setMarkedVolume(new BigDecimal("3"));
			break;
		case 2:
			cm.setProducts(createProducts(10));
			cm.setInventions(createInvetions(2));
			cm.setReputation(new BigDecimal("25"));
			cm.setBudget(new BigDecimal("500000"));
			cm.setStockKap(new BigDecimal(("1000000")));
			cm.setMarkedVolume(new BigDecimal("7"));
			break;
		case 3:
			cm.setProducts(createProducts(20));
			cm.setInventions(createInvetions(5));
			cm.setReputation(new BigDecimal("50"));
			cm.setBudget(new BigDecimal("1000000"));
			cm.setStockKap(new BigDecimal(("10000000")));
			cm.setMarkedVolume(new BigDecimal("15"));
			break;
		}
		return cm;
	}

	private List<CompanyInvetion> createInvetions(int i) {
		List<CompanyInvetion> cil = new ArrayList<>();
		for (int j = 0; j < i; j++) {
			CompanyInvetion ci = new CompanyInvetion();
			ci.setDevelopmentTime(rand(new BigDecimal("6"), new BigDecimal("18"),0));
			ci.setName(UUID.randomUUID().toString());
			cil.add(ci);
		}
		return cil;
	}

	private List<CompanyProduct> createProducts(int i) {
		List<CompanyProduct> cpl = new ArrayList<>();
		for (int j = 0; j < i; j++) {
			CompanyProduct cp = new CompanyProduct();
			cp.setName(UUID.randomUUID().toString());
			BigDecimal cpSatiation = rand(new BigDecimal("0.15"), new BigDecimal("0.75"));
			BigDecimal cpDemand = rand(new BigDecimal("1.5"), new BigDecimal("15")).subtract(cpSatiation);
			BigDecimal cpCost = rand(cpDemand.multiply(new BigDecimal("10")), cpDemand.multiply(new BigDecimal("100")));
			BigDecimal cpPrice = cpCost.multiply(rand(new BigDecimal("1.05"), new BigDecimal("1.5")));
			BigDecimal cpInventory = setInitInventory(cpCost);
			ProductProduction pp = new ProductProduction();
			pp.setInProduction(new BigDecimal("0"));
			pp.setProductionInOneMonth(setProductionInOneMonth(cpCost));
			cp.setDemand(cpDemand);
			cp.setSatiation(cpSatiation);
			cp.setInInventory(cpInventory);
			cp.setProductProduction(pp);
			cp.setCost(cpCost);
			cp.setPrice(cpPrice);
			cpl.add(cp);
		}
		return cpl;
	}

	private BigDecimal setInitInventory(BigDecimal cpCost) {
		if (cpCost.compareTo(new BigDecimal("100")) < 0) {
			return new BigDecimal("10000");
		} else if (cpCost.compareTo(new BigDecimal("300")) < 0) {
			return new BigDecimal("8000");
		} else if (cpCost.compareTo(new BigDecimal("500")) < 0) {
			return new BigDecimal("6000");
		} else if (cpCost.compareTo(new BigDecimal("700")) < 0) {
			return new BigDecimal("4000");
		} else if (cpCost.compareTo(new BigDecimal("900")) < 0) {
			return new BigDecimal("2000");
		} else {
			return new BigDecimal("1000");
		}
	}

	private BigDecimal setProductionInOneMonth(BigDecimal cpCost) {
		if (cpCost.compareTo(new BigDecimal("100")) < 0) {
			return new BigDecimal("2500");
		} else if (cpCost.compareTo(new BigDecimal("300")) < 0) {
			return new BigDecimal("2000");
		} else if (cpCost.compareTo(new BigDecimal("500")) < 0) {
			return new BigDecimal("1500");
		} else if (cpCost.compareTo(new BigDecimal("700")) < 0) {
			return new BigDecimal("1000");
		} else if (cpCost.compareTo(new BigDecimal("900")) < 0) {
			return new BigDecimal("500");
		} else {
			return new BigDecimal("250");
		}
	}

	public static BigDecimal rand(BigDecimal min, BigDecimal max) {
		return rand(min, max, 5);
	}

	public static BigDecimal rand(BigDecimal min, BigDecimal max, int scale) {
		BigDecimal randomBigDecimal = min.add(new BigDecimal(Math.random()).multiply(max.subtract(min)));
		return randomBigDecimal.setScale(scale, BigDecimal.ROUND_DOWN);
	}

	private void startSim(BigDecimal growthRateOfPopInMonth, CompanyModel cm, int yearsOfSim) {
		Simulator sim = new Simulator(growthRateOfPopInMonth, population, cm);
		for (int i = 0; i < yearsOfSim; i++) {
			sim.simOneMonth();
		}
		printComplex(cm);
	}

	public static void main(String[] args) {
		CompanyMain main = new CompanyMain();
		CompanyModel cm = main.initSim(3, new CompanyModel());
		main.printComplex(cm);
		main.startSim(new BigDecimal("0.0043692"), cm, 24);
	}

	private void printComplex(CompanyModel cm) {
		System.out.println("Company ID: " + cm.getCompanyID() + " / Reputation: " + cm.getReputation() + " / Products: " + cm.getProducts().size()
				+ " / Invetions: " + cm.getInventions().size() + " / Budget: " + cm.getBudget().setScale(2, RoundingMode.HALF_DOWN));
		System.out
				.println("=======================================================================================================================================================================");
		for (CompanyProduct cp : cm.getProducts()) {
			System.out.println("Product ID: " + cp.getName() + "\tDemand: " + cp.getDemand().setScale(2, RoundingMode.HALF_DOWN) + "\tSatiation: "
					+ cp.getSatiation().setScale(2, RoundingMode.HALF_DOWN) + "\tIn Inventory: " + cp.getInInventory() + "\tIn Production: "
					+ cp.getProductProduction().getInProduction() + "\tPrice: " + cp.getPrice().setScale(2, RoundingMode.HALF_DOWN) + "\tCost: "
					+ cp.getCost().setScale(2, RoundingMode.HALF_DOWN));

		}
		System.out
				.println("=======================================================================================================================================================================");
		for (CompanyInvetion ci : cm.getInventions()) {
			System.out.println("Invention ID: " + ci.getName() + " / Invention Time: " + ci.getDevelopmentTime());

		}
		System.out
				.println("=======================================================================================================================================================================");
	}

}
>>>>>>> a63b17902c63202d3859c5399365ac10cada85e8
