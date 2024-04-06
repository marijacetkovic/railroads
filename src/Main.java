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

    static Railroad bestIndividual = p.solutions.get(0);
    private static BlockingQueue<Railroad> bestIndividualQueue = new LinkedBlockingQueue<>();

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

        if(Config.RENDER_GUI){
            Gfx gui = new Gfx(trains,bestIndividualQueue);
            JFrame frame = new JFrame("Railroads");
            frame.setSize(Config.CANVAS_SIZE, Config.CANVAS_SIZE);
            frame.add(gui);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        }
        new RSequential(p,bestIndividual,bestIndividualQueue).execute();

        //new RParallel(4,p,bestIndividual,bestIndividualQueue).execute();
       // new RDistributed().execute();
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

    public static Railroad generateTrivialSol(){
            int n = Config.WORLD_SIZE;
            int[][] matrix = new int[n][n];

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    matrix[i][j] = 11;
                    //System.out.print(matrix[i][j]+ " ");
                }
                //System.out.println();
            }

            Railroad r = new Railroad(trains,-1);
            r.setWorld(matrix);
            return r;
    }
}