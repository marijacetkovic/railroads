import java.util.*;

public class Population {
    public double totalFitness;
    public double maxFitness;
    public static List<Railroad> solutions = new ArrayList<>();
    public static int CURRENT_GENERATION=0;

    static int tsIndex = -1;

    Random r = new Random(4);

    public Population(){
        initializeSolutions();
        //System.out.println("im here"+ solutions.size());
    }

    public void initializeSolutions(){
        for (int i = 0; i < Config.POPULATION_SIZE; i++) {
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
        int[][] c1 = new int[Config.WORLD_SIZE][Config.WORLD_SIZE];
        int[][] c2 = new int[Config.WORLD_SIZE][Config.WORLD_SIZE];
       // int crossoverPoint = r.nextInt(.N); // select a random crossover point
        int crossoverPoint = Config.WORLD_SIZE/2;
        //printMatrix(r1.world);
        //printMatrix(r2.world);
        for (int i = 0; i <Config.WORLD_SIZE; i++) {
            for (int j = 0; j < Config.WORLD_SIZE; j++) {
                if (j < crossoverPoint) {
                    c1[i][j] = r1.world[i][j];
                    c2[i][j] = r2.world[i][j];
                } else {
                    c1[i][j] = r2.world[i][j];
                    c2[i][j] = r1.world[i][j];
                }
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
        this.maxFitness = Double.MIN_VALUE;
        for (int i = 0; i < Config.POPULATION_SIZE; i++) {
            double f = solutions.get(i).rateFitness();
            this.totalFitness += f;
            this.maxFitness = Math.max(maxFitness, f);
        }

        System.out.println("total fitness  "+totalFitness+" max fitness  "+maxFitness);
    }

    public Railroad rouletteWheelSelection(){
        //probabilistically chooses
        //the ones w greater fitness are more probable, but might also choose w lower fitness
        //keeps population diverse
        double rand = Math.random() * this.totalFitness; //rnd nr between 0 and total fitness of the population
        int index =  -1;
        for (int i = 0; i < Config.POPULATION_SIZE; i++) {
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
    public Railroad testSelection() {
        // Scale up fitness values to avoid numerical precision issues

        double lowerBound = 0.5 * maxFitness;

        // Generate a random number between 0 and 1
        Railroad r = null;

        while (r==null) {
            int rand = (int) (Math.random() * Config.POPULATION_SIZE);
            r = solutions.get(rand);
            if (r.getFitness() >= lowerBound) {
                return r;
            }
            r = null;
        }
        // This should never happen if fitness values are properly scaled and normalized
        throw new RuntimeException("Roulette wheel selection failed: no individual selected.");
    }

    public Railroad truncationSelection(){
        //chooses the ones with greater fitness first
        //assumes the solutions are sorted
        //might not converge well
        if(tsIndex>0){tsIndex--;}
        return solutions.get(tsIndex);
    }

    public Railroad select(int selectionType){
        Railroad r;
        switch (selectionType) {
            case Config.ROULETTE_WHEEL_SELECTION:
                r = rouletteWheelSelection();
                break;
            case Config.TEST_SELECTION:
                r = testSelection();
                break;
            case Config.TRUNCATION_SELECTION:
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
            case Config.SINGLE_POINT_CROSSOVER:
                singlePointCrossover(r1,r2);
                break;
            case Config.OTHER_CROSSOVER:
                break;
            default:
                System.out.println("invalid crossover type");
        }
    }

    public void mutate(int mutationType, Railroad r){
        switch (mutationType) {
            case Config.INSERTION_MUTATION:
                r.insertionMutation();
                break;
            case Config.OTHER_MUTATION:
                break;
            default:
                System.out.println("invalid mutation type");
        }
    }

    public void sortPopulation(){
        //used for truncation selection
        tsIndex = this.solutions.size(); //reset counter
        Collections.sort(this.solutions); //sort
    }

    //returns a railroad with best fitness
    public Railroad getBestSolution(){
        double bestScore = -100;
        int index = 0;
        for (int i = 0; i < Config.POPULATION_SIZE; i++) {
            Railroad r = solutions.get(i);
            if(r.fitness>bestScore&&!r.selected){
                bestScore = r.fitness;
                index = i;
            }
        }
        Railroad selected = solutions.get(index);
        selected.selected=true;
        System.out.println("best at index "+index+" with fitness "+bestScore);
        return selected;
    }
    public void setSolutions(List<Railroad> solutions) {
        this.solutions = solutions;
    }
//    public Railroad getNewIndividual(){
//        return new Railroad(.trains);
//    }
}