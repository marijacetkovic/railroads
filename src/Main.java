import org.jfree.chart.block.Block;
import util.Config;
import util.TileDictionary;
import util.TrainGenerator;

import javax.swing.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static TileDictionary dict = new TileDictionary();
    public static List<int[]> trains = TrainGenerator.getRandomTrains(Config.NUM_TRAINS);

    public static Population p = new Population();

    static Railroad bestIndividual;
    private static BlockingQueue<Railroad> bestIndividualQueue = new LinkedBlockingQueue<>();
    private static int mode;

    public static void main(String[] args) {
        System.out.println(args);
        if(args.length<1){
            System.out.println("Please enter preferred mode as argument.");
            System.out.println("Sequential mode - 1");
            System.out.println("Parallel mode - 2");
        }
        else{
            mode = Integer.parseInt(args[0]);
        }
        renderGui(trains, bestIndividualQueue);
        runGeneticAlgorithm(2);
    }

    public static void runGeneticAlgorithm(int mode){
        if (mode==1){
            new RSequential(p,bestIndividual,bestIndividualQueue).execute();
        }
        else if (mode==2){
            new RParallel(Runtime.getRuntime().availableProcessors(), p,bestIndividual,bestIndividualQueue).execute();
        }
    }

    public static void renderGui(List<int[]> trains, BlockingQueue<Railroad> bestIndividualQueue){
        SwingUtilities.invokeLater(() -> {
            if (Config.RENDER_GUI) {
                Gfx gui = new Gfx(trains, bestIndividualQueue);
                JFrame frame = new JFrame("Railroads");
                frame.setSize(1000, 1000);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(gui);
                frame.setVisible(true);
            }
        });
    }
}