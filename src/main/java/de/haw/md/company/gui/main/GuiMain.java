package de.haw.md.company.gui.main;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import de.haw.md.company.main.CompanyMainTwo;

public class GuiMain extends Application {

	private static final double SCREEN_X = 1280;
	private static final double SCREEN_Y = 900;
	
	private CompanyMainTwo cmt;

	private void init(Stage primaryStage) {
		cmt = new CompanyMainTwo();
		cmt.simulate();
		Group chartGroup = new Group();
		final AreaChart<Number, Number> ac = companyChart();
		final AreaChart<Number, Number> vpc = companyPieceProdChart();
		final AreaChart<Number, Number> pwc = companyPieceWinChart();
		final AreaChart<Number, Number> ppc = companyPiecePriceChart();
		chartGroup.getChildren().add(ac);
		chartGroup.getChildren().add(vpc);
		chartGroup.getChildren().add(pwc);
		chartGroup.getChildren().add(ppc);
		primaryStage.setScene(new Scene(chartGroup, SCREEN_X, SCREEN_Y));
		primaryStage.resizableProperty().set(false);
	}

	@SuppressWarnings("unchecked")
	private AreaChart<Number, Number> companyChart(){
		final NumberAxis xAxis = new NumberAxis(1, 170, 5);
		final NumberAxis yAxis = new NumberAxis();
		final AreaChart<Number, Number> ac = new AreaChart<Number, Number>(xAxis, yAxis);
		ac.setTitle("Gewinn beider Unternehmen");
		ac.setMinWidth(SCREEN_X/2);
		ac.setMinHeight(SCREEN_Y/4);
		XYChart.Series<Number, Number> company1 = new XYChart.Series<>();
		company1.setName("Unternehmen 1");
		XYChart.Series<Number, Number> company2 = new XYChart.Series<>();
		company2.setName("Unternehmen 2");
		XYChart.Series<Number, Number> company3 = new XYChart.Series<>();
		company3.setName("Unternehmen 3");
		XYChart.Series<Number, Number> company4 = new XYChart.Series<>();
		company4.setName("Unternehmen 4");
		XYChart.Series<Number, Number> company5 = new XYChart.Series<>();
		company5.setName("Unternehmen 5");
		BigDecimal gesGewC1 = BigDecimal.ZERO;
		BigDecimal gesGewC2 = BigDecimal.ZERO;
		BigDecimal gesGewC3 = BigDecimal.ZERO;
		BigDecimal gesGewC4 = BigDecimal.ZERO;
		BigDecimal gesGewC5 = BigDecimal.ZERO;
		for (int i = 0; i < cmt.currentZyklus; i++) {
			gesGewC1 = gesGewC1.add(cmt.getC1().getGewinn()[i].multiply(cmt.getC1().getVerkaufteProd()[i]).divide(new BigDecimal("1.19"), 2, RoundingMode.HALF_DOWN));
			gesGewC2 = gesGewC2.add(cmt.getC2().getGewinn()[i].multiply(cmt.getC2().getVerkaufteProd()[i]).divide(new BigDecimal("1.19"), 2, RoundingMode.HALF_DOWN));
			gesGewC3 = gesGewC3.add(cmt.getC3().getGewinn()[i].multiply(cmt.getC3().getVerkaufteProd()[i]).divide(new BigDecimal("1.19"), 2, RoundingMode.HALF_DOWN));
			gesGewC4 = gesGewC4.add(cmt.getC4().getGewinn()[i].multiply(cmt.getC4().getVerkaufteProd()[i]).divide(new BigDecimal("1.19"), 2, RoundingMode.HALF_DOWN));
			gesGewC5 = gesGewC5.add(cmt.getC5().getGewinn()[i].multiply(cmt.getC5().getVerkaufteProd()[i]).divide(new BigDecimal("1.19"), 2, RoundingMode.HALF_DOWN));
			company1.getData().add(new XYChart.Data<Number, Number>(i, gesGewC1));
			company2.getData().add(new XYChart.Data<Number, Number>(i, gesGewC2));
			company3.getData().add(new XYChart.Data<Number, Number>(i, gesGewC3));
			company4.getData().add(new XYChart.Data<Number, Number>(i, gesGewC4));
			company5.getData().add(new XYChart.Data<Number, Number>(i, gesGewC5));
		}
		ac.getData().addAll(company1, company2, company3, company4, company5);
		return ac;
	}
	
