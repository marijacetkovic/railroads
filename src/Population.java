import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Population {
    static int size=10;
    public static List<Railroad> solutions = new ArrayList<>(size);
    public static int generation=0;

    public Population(){
        initializeSolutions();
        //System.out.println("im here"+ solutions.size());

    }

    public void initializeSolutions(){
        for (int i = 0; i < size; i++) {
            solutions.add(new Railroad(Main.trains)); //train coordinates should be supplied
        }
    }

    public void performCrossover(){
        System.out.println("performing crossover");
        for (int i = 0; i < solutions.size()-2; i++) {
            singlePointCrossover(i,i+1);
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    System.out.print(solutions.get(i).world[j][k]+ " ");
                }
                System.out.println();
            }

            i++;

        }
    }

    public void singlePointCrossover(int i1, int i2){
        //there are multiple ways to do cross over, for now lets do the simplest
        //Random r = new Random(0);
        //int i = r.nextInt(0,Main.N);
        Railroad r1 = solutions.get(i1);
        Railroad r2 = solutions.get(i2);

        int[][] c1 = new int[Main.N][Main.N];
        int[][] c2 = new int[Main.N][Main.N];

        for (int i = 0; i < Main.N/2; i++) {
            c1[i] = Arrays.copyOf(r1.world[i],r1.N);
            c2[i] = Arrays.copyOf(r2.world[i],r1.N);
        }
        for (int i = Main.N/2; i < Main.N; i++) {
            c1[i] = Arrays.copyOf(r2.world[i],r1.N);
            c2[i] = Arrays.copyOf(r1.world[i],r1.N);
        }
        r1.world = c1;
        r2.world = c2;
    }

    public void performEvaluation() {
        //evaluate all solutions
        //first evaluation can be numTrainsThatFinish/numTrains
    }

    public void performSelection() {
        //select the best
        //keep the best
        //middle can undergo crossover
        //worst can be mutated to have some randomness
    }

    public void performMutation() {
        //produce a random solution
    }
}