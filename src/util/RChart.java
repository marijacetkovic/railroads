package util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ChartUtilities;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RChart {
    public static void saveChart(List<double[]> data) {
        // create data
        XYSeries maxFitness = new XYSeries("Max Fitness");
        XYSeries avgFitness = new XYSeries("Average Fitness");
        int gen=1;
        for (double[] d : data) {
            maxFitness.add(gen, d[0]);
            avgFitness.add(gen,d[1]);
            gen++;
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(maxFitness);
        dataset.addSeries(avgFitness);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Population data",
                "Generation", // x axis label
                "Fitness", // y-axis label
                dataset // data
        );

        File outputFile = new File("railroad_chart.png");
        try {
            ChartUtilities.saveChartAsPNG(outputFile, chart, 800, 600);
            System.out.println("Chart saved as PDF: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
