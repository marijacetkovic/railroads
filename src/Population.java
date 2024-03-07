import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Population {
    static int size=10;
    public static List<Railroad> solutions = new ArrayList<>();
    public static List<Railroad> nextGen = new ArrayList<>();
    public static int generation=0;
    public double preserveBound;
    public double parentBound;
    public List<Railroad> parents = new ArrayList<>();
    public double preserveRate;
    public double mutationRate;
    final static int ELITISM_K = 5;
//    final static int SIZE = 200 + ELITISM_K;  // population size
//    final static int MAX_ITER = 2000;             // max number of iterations
//    final static double MUTATION_RATE = 0.05;     // probability of mutation
//    final static double CROSSOVER_RATE = 0.7;     // probability of crossover
    boolean[] selected;
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
        System.out.println("performing crossover"); // over selected parents
        Random r = new Random();
        int p1 = r.nextInt(parents.size()-1);
        int p2 =  r.nextInt(parents.size()-1);
        for (int i = 0; i < parents.size()-2; i++) {
            while(selected[p1]||selected[p2]||p1==p2){
                p1 = r.nextInt(parents.size()-1);
                p2 = r.nextInt(parents.size()-1);
            }
            selected[p1] = true;
            selected[p2] = true;
            singlePointCrossover(p1,p2);
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    System.out.print(parents.get(i).world[j][k]+ " ");
                }
                System.out.println();
            }


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
        for (int i = 0; i < solutions.size(); i++) {
            solutions.get(i).rateFitness();
        }
    }

    //this gotta be fixed next lalala
    public void performSelection() {
        //keep the best
        //middle can undergo crossover
        //worst can be mutated to have some randomness
        int c = 0;
        int pc = (int) ((int) size*preserveRate);
        for (int i = 0; i < solutions.size(); i++) {
            if(solutions.get(i).fitness>preserveBound&&c<pc){
                c++;
                nextGen.add(solutions.get(i));
                //selected[i]=true; //dont want to crossover these
            }
            else if(solutions.get(i).fitness>parentBound){
                parents.add(solutions.get(i));
            }
        }
        selected=new boolean[parents.size()];
        Arrays.fill(selected,false);
    }

    public void performMutation() {
        //produce a random solution
    }
    public Railroad getNewIndividual(){
        return new Railroad(Main.trains);
    }
}