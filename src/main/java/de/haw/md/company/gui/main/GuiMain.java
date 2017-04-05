package de.haw.md.company.gui.main;

import java.math.BigDecimal;
import java.util.Arrays;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.stage.Stage;
import de.haw.md.company.main.CompanyMainTwo;

@SuppressWarnings("restriction")
public class GuiMain extends Application {

	private static final double SCREEN_X = 1280;
	private static final double SCREEN_Y = 900;

	private CompanyMainTwo cmt;

	private static final int START_DIAGRAMM = 0;

	private AreaChart<Number, Number> price;
	private AreaChart<Number, Number> cost;
	private AreaChart<Number, Number> profitPP;
	private AreaChart<Number, Number> selledP;
	private AreaChart<Number, Number> profitA;
	private AreaChart<Number, Number> priceChange;

	private void init(Stage primaryStage) {
		cmt = new CompanyMainTwo();
		cmt.simulate();
		Group root = new Group();
		Group toolGroup = new Group();
		ToolBar toolBar = new ToolBar();
		final Button buttonPrice = new Button("Preis p.P");
		toolBar.getItems().add(buttonPrice);
		final Button buttonCost = new Button("Kosten p.P");
		toolBar.getItems().add(buttonCost);
		final Button buttonProfit = new Button("Gewinn p.P");
		toolBar.getItems().add(buttonProfit);
		final Button buttonSelled = new Button("Verkaufte Prod. p.W");
		toolBar.getItems().add(buttonSelled);
		final Button buttonProfitA = new Button("Ges.Gewinn");
		toolBar.getItems().add(buttonProfitA);
		final Button buttonPriceChange = new Button("Preisänderung");
		toolBar.getItems().add(buttonPriceChange);
		toolGroup.getChildren().add(toolBar);
		Group chartGroup = new Group();
		price = createChart("Preis p.P", 10, 5, 0, 65, cmt.getC1().getCalcPreis(), cmt.getC2().getCalcPreis(), cmt.getC3().getCalcPreis(), cmt.getC4()
				.getCalcPreis(), cmt.getC5().getCalcPreis());
		chartGroup.getChildren().add(price);
		cost = createChart("Kosten p.P", 10, 5, 0, 80, cmt.getC1().getPkPreis(), cmt.getC2().getPkPreis(), cmt.getC3().getPkPreis(), cmt.getC4().getPkPreis(),
				cmt.getC5().getPkPreis());
		cost.setVisible(false);
		chartGroup.getChildren().add(cost);
		profitPP = createChart("Gewinn p.P", 10, 0.5, -10, 10, cmt.getC1().getGewinn(), cmt.getC2().getGewinn(), cmt.getC3().getGewinn(), cmt.getC4()
				.getGewinn(), cmt.getC5().getGewinn());
		profitPP.setVisible(false);
		chartGroup.getChildren().add(profitPP);
		selledP = createChart("Verkaufte Produkte (p.W.)", 10, 50000, 0, 1010000, cmt.getC1().getVerkaufteProd(), cmt.getC2().getVerkaufteProd(), cmt.getC3()
				.getVerkaufteProd(), cmt.getC4().getVerkaufteProd(), cmt.getC5().getVerkaufteProd());
		chartGroup.getChildren().add(selledP);
		selledP.setVisible(false);
		profitA = createChart("Ges.Gewinn", 20, 5000000, 0, 130000000, cmt.getC1().getGesGew(), cmt.getC2().getGesGew(), cmt.getC3().getGesGew(), cmt.getC4()
				.getGesGew(), cmt.getC5().getGesGew());
		chartGroup.getChildren().add(profitA);
		profitA.setVisible(false);
		priceChange = createChart("Preisänderung", 10, 0.001, 0, 0.1, cmt.getC1().getPaArray(), cmt.getC2().getPaArray(), cmt.getC3().getPaArray(), cmt.getC4()
				.getPaArray(), cmt.getC5().getPaArray());
		chartGroup.getChildren().add(priceChange);
		priceChange.setVisible(false);
		buttonPrice.setOnAction(createEventHandler(true, false, false, false, false, false));
		buttonCost.setOnAction(createEventHandler(false, true, false, false, false, false));
		buttonProfit.setOnAction(createEventHandler(false, false, true, false, false, false));
		buttonSelled.setOnAction(createEventHandler(false, false, false, true, false, false));
		buttonProfitA.setOnAction(createEventHandler(false, false, false, false, true, false));
		buttonPriceChange.setOnAction(createEventHandler(false, false, false, false, false, true));
		root.getChildren().add(toolBar);
		root.getChildren().add(chartGroup);
		primaryStage.setScene(new Scene(root, SCREEN_X, SCREEN_Y));
		primaryStage.resizableProperty().set(false);
	}

	private EventHandler<ActionEvent> createEventHandler(boolean triggerPrice, boolean triggerCost, boolean triggerProfitPP, boolean triggerSelledP,
			boolean triggerProfitA, boolean triggerPriceChange) {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				price.setVisible(triggerPrice);
				cost.setVisible(triggerCost);
				profitPP.setVisible(triggerProfitPP);
				selledP.setVisible(triggerSelledP);
				profitA.setVisible(triggerProfitA);
				priceChange.setVisible(triggerPriceChange);
			}
		};
	}

	@SuppressWarnings("unchecked")
	private AreaChart<Number, Number> createChart(String text, int diagramStepsX, double diagramStepsY, double minSize, double maxSize,
			BigDecimal[]... dataArrays) {
		final NumberAxis xAxis = new NumberAxis(START_DIAGRAMM, CompanyMainTwo.ANZ_ZYKLEN, diagramStepsX);
		final NumberAxis yAxis = new NumberAxis(minSize, maxSize, diagramStepsY);
		final AreaChart<Number, Number> ac = new AreaChart<Number, Number>(xAxis, yAxis);
		ac.setTitle(text);
		ac.setMinWidth(SCREEN_X);
		ac.setMinHeight(SCREEN_Y - 30);
		ac.relocate(0, 30);
		XYChart.Series<Number, Number>[] company = new XYChart.Series[dataArrays.length];
		for (int i = 0; i < dataArrays.length; i++) {
			company[i] = new XYChart.Series<>();
			company[i].setName("Unternehmen " + (i + 1) + " " + text);
			BigDecimal[] dataSet = dataArrays[i];
			for (int j = 0; j < cmt.currentZyklus; j++) {
				company[i].getData().add(new XYChart.Data<Number, Number>(j, dataSet[j]));
			}
		}
		ac.getData().addAll(Arrays.asList(company));
		return ac;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		init(primaryStage);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
