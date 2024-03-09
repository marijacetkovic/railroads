import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Population {
    static int size=10;
    public double totalFitness;
    public static List<Railroad> solutions = new ArrayList<>();
    public static int generation=0;
    public List<Railroad> parents = new ArrayList<>();
    public double mutationRate;
    final static int ELITISM_K = 5;

    //    final static int SIZE = 200 + ELITISM_K;  // population size
//    final static int MAX_ITER = 2000;             // max number of iterations
//    final static double MUTATION_RATE = 0.05;     // probability of mutation
//    final static double CROSSOVER_RATE = 0.7;     // probability of crossover
    Random r = new Random();

    public Population(){
        initializeSolutions();
        //System.out.println("im here"+ solutions.size());
    }

    public void initializeSolutions(){
        for (int i = 0; i < size; i++) {
            solutions.add(new Railroad(Main.trains)); //train coordinates should be supplied
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
        this.totalFitness = 0.0;
        for (int i = 0; i < solutions.size(); i++) {
            this.totalFitness += solutions.get(i).rateFitness();
        }
    }

//    public void performSelection() {
//        int c = 0;
//        int pc = (int) ((int) size*preserveRate);
//        for (int i = 0; i < solutions.size(); i++) {
//            if(solutions.get(i).fitness>preserveBound&&c<pc){
//                c++;
//                nextGen.add(solutions.get(i));
//                //selected[i]=true; //dont want to crossover these
//            }
//            else if(solutions.get(i).fitness>parentBound){
//                parents.add(solutions.get(i));
//            }
//        }
//        selected=new boolean[parents.size()];
//        Arrays.fill(selected,false);
//    }

    public Railroad select(){
        double rand = r.nextDouble() * this.totalFitness; //rnd nr between 0 and total fitness of the population
        int index = 0;
        for (int i=0; i<solutions.size(); i++) {
            rand -= solutions.get(i).getFitness();
            if(rand<0){
                index = i;
                break;
            }
        }
        return solutions.get(index);
    }

    //returns a railroad with best fitness
    public Railroad getBestSolution(){
        double bestScore = 0;
        int index = -1;
        for (int i = 0; i < solutions.size(); i++) {
            double s = solutions.get(i).fitness;
            if(s>=bestScore){
                bestScore = s;
                index = i;
            }
        }
        return solutions.get(index);
    }
    public void setSolutions(List<Railroad> solutions) {
        this.solutions = solutions;
    }


    public void performMutation() {
        //produce a random solution
    }
    public Railroad getNewIndividual(){
        return new Railroad(Main.trains);
    }
}