import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static TileDictionary dict = new TileDictionary();
    final static int NUM_GENERATIONS =400;
    public static int N = 15;
    final static int NUM_TRAINS = 20;
    public static int canvasSize = 800;
    static Random r = new Random(4);
    public static List<int[]> trains = getRandomTrains(NUM_TRAINS);
    public static Population p = new Population();

    public static void main(String[] args) {

        //for each solution calculate values of tiles
        // + # of the trains unable to finish which should be 0
        p.performEvaluation();
        List<Railroad> newP = new ArrayList<>(10);

//        Railroad w = new Railroad(trains);
//        for (int i = 0; i < 10; i++) {
//            for (int j = 0; j <10; j++) {
//                System.out.print(w.world[i][j]+" ");
//            }
//            System.out.println();
//        }
        //int[][] world = Main.dict.transform(w.world);


        Gfx gui = new Gfx(trains);
        JFrame frame = new JFrame("Railroads");
        frame.setSize(canvasSize+200, canvasSize+200);
        frame.add(gui);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        while(p.CURRENT_GENERATION<NUM_GENERATIONS){
           // gui.repaint();
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int index=0;
            //choose the elite
            for (int i = 0; i < Population.ELITISM_K; i++) {
                newP.add(index,p.getBestSolution());
                index++;
            }
            while(index+1<p.solutions.size()){
                Railroad r1 = p.select(Population.ROULETTE_WHEEL_SELECTION);
                Railroad r2 = p.select(Population.ROULETTE_WHEEL_SELECTION);
                //crossover
                if(r.nextDouble()<Population.CROSSOVER_RATE){
                    p.crossover(Population.SINGLE_POINT_CROSSOVER,r1,r2);
                }
                //mutate
                if(r.nextDouble()<Population.MUTATION_RATE){
                    p.mutate(Population.INSERTION_MUTATION,r1);
                }
                if(r.nextDouble()<Population.MUTATION_RATE){
                    p.mutate(Population.INSERTION_MUTATION,r2);
                }
                //add to new pop
                newP.add(index,r1);
                index++;
                //System.out.println(index);
                newP.add(index,r2);
                index++;
                //System.out.println(index);
            }
              Railroad r = p.getBestSolution(); //solution to represent per generation
            System.out.println("best solution id "+r.id+" with fitness "+r.fitness+ "and generation "+p.CURRENT_GENERATION );
            p.performEvaluation();
              Population.CURRENT_GENERATION++;
              //System.out.println("current gen "+p.CURRENT_GENERATION);

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
            train[i]=r.nextInt(0,N);
            System.out.print(train[i]+" ");
        }
        System.out.println();
        return train;
    }
}