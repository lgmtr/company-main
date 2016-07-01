package de.haw.md.akka.cluster.test;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.testng.annotations.Test;

import scala.concurrent.duration.Duration;
import de.haw.md.akka.main.ActorSystemContainer;
import de.haw.md.akka.main.CompanyElectrPartProd;
import de.haw.md.akka.main.CompanyMobile;
import de.haw.md.akka.main.MarketContainer;
import de.haw.md.akka.main.CompanyOil;
import de.haw.md.company.main.CompanyMainTwo;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class PublishSubscribeTest {

	private final static String CHANNEL = "MarketSim";

	private final long TEST_TIME = 12000;

	@Test
	public void pubSubTest() {
		final ActorSystem system = ActorSystemContainer.getInstance().getSystem();

		system.actorOf(Props.create(CompanyOil.class, CHANNEL, "Company_Oil"), "Company_Oil");
		system.actorOf(
				Props.create(CompanyElectrPartProd.class, CHANNEL, "Company_ElectroPart_1",
						CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("20"), new BigDecimal("30")),
						CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.25"), new BigDecimal("1.30")),
						CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.2"), new BigDecimal("1.7")),
						CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.1"), new BigDecimal("1.15"))), "Company_ElectroPart_1");
		system.actorOf(
				Props.create(CompanyElectrPartProd.class, CHANNEL, "Company_ElectroPart_2",
						CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("13"), new BigDecimal("25")),
						CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.20"), new BigDecimal("1.25")),
						CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.5"), new BigDecimal("2.5")),
						CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.08"), new BigDecimal("1.12"))), "Company_ElectroPart_2");
		system.actorOf(Props.create(CompanyMobile.class, CHANNEL, "Company_Mobile_1",
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("20"), new BigDecimal("30")),
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.20"), new BigDecimal("1.25")),
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("2"), new BigDecimal("4")),
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.2"), new BigDecimal("1.4")), "Company_ElectroPart_1",
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.05"), new BigDecimal("1.07"))), "Company_Mobile_1");
		system.actorOf(Props.create(CompanyMobile.class, CHANNEL, "Company_Mobile_2",
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("20"), new BigDecimal("30")),
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.20"), new BigDecimal("1.25")),
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("2"), new BigDecimal("4")),
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.2"), new BigDecimal("1.4")), "Company_ElectroPart_2",
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.05"), new BigDecimal("1.07"))), "Company_Mobile_2");
		system.actorOf(Props.create(CompanyMobile.class, CHANNEL, "Company_Mobile_3",
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("20"), new BigDecimal("30")),
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.20"), new BigDecimal("1.25")),
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("2"), new BigDecimal("4")),
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.2"), new BigDecimal("1.4")), "Company_ElectroPart_1",
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.05"), new BigDecimal("1.07"))), "Company_Mobile_3");
		system.actorOf(Props.create(CompanyMobile.class, CHANNEL, "Company_Mobile_4",
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("20"), new BigDecimal("30")),
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.20"), new BigDecimal("1.25")),
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("2"), new BigDecimal("4")),
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.2"), new BigDecimal("1.4")), "Company_ElectroPart_2",
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.05"), new BigDecimal("1.07"))), "Company_Mobile_4");
		system.actorOf(Props.create(CompanyMobile.class, CHANNEL, "Company_Mobile_5",
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("20"), new BigDecimal("30")),
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.20"), new BigDecimal("1.25")),
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("2"), new BigDecimal("4")),
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.2"), new BigDecimal("1.4")), "Company_ElectroPart_1",
				CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.05"), new BigDecimal("1.07"))), "Company_Mobile_5");

		ActorRef publisher = MarketContainer.getInstance().getPublisher(CHANNEL);
		system.scheduler().schedule(Duration.Zero(), Duration.create(50, TimeUnit.MILLISECONDS), publisher, "Tick", system.dispatcher(), publisher);
		long time = System.currentTimeMillis();
		do {
		} while (!((time + TEST_TIME) <= System.currentTimeMillis()));
	}
}
