/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.axis.AxisLabelLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

/**
 *
 * @author hiago
 */
public class Chart_Generator{
    
/*Resumidamente, a construção de gráficos com o JFreeChart é feita em quatro etapas:
1. Definição do conjunto de dados a serem plotados (o dataset)
2. Criação do modelo do gráfico (representado por um objeto da classe JFreeChart)
3. (Opcional) Alteração de propriedades de plotagem (fazendo modificações no objeto Plot obtido a partir do objeto JFreeChart)
4. Geração da imagem do gráfico a partir do seu modelo (ou de um componente visual, como um painel Swing)*/ 
    
    
    
    public Chart_Generator() {
    }

    public JFreeChart createChart(ArrayList<Double> list, String xlabel, String ylabel, double lower_range, double upper_range) {
        //creating the chart
        JFreeChart xylineChart= ChartFactory.createXYLineChart("",
                xlabel, ylabel, createDataset(list, ylabel), PlotOrientation.VERTICAL, true, true, false);
        
        
        //setting some options
        final XYPlot plot= xylineChart.getXYPlot();
        
        plot.getDomainAxis().setAutoRange(true); 
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.getRenderer().setSeriesPaint(0, Color.red);
        //changing the range of Y axis
        NumberAxis range= (NumberAxis) plot.getRangeAxis();
        range.setRange(lower_range, upper_range);
        
        return xylineChart;
    }

    private XYDataset createDataset(ArrayList<Double> list, String serie_name) {
        final XYSeries distance_1_0= new XYSeries(serie_name);
        for(int i=0; i<list.size(); i++){
            distance_1_0.add(i, list.get(i));
        }
        final XYSeriesCollection dataset= new XYSeriesCollection();
        dataset.addSeries(distance_1_0);
        return dataset;
    }
    
    public void plotChart(JFreeChart chart, String path){
        try{
            File file= new File(path);
            ChartUtilities.saveChartAsPNG(file, chart, 500, 400);
        }catch(Exception error){
            System.out.println(error.getMessage());
        }
    }
    
    public JFreeChart createParaconsistentChart(ArrayList<Double> list){
        final XYSeries all_features= new XYSeries("Ponto P (Todas características)");
        all_features.add(list.get(0), list.get(1));
        final XYSeries selected_features= new XYSeries("Ponto P (Características selecionadas)");
        selected_features.add(list.get(2), list.get(3));
        
        final XYSeriesCollection dataset= new XYSeriesCollection();
        dataset.addSeries(all_features);
        dataset.addSeries(selected_features);
        
        JFreeChart scatter_chart= ChartFactory.createScatterPlot("", "", "", dataset, PlotOrientation.VERTICAL, true, true, false);
        
        //setting some options
        final XYPlot plot= scatter_chart.getXYPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.getRenderer().setSeriesPaint(0, Color.red);
        plot.getRenderer().setSeriesPaint(1, Color.blue);
        plot.setDomainCrosshairVisible(true); 
        plot.setRangeCrosshairVisible(true);  
        plot.setOutlineVisible(false); 
        //changing the ranges
        NumberAxis domain= (NumberAxis) plot.getDomainAxis();   
        domain.setRange(-1.0, 1.0); 
        NumberAxis range= (NumberAxis) plot.getRangeAxis(); 
        range.setRange(-1.0, 1.0);
        
        //setting a diamond shape
        Polygon polygon= new Polygon();
        polygon.addPoint(0, -1);
        polygon.addPoint(-1, 0);
        polygon.addPoint(0, 1);
        polygon.addPoint(1, 0);
        
        XYShapeAnnotation area= new XYShapeAnnotation(polygon, new BasicStroke(), Color.BLACK);
        plot.addAnnotation(area); 
        
        //custom label G1 and G2
        final Marker g1= new ValueMarker(0.15);
        g1.setPaint(Color.white);
        g1.setLabel("G1");
        g1.setLabelAnchor(RectangleAnchor.RIGHT);
        g1.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addRangeMarker(g1);
        
        final Marker g2= new ValueMarker(0.15);
        g2.setPaint(Color.white);
        g2.setLabel("G2");
        g2.setLabelAnchor(RectangleAnchor.TOP_LEFT);
        g2.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addDomainMarker(g2);
        
        return scatter_chart;
    }
    
}
