import java.util.*;

public class Population {
    final static int POPULATION_SIZE=500;
    public double totalFitness;
    public static List<Railroad> solutions = new ArrayList<>();
    public static int CURRENT_GENERATION=0;
    public double mutationRate;
    final static int ELITISM_K = (int) 0.1*Population.POPULATION_SIZE;
    final static int ROULETTE_WHEEL_SELECTION = 0;
    final static int TRUNCATION_SELECTION = 1;
    final static int SINGLE_POINT_CROSSOVER = 0;
    final static int OTHER_CROSSOVER = 1;
    final static int INSERTION_MUTATION = 0;
    final static int OTHER_MUTATION = 1;
    static int tsIndex = -1;

    //    final static int SIZE = 200 + ELITISM_K;  // population size
//    final static int MAX_ITER = 2000;             // max number of iterations
    final static double MUTATION_RATE = 0.07;     // probability of mutation
    final static double CROSSOVER_RATE = 0.7;     // probability of crossover
    Random r = new Random(4);

    public Population(){
        initializeSolutions();
        //System.out.println("im here"+ solutions.size());
    }

    public void initializeSolutions(){
        for (int i = 0; i < POPULATION_SIZE; i++) {
            solutions.add(new Railroad(Main.trains,i)); //train coordinates should be supplied
        }
    }

    public void printMatrix(int[][] matrix){
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                //System.out.print(matrix[i][j] + " ");
            }
          //  System.out.println();
        }
    }



    public void singlePointCrossover(Railroad r1, Railroad r2){
        int[][] c1 = new int[Main.N][Main.N];
        int[][] c2 = new int[Main.N][Main.N];
        //int crossoverPoint = r.nextInt(Main.N); // Select a random crossover point
        int crossoverPoint = Main.N/2;
        //printMatrix(r1.world);
        //printMatrix(r2.world);
        for (int i = 0; i < Main.N; i++) {
            for (int j = 0; j < crossoverPoint; j++) {
                c1[i][j] = r1.world[i][j];
                c2[i][j] = r2.world[i][j];
            }
        }

        for (int i = 0; i < Main.N; i++) {
            for (int j = crossoverPoint; j < Main.N; j++) {
                c1[i][j] = r2.world[i][j];
                c2[i][j] = r1.world[i][j];
            }
        }
        //printMatrix(c1);
        //printMatrix(c2);

        //System.out.println("CROSSING OVER individual " + r1.id + " and individual " + r2.id);
        r1.setWorld(c1);
        r2.setWorld(c2);
    }


    public void performEvaluation() {
        //evaluate all solutions
        //first evaluation can be numTrainsThatFinish/numTrains
        this.totalFitness = 0.0;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            this.totalFitness += solutions.get(i).rateFitness();
        }

        System.out.println("total fitness  "+totalFitness);
    }


    public Railroad rouletteWheelSelection(){
        //probabilistically chooses
        //the ones w greater fitness are more probable, but might also choose w lower fitness
        //keeps population diverse
        double rand = Math.random() * this.totalFitness; //rnd nr between 0 and total fitness of the population
        int index =  -1;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            rand -= solutions.get(i).getFitness();
            if(rand<0){
                index = i;
                break;
            }
        }
        // index = r.nextInt(POPULATION_SIZE);

        //System.out.println("Selected individual at index "+index);
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
        double bestScore = -100;
        int index = 0;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            Railroad r = solutions.get(i);
            if(r.fitness>bestScore&&!r.selected){
                bestScore = r.fitness;
                index = i;
            }
        }
        Railroad selected = solutions.get(index);
        selected.selected=true;
        //System.out.println("best at index "+index+" with fitness "+bestScore);
        return selected;
    }
    public void setSolutions(List<Railroad> solutions) {
        this.solutions = solutions;
    }
//    public Railroad getNewIndividual(){
//        return new Railroad(Main.trains);
//    }
}