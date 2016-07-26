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
import de.haw.md.sups.StaticVariables;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class PublishSubscribeTest {

	private final long TEST_TIME = 120000;

	@Test
	public void pubSubBigTest() {

		int electroPartFactories = 7;
		int mobileFactories = 15;

		final ActorSystem system = ActorSystemContainer.getInstance().getSystem();

		system.actorOf(Props.create(CompanyOil.class, StaticVariables.CHANNEL, "Company_Oil"), "Company_Oil");
		for (int i = 0; i < electroPartFactories; i++)
			system.actorOf(
					Props.create(CompanyElectrPartProd.class, StaticVariables.CHANNEL, "Company_ElectroPart_" + i,
							CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("15"), new BigDecimal("30")),
							CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.20"), new BigDecimal("1.30")),
							CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.2"), new BigDecimal("1.7")),
							CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.05"), new BigDecimal("1.2"))), "Company_ElectroPart_" + i);
		for (int i = 0; i < mobileFactories; i++)
			system.actorOf(Props.create(CompanyMobile.class, StaticVariables.CHANNEL, "Company_Mobile_" + i,
					CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("20"), new BigDecimal("40")),
					CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("2"), new BigDecimal("4")),
					CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.2"), new BigDecimal("1.4")), "Company_ElectroPart_" + i%electroPartFactories,
					CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.03"), new BigDecimal("1.1")),
					CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("5000000"), new BigDecimal("7000000")),
					CompanyMainTwo.generateRandomBigDecimalFromRangeScale0(new BigDecimal("4"), new BigDecimal("7")),
					CompanyMainTwo.generateRandomBigDecimalFromRangeScale0(new BigDecimal("3000"), new BigDecimal("4000"))), "Company_Mobile_" + i);

		ActorRef publisher = MarketContainer.getInstance().getPublisher(StaticVariables.CHANNEL);
		system.scheduler().schedule(Duration.Zero(), Duration.create(500, TimeUnit.MILLISECONDS), publisher, "Tick", system.dispatcher(), publisher);
		long time = System.currentTimeMillis();
		do {
		} while (!((time + TEST_TIME) <= System.currentTimeMillis()));
	}
}
