import java.util.*;

public class Population {
    final static int POPULATION_SIZE=10;
    public double totalFitness;
    public static List<Railroad> solutions = new ArrayList<>();
    public static int generation=0;
    public double mutationRate;
    final static int ELITISM_K = 5;
    final static int ROULETTE_WHEEL_SELECTION = 0;
    final static int TRUNCATION_SELECTION = 1;
    final static int SINGLE_POINT_CROSSOVER = 0;
    final static int OTHER_CROSSOVER = 1;
    final static int INSERTION_MUTATION = 0;
    final static int OTHER_MUTATION = 1;
    static int tsIndex = -1;

    //    final static int SIZE = 200 + ELITISM_K;  // population size
//    final static int MAX_ITER = 2000;             // max number of iterations
//    final static double MUTATION_RATE = 0.05;     // probability of mutation
    final static double CROSSOVER_RATE = 0.7;     // probability of crossover
    Random r = new Random();

    public Population(){
        initializeSolutions();
        //System.out.println("im here"+ solutions.size());
    }

    public void initializeSolutions(){
        for (int i = 0; i < POPULATION_SIZE; i++) {
            solutions.add(new Railroad(Main.trains)); //train coordinates should be supplied
        }
    }



    public void singlePointCrossover(Railroad r1, Railroad r2){
        //Random r = new Random(0);
        //int i = r.nextInt(0,Main.N);

        //copies half of one parents genes and half of other
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
        for (int i = 0; i < POPULATION_SIZE; i++) {
            this.totalFitness += solutions.get(i).rateFitness();
        }
    }


    public Railroad rouletteWheelSelection(){
        //probabilistically chooses
        //the ones w greater fitness are more probable, but might also choose w lower fitness
        //keeps population diverse
        double rand = r.nextDouble() * this.totalFitness; //rnd nr between 0 and total fitness of the population
        int index = 0;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            rand -= solutions.get(i).getFitness();
            if(rand<0){
                index = i;
                break;
            }
        }
        return solutions.get(index);
    }

    public Railroad truncationSelection(){
        //chooses the ones with greater fitness first
        //assumes the solutions are sorted
        //might not converge well
        tsIndex++;
        return solutions.get(tsIndex);
    }

    public Railroad select(int selectionType){
        Railroad r;
        switch (selectionType) {
            case ROULETTE_WHEEL_SELECTION:
                r = rouletteWheelSelection();
                break;
            case TRUNCATION_SELECTION:
                r = truncationSelection();
                break;
            default:
                r = null;
                System.out.println("invalid selection type");
        }
        return r;
    }

    public void crossover(int crossoverType,Railroad r1, Railroad r2){
        switch (crossoverType) {
            case SINGLE_POINT_CROSSOVER:
                singlePointCrossover(r1,r2);
                break;
            case OTHER_CROSSOVER:
                break;
            default:
                System.out.println("invalid crossover type");
        }
    }

    public void mutate(int mutationType, Railroad r){
        switch (mutationType) {
            case INSERTION_MUTATION:
                r.insertionMutation();
                break;
            case OTHER_MUTATION:
                break;
            default:
                System.out.println("invalid mutation type");
        }
    }

    public void sortPopulation(){
        //used for truncation selection
        tsIndex = -1; //reset counter
        Collections.sort(this.solutions); //sort
    }

    //returns a railroad with best fitness
    public Railroad getBestSolution(){
        double bestScore = 0;
        int index = -1;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            Railroad r = solutions.get(i);
            if(r.fitness>=bestScore&&!r.selected){
                bestScore = r.fitness;
                index = i;
                r.selected = true;
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