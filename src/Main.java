import util.Config;
import util.TileDictionary;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static TileDictionary dict = new TileDictionary();
    static Random r = new Random(Config.RANDOM_SEED);
    public static List<int[]> trains = getRandomTrains(Config.NUM_TRAINS);

    public static Population p = new Population();

    static Railroad bestIndividual = p.getSolutions().get(0);
    private static BlockingQueue<Railroad> bestIndividualQueue = new LinkedBlockingQueue<>();
    private static int mode;

    public static void main(String[] args) {
        //List<Railroad> newP = new ArrayList<>(10);

//        Railroad w = new Railroad(trains);
//        for (int i = 0; i < 10; i++) {
//            for (int j = 0; j <10; j++) {
//                System.out.print(w.world[i][j]+" ");
//            }
//            System.out.println();
//        }
        //int[][] world = Main.dict.transform(w.world);
        System.out.println(args);
        if(args.length<1){
            System.out.println("Please enter preferred mode as argument.");
            System.out.println("Sequential mode - 1");
            System.out.println("Parallel mode - 2");
        }
        else{
            mode = Integer.parseInt(args[0]);
        }
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
//        double start = System.currentTimeMillis();
//        new RSequential(p,bestIndividual,bestIndividualQueue).execute();
//        double end = System.currentTimeMillis();
//        System.out.println("Sequential time is "+(end-start));
//        double start2 = System.currentTimeMillis();
        //new RParallel(8,p,bestIndividual,bestIndividualQueue).execute();
//        double end2 = System.currentTimeMillis();
//        System.out.println("Parallel time is "+(end2-start2));
//        new RParallel(8,p,bestIndividual,bestIndividualQueue).execute();

       // new RDistributed(p,bestIndividual,bestIndividualQueue).execute(args);
    }

    public void runGeneticAlgorithm(int mode){
        if (mode==1){
            new RSequential(p,bestIndividual,bestIndividualQueue).execute();
        }
        else if (mode==2){
            new RParallel(Runtime.getRuntime().availableProcessors(), p,bestIndividual,bestIndividualQueue).execute();
        }
    }
    public static List<int[]> getRandomTrains(int numT){
        List<int[]> trains = new ArrayList<>();
        for (int i = 0; i < numT; i++) {
            System.out.println("TRAIN "+i);
            trains.add(generateRandomTrain());
        }
        return trains;
    }

    private static int[] generateRandomTrain(){
        int[] train = new int[4];
        for (int i = 0; i < 4; i++) {
            train[i]=r.nextInt(0,Config.WORLD_SIZE);
            System.out.print(train[i]+" ");
        }
        System.out.println();
        return train;
    }

}