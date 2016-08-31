package de.haw.md.company.gui.main;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import de.haw.md.akka.main.msg.CompanyShareMsgModel;
import de.haw.md.akka.main.msg.MarketResponseMsgModel;
import de.haw.md.akka.main.msg.MarketShareMsgModel;
import de.haw.md.akka.main.msg.ResourceMsgModel;
import de.haw.md.sups.CompanyValuesEnum;
import de.haw.md.sups.GUIChartHelperEnum;
import de.haw.md.sups.GUIMenueItemsEnum;
import de.haw.md.sups.StaticVariables;

@SuppressWarnings("restriction")
public class GuiMultiAgentMain extends Application {

	private static final double SCREEN_X = 1600;
	private static final double SCREEN_Y = 1000;

	private static final int ELECTRO_PART_FACTORIES = 7;
	private static final int MOBILE_FACTORIES = 15;

	private static final long SEQUENCE_DURATION = 1000;

	private Map<String, XYChart.Series<Number, Number>> chartSeriesMap = new HashMap<>();

	private Map<String, LineChart<Number, Number>> lineChartMap = new HashMap<>();

	private List<NumberAxis> xAxisList = new ArrayList<>();

	private Timeline animation;

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
							StaticVariables.generateRandomBigDecimalFromRange(new BigDecimal("1.6"), new BigDecimal("1.9"))), "Company_ElectroPart_" + i);
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
					if (counter > 0) {
						Map<String, ResourceMsgModel> marketMap = MarketContainer.getInstance().getMarket().getResourceMarketResponses();
						for (String type : marketMap.keySet()) {
							ResourceMsgModel rmm = marketMap.get(type);
							final ObservableList<Data<Number, Number>> series = chartSeriesMap.get(type).getData();
							final BigDecimal convertToBigDecimal = StaticVariables.convertToBigDecimal(rmm.getValue());
							series.add(new XYChart.Data<Number, Number>(counter, convertToBigDecimal));
						}
						Map<String, MarketResponseMsgModel> mobileMarktMap = MarketContainer.getInstance().getMarket().getMobileMarketResponses();
						for (String company : mobileMarktMap.keySet()) {
							MarketResponseMsgModel mrmm = mobileMarktMap.get(company);
							for (GUIChartHelperEnum guiChartHelperEnum : GUIChartHelperEnum.getChartElementsWithGroupName("Company")) {
								final ObservableList<Data<Number, Number>> series = chartSeriesMap.get(mrmm.getCompany() + "_" + guiChartHelperEnum.getName())
										.getData();
								Class<?> mrmmClass = mrmm.getClass();
								try {
									PropertyDescriptor pd = new PropertyDescriptor(guiChartHelperEnum.getMethodName(), mrmmClass);
									Method getter = pd.getReadMethod();
									String value = (String) getter.invoke(mrmm);
									BigDecimal convertToBigDecimal = StaticVariables.convertToBigDecimal(value);
									if (series.size() > 0 && guiChartHelperEnum.getMethodName().equals("profit"))
										convertToBigDecimal = StaticVariables.convertToBigDecimal(series.get(series.size() - 1).getYValue().toString()).add(
												convertToBigDecimal);
									series.add(new XYChart.Data<Number, Number>(counter, convertToBigDecimal));
								} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
										| IntrospectionException e) {
									e.printStackTrace();
								}
							}
						}
						MarketShareMsgModel msmm = MarketContainer.getInstance().getMarket().getMsmm();
						if (msmm != null) {
							for (int i = 0; i < MOBILE_FACTORIES; i++) {
								final String companyName = "Company_Mobile_" + i;
								BigDecimal csmmShareValue = BigDecimal.ZERO;
								final CompanyShareMsgModel csmm = msmm.findShareByCompanyName(companyName);
								if (csmm != null)
									csmmShareValue = StaticVariables.convertToBigDecimal(csmm.getShareValue());
								final ObservableList<Data<Number, Number>> series = chartSeriesMap.get(companyName + "_share").getData();
								series.add(new XYChart.Data<Number, Number>(counter, csmmShareValue));
							}
						}
					}
					counter++;
					if (counter - 2 >= xAxisList.get(0).getUpperBound())
						setNewBoundsForAllAxis();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}

			private void setNewBoundsForAllAxis() {
				for (NumberAxis xAxis : xAxisList) {
					xAxis.setUpperBound(xAxis.getUpperBound() + 1);
					xAxis.setLowerBound(xAxis.getLowerBound() + 1);
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
		chartList.add(createResourceChart("Oil"));
		chartList.add(createResourceChart("Metall"));
		chartList.add(createResourceChart("Edelmetalle"));
		chartList.addAll(createMobileCompanyCharts());
		chartList.add(createMarketShareChart());
		return chartList;
	}

	private Node createMarketShareChart() {
		LineChart<Number, Number> lineChart = createNewSimpleLineChart(GUIChartHelperEnum.MARKET_SHARES.getName());
		lineChart.getData().addAll(createShareXYSeries());
		lineChartMap.put(GUIChartHelperEnum.MARKET_SHARES.getName(), lineChart);
		return lineChart;
	}

	private List<Series<Number, Number>> createShareXYSeries() {
		List<XYChart.Series<Number, Number>> seriesList = new ArrayList<>();
		for (int i = 0; i < MOBILE_FACTORIES; i++) {
			final String seriesName = "Company_Mobile_" + i;
			Series<Number, Number> series = new XYChart.Series<>();
			series.setName(seriesName);
			seriesList.add(series);
			chartSeriesMap.put("Company_Mobile_" + i + "_share", series);
		}
		return seriesList;
	}

	private List<Node> createMobileCompanyCharts() {
		List<Node> companyCharts = new ArrayList<>();
		for (GUIChartHelperEnum guiChartHelperEnum : GUIChartHelperEnum.getChartElementsWithGroupName("Company")) {
			final String name = guiChartHelperEnum.getName();
			LineChart<Number, Number> lineChart = createNewSimpleLineChart(name);
			lineChart.getData().addAll(createMobileXYSeries(name));
			lineChartMap.put(name, lineChart);
			companyCharts.add(lineChart);
		}
		return companyCharts;
	}

	private List<Series<Number, Number>> createMobileXYSeries(String name) {
		List<XYChart.Series<Number, Number>> seriesList = new ArrayList<>();
		for (int i = 0; i < MOBILE_FACTORIES; i++) {
			final String seriesName = "Company_Mobile_" + i;
			Series<Number, Number> series = new XYChart.Series<>();
			series.setName(seriesName);
			seriesList.add(series);
			chartSeriesMap.put("Company_Mobile_" + i + "_" + name, series);
		}
		return seriesList;
	}

	private Node createResourceChart(String group) {
		LineChart<Number, Number> lineChart = createNewSimpleLineChart(group);
		lineChart.getData().addAll(createResourceXYSeries(group));
		lineChartMap.put(group, lineChart);
		return lineChart;
	}

	private LineChart<Number, Number> createNewSimpleLineChart(String title) {
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel(GUIChartHelperEnum.getIdetifierByName(title));
		NumberAxis xAxis = new NumberAxis();
		xAxis.setAutoRanging(false);
		xAxis.setLowerBound(0);
		xAxis.setUpperBound(365);
		xAxis.setLabel("Vergangene Zeit in Tagen");
		xAxisList.add(xAxis);
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

	@SuppressWarnings("deprecation")
	@Override
	public void stop() {
		System.out.println("Window Closed");
		ActorSystemContainer.getInstance().getSystem().shutdown();
	}

}
