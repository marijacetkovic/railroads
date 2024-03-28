import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static TileDictionary dict = new TileDictionary();
    static Random r = new Random(Config.RANDOM_SEED);
    public static List<int[]> trains = getRandomTrains(Config.NUM_TRAINS);

    public static Population p = new Population();

    static Railroad bestIndividual = p.solutions.get(0);
    private static BlockingQueue<Railroad> bestIndividualQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        p.performEvaluation();
        //List<Railroad> newP = new ArrayList<>(10);

//        Railroad w = new Railroad(trains);
//        for (int i = 0; i < 10; i++) {
//            for (int j = 0; j <10; j++) {
//                System.out.print(w.world[i][j]+" ");
//            }
//            System.out.println();
//        }
        //int[][] world = Main.dict.transform(w.world);

        if(Config.RENDER_GUI){
            Gfx gui = new Gfx(trains,bestIndividualQueue);
            JFrame frame = new JFrame("Railroads");
            frame.setSize(Config.CANVAS_SIZE, Config.CANVAS_SIZE);
            frame.add(gui);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        }



        while(p.CURRENT_GENERATION<Config.NUM_GENERATIONS){
            List<Railroad> newP = new ArrayList<>(10);
            int index=0;
            //choose the elite
            for (int i = 0; i < Config.ELITISM_K; i++) {
                Railroad r = p.getBestSolution();
               // r.selected=false;
                newP.add(r);
                index++;
            }
           // p.sortPopulation();
            while(index<p.solutions.size()){
                Railroad r1 = p.select(Config.TEST_SELECTION);
                Railroad r2 = p.select(Config.TEST_SELECTION);
                //crossover
                if(Math.random()<Config.CROSSOVER_RATE){
                    p.crossover(Config.SINGLE_POINT_CROSSOVER,r1,r2);
                }
                //mutate
                if(Math.random()<Config.MUTATION_RATE){
                    p.mutate(Config.INSERTION_MUTATION,r1);
                }
                if(Math.random()<Config.MUTATION_RATE){
                    p.mutate(Config.INSERTION_MUTATION,r2);
                }
                //add to new pop
                newP.add(r1);
                //System.out.println(index);
                newP.add(r2);
                index+=2;
                //System.out.println(index);
            }
            p.setSolutions(newP);
            bestIndividual = p.getBestSolution(); //solution to represent per generation
            bestIndividualQueue.offer(bestIndividual);
            System.out.println("best solution id "+bestIndividual.id+" with fitness "+bestIndividual.fitness+ " and generation "+p.CURRENT_GENERATION );

            p.performEvaluation();
              Population.CURRENT_GENERATION++;
              //System.out.println("current gen "+p.CURRENT_GENERATION);

        }
    }

    public static Railroad getBestIndividual(){
        return bestIndividual;
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