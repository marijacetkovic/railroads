import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static TileDictionary dict = new TileDictionary();
    public static int N = 3;
    public static int numTrains = 10;
    public static int canvasSize = 800;
    public static List<int[]> trains = getRandomTrains(numTrains);
    public static void main(String[] args) {

        //for each solution calculate values of tiles
        // + # of the trains unable to finish which should be 0
        Population p = new Population();
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
        frame.setSize(canvasSize, canvasSize);
        frame.add(gui);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        while(true){
//            p.performEvaluation();
//            p.performSelection();
//            p.performCrossover();
//            p.performMutation();
//            Population.generation++;
        }

    }

    public static List<int[]> getRandomTrains(int numT){
        List<int[]> trains = new ArrayList<>();
        for (int i = 0; i < numT; i++) {
            trains.add(generateRandomTrain());
        }
        return trains;
    }

    private static int[] generateRandomTrain(){
        Random random = new Random();
        int[] train = new int[4];
        for (int i = 0; i < 4; i++) {
            train[i]=random.nextInt(0,N);
           // System.out.print(train.get(i)+" ");
        }
       // System.out.println();
        return train;
    }
}