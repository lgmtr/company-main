package de.haw.md.company.gui.main;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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
import de.haw.md.akka.main.msg.ResourceMsgModel;
import de.haw.md.sups.CompanyValuesEnum;
import de.haw.md.sups.GUIChartHelperEnum;
import de.haw.md.sups.GUIMenueItemsEnum;
import de.haw.md.sups.StaticVariables;

public class GuiMultiAgentMain extends Application {

	private static final double SCREEN_X = 1280;
	private static final double SCREEN_Y = 900;

	private static final int ELECTRO_PART_FACTORIES = 7;
	private static final int MOBILE_FACTORIES = 15;

	private static final long SEQUENCE_DURATION = 750;

	private Map<String, XYChart.Series<Number, Number>> chartSeriesMap = new HashMap<>();

	private Map<String, LineChart<Number, Number>> lineChartMap = new HashMap<>();

	private List<XYChart.Series<Number, Number>> companyData = new ArrayList<>();

	private Timeline animation;

	private NumberAxis xAxis = new NumberAxis();

	private int counter = 0;

	private final static ActorSystem system = ActorSystemContainer.getInstance().getSystem();

	public static void main(String[] args) {
		system.actorOf(Props.create(CompanyOil.class, StaticVariables.CHANNEL, "Company_Oil"), "Company_Oil");
		for (int i = 0; i < ELECTRO_PART_FACTORIES; i++)
			system.actorOf(
					Props.create(CompanyElectrPartProd.class, StaticVariables.CHANNEL, "Company_ElectroPart_" + i,
							StaticVariables.generateRandomBigDecimalFromRange(new BigDecimal("15"), new BigDecimal("30")),
							StaticVariables.generateRandomBigDecimalFromRange(new BigDecimal("1.20"), new BigDecimal("1.30")),
							StaticVariables.generateRandomBigDecimalFromRange(new BigDecimal("1.2"), new BigDecimal("1.7")),
							StaticVariables.generateRandomBigDecimalFromRange(new BigDecimal("1.05"), new BigDecimal("1.2"))), "Company_ElectroPart_" + i);
		for (int i = 0; i < MOBILE_FACTORIES; i++)
			system.actorOf(Props.create(CompanyMobile.class, StaticVariables.CHANNEL, "Company_Mobile_" + i, CompanyValuesEnum.COST_MAN_HOUR.getRandomValue(),
					CompanyValuesEnum.PROD_MAN_HOUR.getRandomValue(), CompanyValuesEnum.BONUS.getRandomValue(), "Company_ElectroPart_" + i
							% ELECTRO_PART_FACTORIES, CompanyValuesEnum.SUPPLIER_DISCOUNT.getRandomValue(), CompanyValuesEnum.FIX_COST.getRandomValue(),
					CompanyValuesEnum.PRODUCTION_LINES.getRandomValue(), CompanyValuesEnum.PRODUCTION_LINES_CAP.getRandomValue(),
					CompanyValuesEnum.MONTHLY_COSTS.getRandomValue()), "Company_Mobile_" + i);
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		init(primaryStage);
		animation = new Timeline();
		animation.getKeyFrames().add(new KeyFrame(javafx.util.Duration.millis(SEQUENCE_DURATION), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				final ActorRef publisher = MarketContainer.getInstance().getPublisher(StaticVariables.CHANNEL);
				system.scheduler().scheduleOnce(Duration.Zero(), publisher, "Tick", system.dispatcher(), publisher);
				try {
					// Map<String, MarketResponseMsgModel> marketMap =
					// MarketContainer.getInstance().getMarket().getMobileMarketResponses();
					// for (String companyName : marketMap.keySet()) {
					// final ObservableList<Data<Number, Number>> series =
					// getSeriesWithName(companyName).getData();
					// series.add(new XYChart.Data<Number, Number>(counter,
					// series.size() > 0 ? StaticVariables.convertToBigDecimal(
					// series.get(series.size() -
					// 1).getYValue().toString()).add(
					// StaticVariables.convertToBigDecimal(marketMap.get(companyName).getProfit()))
					// : StaticVariables.convertToBigDecimal(marketMap
					// .get(companyName).getProfit())));
					// }
					if (counter > 0) {
						Map<String, ResourceMsgModel> marketMap = MarketContainer.getInstance().getMarket().getResourceMarketResponses();
						for (String type : marketMap.keySet()) {
							ResourceMsgModel rmm = marketMap.get(type);
							final ObservableList<Data<Number, Number>> series = chartSeriesMap.get(type).getData();
							final BigDecimal convertToBigDecimal = StaticVariables.convertToBigDecimal(rmm.getValue());
							series.add(new XYChart.Data<Number, Number>(counter, convertToBigDecimal));
						}
					}
					counter++;
					if (counter - 2 >= xAxis.getUpperBound()) {
						xAxis.setUpperBound(xAxis.getUpperBound() + 1);
						xAxis.setLowerBound(xAxis.getLowerBound() + 1);
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		}));
		animation.setCycleCount(Animation.INDEFINITE);
		primaryStage.show();
	}

	private void init(Stage primaryStage) {
		Group root = new Group();
		Group toolBar = new Group();
		Group chartView = new Group();
		chartView.getChildren().addAll(createChartNodes());
		toolBar.getChildren().addAll(createToolBarNodes());
		root.getChildren().addAll(toolBar, chartView);
		primaryStage.setScene(new Scene(root, SCREEN_X, SCREEN_Y));
		primaryStage.resizableProperty().set(false);
	}

	private List<Node> createChartNodes() {
		List<Node> chartList = new ArrayList<>();
		xAxis = new NumberAxis();
		xAxis.setAutoRanging(false);
		xAxis.setLowerBound(0);
		xAxis.setUpperBound(100);
		chartList.add(createResourceChart("Oil"));
		chartList.add(createResourceChart("Metall"));
		chartList.add(createResourceChart("Edelmetalle"));
		return chartList;
	}
	
	private List<Node> createMobileCompanyCharts(){
		List<Node> companyCharts = new ArrayList<>();
		for (GUIChartHelperEnum guiChartHelperEnum : GUIChartHelperEnum.getChartElementsWithGroupName("Company")) {
			LineChart<Number, Number> lineChart = createNewSimpleLineChart(guiChartHelperEnum.getName());
			
		}
		return companyCharts;
	}

	private Node createResourceChart(String group) {
		LineChart<Number, Number> lineChart = createNewSimpleLineChart(group);
		lineChart.getData().addAll(createResourceXYSeries(group));
		lineChartMap.put(group, lineChart);
		return lineChart;
	}
	
	private LineChart<Number, Number> createNewSimpleLineChart(String title){
		NumberAxis yAxis = new NumberAxis();
		LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
		lineChart.setTitle(title);
		lineChart.setMinWidth(SCREEN_X);
		lineChart.setMinHeight(SCREEN_Y - 50);
		lineChart.relocate(0, 30);
		lineChart.setCreateSymbols(false);
		return lineChart;
	}

	private List<XYChart.Series<Number, Number>> createResourceXYSeries(String group) {
		List<XYChart.Series<Number, Number>> seriesList = new ArrayList<>();
		for (GUIChartHelperEnum guiChartHelperEnum : GUIChartHelperEnum.getChartElementsWithGroupName(group)) {
			Series<Number, Number> series = new XYChart.Series<>();
			series.setName(guiChartHelperEnum.getName());
			seriesList.add(series);
			chartSeriesMap.put(guiChartHelperEnum.getName(), series);
		}
		return seriesList;
	}

	private List<Node> createToolBarNodes() {
		List<Node> toolBarList = new ArrayList<>();
		toolBarList.add(createComboBox());
		Button start = new Button("Start");
		start.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				System.out.println("Started");
				animation.play();
			}
		});
		start.setMinWidth(75);
		start.relocate(SCREEN_X - 165, 5);
		toolBarList.add(start);
		Button stop = new Button("Stop");
		stop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				System.out.println("Stoped");
				animation.pause();
			}
		});
		stop.setMinWidth(75);
		stop.relocate(SCREEN_X - 80, 5);
		toolBarList.add(stop);
		return toolBarList;
	}

	private Node createComboBox() {
		ChoiceBox<String> cb = new ChoiceBox<>();
		for (GUIMenueItemsEnum gmie : GUIMenueItemsEnum.values()) {
			cb.getItems().add(gmie.getName());
		}
		cb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> ov, String value, String newValue) {
				selectChartWithName(newValue);
			}
		});
		cb.getSelectionModel().selectFirst();
		cb.setMinWidth(150);
		cb.relocate(5, 5);
		selectChartWithName(cb.getSelectionModel().selectedItemProperty().getValue());
		return cb;
	}

	private void selectChartWithName(String groupName) {
		for (String lineChartMapKey : lineChartMap.keySet())
			lineChartMap.get(lineChartMapKey).setVisible(false);
		lineChartMap.get(groupName).setVisible(true);
	}

}
