package de.haw.md.company.gui.main;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import de.haw.md.akka.main.ActorSystemContainer;
import de.haw.md.akka.main.CompanyElectrPartProd;
import de.haw.md.akka.main.CompanyMobile;
import de.haw.md.akka.main.CompanyOil;
import de.haw.md.akka.main.MarketContainer;
import de.haw.md.akka.main.msg.MarketResponseMsgModel;
import de.haw.md.company.main.CompanyMainTwo;
import de.haw.md.sups.StaticVariables;

public class GuiMultiAgentMain extends Application {

	private static final double SCREEN_X = 1280;
	private static final double SCREEN_Y = 900;

	private static final int ELECTRO_PART_FACTORIES = 7;
	private static final int MOBILE_FACTORIES = 15;

	private List<XYChart.Series<Number, Number>> companyData = new ArrayList<>();

	private Timeline animation;

	private NumberAxis xAxis = new NumberAxis();
	private NumberAxis yAxis = new NumberAxis();

	private int counter = 0;

	private final static ActorSystem system = ActorSystemContainer.getInstance().getSystem();

	public static void main(String[] args) {

		system.actorOf(Props.create(CompanyOil.class, StaticVariables.CHANNEL, "Company_Oil"), "Company_Oil");
		for (int i = 0; i < ELECTRO_PART_FACTORIES; i++)
			system.actorOf(
					Props.create(CompanyElectrPartProd.class, StaticVariables.CHANNEL, "Company_ElectroPart_" + i,
							CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("15"), new BigDecimal("30")),
							CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.20"), new BigDecimal("1.30")),
							CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.2"), new BigDecimal("1.7")),
							CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.05"), new BigDecimal("1.2"))), "Company_ElectroPart_" + i);
		for (int i = 0; i < MOBILE_FACTORIES; i++)
			system.actorOf(Props.create(CompanyMobile.class, StaticVariables.CHANNEL, "Company_Mobile_" + i,
					CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("20"), new BigDecimal("40")),
					CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("2"), new BigDecimal("4")),
					CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.2"), new BigDecimal("1.4")), "Company_ElectroPart_" + i
							% ELECTRO_PART_FACTORIES, CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("1.03"), new BigDecimal("1.1")),
					CompanyMainTwo.generateRandomBigDecimalFromRange(new BigDecimal("5000000"), new BigDecimal("7000000")),
					CompanyMainTwo.generateRandomBigDecimalFromRangeScale0(new BigDecimal("4"), new BigDecimal("7")),
					CompanyMainTwo.generateRandomBigDecimalFromRangeScale0(new BigDecimal("3000"), new BigDecimal("4000"))), "Company_Mobile_" + i);

		ActorRef publisher = MarketContainer.getInstance().getPublisher(StaticVariables.CHANNEL);
		system.scheduler().schedule(Duration.Zero(), Duration.create(250, TimeUnit.MILLISECONDS), publisher, "Tick", system.dispatcher(), publisher);
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		init(primaryStage);
		animation = new Timeline();
		animation.getKeyFrames().add(new KeyFrame(javafx.util.Duration.millis(250), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				Map<String, MarketResponseMsgModel> marketMap = MarketContainer.getInstance().getMarket().getMobileMarketResponses();
				for (String companyName : marketMap.keySet()) {
					final ObservableList<Data<Number, Number>> series = getSeriesWithName(companyName).getData();
					if (StaticVariables.convertToBigDecimal(marketMap.get(companyName).getProfit()).compareTo(BigDecimal.ZERO) > 0)
						series.add(new XYChart.Data<Number, Number>(counter, series.size() > 0 ? StaticVariables.convertToBigDecimal(
								series.get(series.size() - 1).getYValue().toString()).add(
								StaticVariables.convertToBigDecimal(marketMap.get(companyName).getProfit())) : StaticVariables.convertToBigDecimal(marketMap
								.get(companyName).getProfit())));
					// series.add(new XYChart.Data<Number, Number>(counter,
					// StaticVariables.convertToBigDecimal(marketMap.get(companyName).getRevenue())));
				}
				counter++;
				if (counter - 3 >= xAxis.getUpperBound()) {
					xAxis.setUpperBound(xAxis.getUpperBound() + 1);
					xAxis.setLowerBound(xAxis.getLowerBound() + 1);
				}
			}
		}));
		animation.setCycleCount(Animation.INDEFINITE);
		primaryStage.show();
		animation.play();

	}

	private void init(Stage primaryStage) {
		Group root = new Group();
		xAxis = new NumberAxis();
		yAxis = new NumberAxis();
		xAxis.setAutoRanging(false);
		xAxis.setLowerBound(0);
		xAxis.setUpperBound(100);
		final LineChart<Number, Number> ac = new LineChart<Number, Number>(xAxis, yAxis);
		ac.setMinWidth(SCREEN_X);
		ac.setMinHeight(SCREEN_Y - 30);
		ac.setCreateSymbols(false);
		createXYCharts();
		ac.getData().addAll(companyData);
		root.getChildren().add(ac);
		primaryStage.setScene(new Scene(root, SCREEN_X, SCREEN_Y));
		primaryStage.resizableProperty().set(false);
	}

	private void createXYCharts() {
		for (int i = 0; i < MOBILE_FACTORIES; i++) {
			Series<Number, Number> series = new XYChart.Series<>();
			series.setName("Company_Mobile_" + i);
			companyData.add(series);
		}
	}

	private XYChart.Series<Number, Number> getSeriesWithName(String name) {
		for (XYChart.Series<Number, Number> series : companyData)
			if (series.getName().equals(name))
				return series;
		return null;
	}

}
