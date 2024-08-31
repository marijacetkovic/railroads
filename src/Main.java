import util.Config;
import util.TileDictionary;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static TileDictionary dict = new TileDictionary();
    public static List<int[]> trains;

    public static Population p;

    static Railroad bestIndividual;
    private static BlockingQueue<Railroad> bestIndividualQueue;

    public static void main(String[] args) {
        initProgram(args);
    }

    private static void initProgram(String[] args){

        if (args.length < 3) {
            System.out.println("Usage: java Main <modeOfExecution> <worldSize> <numTrains> <displayGui>");
            System.exit(1);
        }

        int mode = Integer.parseInt(args[0]);
        int worldSize = Integer.parseInt(args[1]);
        int numTrains = Integer.parseInt(args[2]);
        boolean displayGui = Boolean.parseBoolean(args[3]);

        Config.WORLD_SIZE = worldSize;
        Config.NUM_TRAINS = numTrains;
        Config.RENDER_GUI = displayGui;
        runGeneticAlgorithm(mode);

    }

    public static void runGeneticAlgorithm(int mode){
        p=new Population();
        bestIndividualQueue=new LinkedBlockingQueue<>();
        trains = TrainGenerator.getRandomTrains(Config.NUM_TRAINS);
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