	@SuppressWarnings("unchecked")
	private AreaChart<Number, Number> companyPieceProdChart(){
		final NumberAxis xAxis = new NumberAxis(1, 170, 5);
		final NumberAxis yAxis = new NumberAxis();
		final AreaChart<Number, Number> ac = new AreaChart<Number, Number>(xAxis, yAxis);
		ac.setTitle("Artikelgewinn");
		ac.setMinWidth(SCREEN_X/2);
		ac.setMinHeight(SCREEN_Y/2);
		ac.relocate(SCREEN_X/2, 0);
		XYChart.Series<Number, Number> company1ag = new XYChart.Series<>();
		company1ag.setName("Unternehmen 1 Verkaufte Produkte (p.W.)");
		XYChart.Series<Number, Number> company2ag = new XYChart.Series<>();
		company2ag.setName("Unternehmen 2 Verkaufte Produkte (p.W.)");
		XYChart.Series<Number, Number> company3ag = new XYChart.Series<>();
		company3ag.setName("Unternehmen 3 Verkaufte Produkte (p.W.)");
		XYChart.Series<Number, Number> company4ag = new XYChart.Series<>();
		company4ag.setName("Unternehmen 4 Verkaufte Produkte (p.W.)");
		XYChart.Series<Number, Number> company5ag = new XYChart.Series<>();
		company5ag.setName("Unternehmen 5 Verkaufte Produkte (p.W.)");
		for (int i = 0; i < cmt.currentZyklus; i++) {
			company1ag.getData().add(new XYChart.Data<Number, Number>(i, cmt.getC1().getVerkaufteProd()[i]));
			company2ag.getData().add(new XYChart.Data<Number, Number>(i, cmt.getC2().getVerkaufteProd()[i]));
			company3ag.getData().add(new XYChart.Data<Number, Number>(i, cmt.getC3().getVerkaufteProd()[i]));
			company4ag.getData().add(new XYChart.Data<Number, Number>(i, cmt.getC4().getVerkaufteProd()[i]));
			company5ag.getData().add(new XYChart.Data<Number, Number>(i, cmt.getC5().getVerkaufteProd()[i]));
		}
		ac.getData().addAll(company1ag, company2ag, company3ag, company4ag, company5ag);
		return ac;
	}
	
	@SuppressWarnings("unchecked")
	private AreaChart<Number, Number> companyPieceWinChart(){
		final NumberAxis xAxis = new NumberAxis(1, 170, 5);
		final NumberAxis yAxis = new NumberAxis();
		final AreaChart<Number, Number> ac = new AreaChart<Number, Number>(xAxis, yAxis);
		ac.setTitle("Artikelgewinn");
		ac.setMinWidth(SCREEN_X/2);
		ac.setMinHeight(SCREEN_Y/2);
		ac.relocate(0, SCREEN_Y/2);
		XYChart.Series<Number, Number> company1ag = new XYChart.Series<>();
		company1ag.setName("Unternehmen 1 Artikelgewinn");
		XYChart.Series<Number, Number> company2ag = new XYChart.Series<>();
		company2ag.setName("Unternehmen 2 Artikelgewinn");
		XYChart.Series<Number, Number> company3ag = new XYChart.Series<>();
		company3ag.setName("Unternehmen 3 Artikelgewinn");
		XYChart.Series<Number, Number> company4ag = new XYChart.Series<>();
		company4ag.setName("Unternehmen 4 Artikelgewinn");
		XYChart.Series<Number, Number> company5ag = new XYChart.Series<>();
		company5ag.setName("Unternehmen 5 Artikelgewinn");
		for (int i = 0; i < cmt.currentZyklus; i++) {
			company1ag.getData().add(new XYChart.Data<Number, Number>(i, cmt.getC1().getGewinn()[i]));
			company2ag.getData().add(new XYChart.Data<Number, Number>(i, cmt.getC2().getGewinn()[i]));
			company3ag.getData().add(new XYChart.Data<Number, Number>(i, cmt.getC3().getGewinn()[i]));
			company4ag.getData().add(new XYChart.Data<Number, Number>(i, cmt.getC4().getGewinn()[i]));
			company5ag.getData().add(new XYChart.Data<Number, Number>(i, cmt.getC5().getGewinn()[i]));
		}
		ac.getData().addAll(company1ag, company2ag, company3ag, company4ag, company5ag);
		return ac;
	}
	
	@SuppressWarnings("unchecked")
	private AreaChart<Number, Number> companyPiecePriceChart(){
		final NumberAxis xAxis = new NumberAxis(1, 170, 10);
		final NumberAxis yAxis = new NumberAxis(230,490, 10);
		final AreaChart<Number, Number> ac = new AreaChart<Number, Number>(xAxis, yAxis);
		ac.setTitle("Artikelpreis");
		ac.setMinWidth(SCREEN_X/2);
		ac.setMinHeight(SCREEN_Y/2);
		ac.relocate(SCREEN_X/2, SCREEN_Y/2);
		XYChart.Series<Number, Number> company1ap = new XYChart.Series<>();
		company1ap.setName("Unternehmen 1 Artikelpreis");
		XYChart.Series<Number, Number> company2ap = new XYChart.Series<>();
		company2ap.setName("Unternehmen 2 Artikelpreis");
		XYChart.Series<Number, Number> company3ap = new XYChart.Series<>();
		company3ap.setName("Unternehmen 3 Artikelpreis");
		XYChart.Series<Number, Number> company4ap = new XYChart.Series<>();
		company4ap.setName("Unternehmen 4 Artikelpreis");
		XYChart.Series<Number, Number> company5ap = new XYChart.Series<>();
		company5ap.setName("Unternehmen 5 Artikelpreis");
		for (int i = 0; i < cmt.currentZyklus; i++) {
			company1ap.getData().add(new XYChart.Data<Number, Number>(i, cmt.getC1().getCalcPreis()[i]));
			company2ap.getData().add(new XYChart.Data<Number, Number>(i, cmt.getC2().getCalcPreis()[i]));
			company3ap.getData().add(new XYChart.Data<Number, Number>(i, cmt.getC3().getCalcPreis()[i]));
			company4ap.getData().add(new XYChart.Data<Number, Number>(i, cmt.getC4().getCalcPreis()[i]));
			company5ap.getData().add(new XYChart.Data<Number, Number>(i, cmt.getC5().getCalcPreis()[i]));
		}
		ac.getData().addAll(company1ap, company2ap, company3ap, company4ap, company5ap);
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
