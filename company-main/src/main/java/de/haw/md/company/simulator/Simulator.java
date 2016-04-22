package de.haw.md.company.simulator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import de.haw.md.company.main.CompanyMain;
import de.haw.md.company.model.CompanyInvetion;
import de.haw.md.company.model.CompanyModel;
import de.haw.md.company.model.CompanyProduct;

public class Simulator {

	private List<CompanyModel> cml;

	private BigDecimal growthRateOfPop;

	private BigDecimal population;

	public Simulator(BigDecimal growthRateOfPop, BigDecimal population, CompanyModel... cms) {
		this.growthRateOfPop = growthRateOfPop;
		this.population = population;
		this.cml = Arrays.asList(cms);
	}

	public void simOneMonth() {
		this.population = this.population.multiply((this.growthRateOfPop.divide(new BigDecimal("100"))).add(BigDecimal.ONE))
				.setScale(0, RoundingMode.HALF_DOWN);
		for (CompanyModel cm : cml) {
			List<CompanyProduct> dummyList = new ArrayList<>();
			for (CompanyProduct cp : cm.getProducts()) {
				cp.setInInventory(cp.getInInventory().add(cp.getProductProduction().getInProduction()));
				cm.setBudget(cm.getBudget().subtract(cp.getProductProduction().getInProduction().multiply(cp.getPrice())));
				cp.getProductProduction().setInProduction(BigDecimal.ZERO);
				BigDecimal calculatedDemand = population.multiply((cp.getDemand().subtract(cp.getSatiation())).divide(new BigDecimal("100")))
						.multiply(cm.getMarkedVolume().divide(new BigDecimal("100"))).divide(BigDecimal.TEN).setScale(0, RoundingMode.HALF_DOWN);
				if (calculatedDemand.compareTo(BigDecimal.ZERO) < 0) {
					dummyList.add(cp);
				} else {
					cp.setDemand(cp.getDemand().subtract(new BigDecimal("0.1")));
					cp.setSatiation(cp.getSatiation().add(new BigDecimal("0.2")));
					BigDecimal selledProducts = calcSelledProducts(cp.getCost(), calculatedDemand.divide(BigDecimal.valueOf(12), RoundingMode.HALF_DOWN));
					cp.setInInventory(cp.getInInventory().subtract(selledProducts));
					cm.setBudget(cm.getBudget().add(selledProducts.multiply(cp.getPrice())));
					if(selledProducts.compareTo(cp.getInInventory()) > 0 || selledProducts.compareTo(cp.getProductProduction().getProductionInOneMonth())>0){
						cp.getProductProduction().setInProduction(cp.getProductProduction().getProductionInOneMonth());
					}
				}
			}
			for (CompanyProduct companyProduct : dummyList) {
				cm.getProducts().remove(companyProduct);
			}
			if(CompanyMain.rand(BigDecimal.ZERO, BigDecimal.valueOf(2), 0).compareTo(BigDecimal.ONE)<0){
				CompanyInvetion ci = new CompanyInvetion();
				ci.setName(UUID.randomUUID().toString());
				ci.setDevelopmentTime(CompanyMain.rand(new BigDecimal("6"), new BigDecimal("18"),0));
				cm.getInventions().add(ci);
				final BigDecimal rand = CompanyMain.rand(new BigDecimal("0.15"), new BigDecimal("0.30"));
				final BigDecimal multiply = cm.getBudget().multiply(rand);
				cm.setBudget(cm.getBudget().subtract(multiply));
			}
		}
	}
	
	private BigDecimal calcSelledProducts(BigDecimal cpCost, BigDecimal posibleSelled) {
		if (cpCost.compareTo(new BigDecimal("100")) < 0) {
			return posibleSelled.divide(CompanyMain.rand(BigDecimal.valueOf(2), BigDecimal.valueOf(8), 0), RoundingMode.HALF_DOWN);
		} else if (cpCost.compareTo(new BigDecimal("300")) < 0) {
			return posibleSelled.divide(CompanyMain.rand(BigDecimal.valueOf(8), BigDecimal.valueOf(14), 0), RoundingMode.HALF_DOWN);
		} else if (cpCost.compareTo(new BigDecimal("500")) < 0) {
			return posibleSelled.divide(CompanyMain.rand(BigDecimal.valueOf(16), BigDecimal.valueOf(20), 0), RoundingMode.HALF_DOWN);
		} else if (cpCost.compareTo(new BigDecimal("700")) < 0) {
			return posibleSelled.divide(CompanyMain.rand(BigDecimal.valueOf(28), BigDecimal.valueOf(36), 0), RoundingMode.HALF_DOWN);
		} else if (cpCost.compareTo(new BigDecimal("900")) < 0) {
			return posibleSelled.divide(CompanyMain.rand(BigDecimal.valueOf(42), BigDecimal.valueOf(47), 0), RoundingMode.HALF_DOWN);
		} else {
			return posibleSelled.divide(CompanyMain.rand(BigDecimal.valueOf(85), BigDecimal.valueOf(90), 0), RoundingMode.HALF_DOWN);
		}
	}

	public List<CompanyModel> getCml() {
		return cml;
	}

	public BigDecimal getGrowthRateOfPop() {
		return growthRateOfPop;
	}

	public BigDecimal getPopulation() {
		return population;
	}

}
