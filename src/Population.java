import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Population {
    public double totalFitness;
    public double maxFitness;
    public double avgFitness;
    public static List<Railroad> solutions = new ArrayList<>();
    public static int CURRENT_GENERATION=1;
    public int POPULATION_SIZE = Config.POPULATION_SIZE;

    static int tsIndex = -1;

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
        int[][] m1 = new int[Config.WORLD_SIZE][Config.WORLD_SIZE];
        int[][] m2 = new int[Config.WORLD_SIZE][Config.WORLD_SIZE];
       int crossoverPoint = r.nextInt(Config.WORLD_SIZE); // select a random crossover point
       // int crossoverPoint = Config.WORLD_SIZE/2;
        //printMatrix(r1.world);
        //printMatrix(r2.world);
        for (int i = 0; i <Config.WORLD_SIZE; i++) {
            for (int j = 0; j < Config.WORLD_SIZE; j++) {
                if (j < crossoverPoint) {
                    m1[i][j] = r1.world[i][j];
                    m2[i][j] = r2.world[i][j];
                } else {
                    m1[i][j] = r2.world[i][j];
                    m2[i][j] = r1.world[i][j];
                }
            }
        }
        //printMatrix(c1);
        //printMatrix(c2);

        //System.out.println("CROSSING OVER individual " + r1.id + " and individual " + r2.id);
        r1.setWorld(m1);
        r2.setWorld(m2);
    }

    public void performEvaluation() {
        //evaluate all solutions
        this.totalFitness = 0.0;
        this.maxFitness = Double.MIN_VALUE;
        this.avgFitness = 0;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            //double f = solutions.get(i).rateFitness();
            double f = solutions.get(i).rateFitness();
            this.totalFitness += f;
            this.maxFitness = Math.max(maxFitness, f);
        }
        this.avgFitness = totalFitness/POPULATION_SIZE;
        printPopulationStatistics();
    }
    public void performEvaluationM() {
        //evaluate all solutions
        this.totalFitness = 0.0;
        this.maxFitness = Double.MAX_VALUE;
        this.avgFitness = 0;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            //double f = solutions.get(i).rateFitness();
            double f = solutions.get(i).rateFitnessWithPricing();
            this.totalFitness += f;
            this.maxFitness = Math.min(maxFitness, f);
        }
        this.avgFitness = totalFitness/POPULATION_SIZE;
        printPopulationStatistics();
    }

    public void performEvaluation2(int start, int end) {
        System.out.println("Thread " + Thread.currentThread().getName() + " evaluating solutions from " + start + " to " + (end - 1));
        //evaluate all solutions
        for (int i = start; i < end; i++) {
            double f = solutions.get(i).rateFitness();
            //updateStatistics(f);
        }
    }

    public List<Railroad> performEvaluationD(int start, int end, int rank) {
        //System.out.println("Thread " + Thread.currentThread() + " evaluating solutions from " + start + " to " + (end - 1));
        List<Railroad> mySolutions = new ArrayList<>();
        //evaluate all solutions
        for (int i = start; i < end; i++) {
            Railroad r = solutions.get(i);
            r.rateFitness();
            mySolutions.add(r);
            //System.out.println("i am process "+rank+"amd i evaluated index "+i);
        }
        return mySolutions;
    }

    public synchronized void updateStatistics(double f){
        this.totalFitness += f;
        //System.out.println(this.totalFitness);
        this.maxFitness = Math.max(maxFitness, f);
    }
    public void updateAllStatistics(){
        for ( Railroad r: solutions) {
            this.totalFitness += r.fitness;
            this.maxFitness = Math.max(maxFitness, r.fitness);
        }
    }
    //collect statistics?????

    public void resetStatistics(){
        this.totalFitness = 0.0;
        this.maxFitness = Double.MIN_VALUE;
        this.avgFitness = 0;
    }

    public void printPopulationStatistics(){
        this.avgFitness = totalFitness/POPULATION_SIZE;
        System.out.println("Total fitness: "+totalFitness+"   |   Max fitness: "+maxFitness+"   |   Avg fitness: "+avgFitness);
    }

    public Railroad rouletteWheelSelection(){
        //probabilistically chooses
        //the ones w greater fitness are more probable, but might also choose w lower fitness
        //keeps population diverse
        double rand = Math.random() * this.totalFitness; //rnd nr between 0 and total fitness of the population
        int index =  0;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            rand -= solutions.get(i).getFitness();
            if(rand<0){
                index = i;
                break;
            }
        }
        // index = r.nextInt(POPULATION_SIZE);

        //System.out.println("Selected individual at index "+index);
        Railroad selected = solutions.get(index);
        Railroad r = new Railroad(selected.trains,-1);
        r.setWorld(selected.world);
        return r;
    }
    public Railroad testSelection() {
        // Scale up fitness values to avoid numerical precision issues

        double lowerBound = 0.5 * maxFitness;

        // Generate a random number between 0 and 1
        Railroad r = null;

        while (r==null) {
            int rand = (int) (Math.random() * POPULATION_SIZE);
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

//    public void sortPopulation(){
//        //used for truncation selection
//        tsIndex = this.solutions.size(); //reset counter
//        Collections.sort(this.solutions); //sort
//    }

    public List<Railroad> buildPopulation(int start, int end, List<Railroad> newP){
        while(start<end){
            Railroad r1 = this.select(Config.ROULETTE_WHEEL_SELECTION);
            Railroad r2 = this.select(Config.ROULETTE_WHEEL_SELECTION);
            //crossover
            if(Math.random()<Config.CROSSOVER_RATE){
                this.crossover(Config.SINGLE_POINT_CROSSOVER,r1,r2);
            }
            this.mutate(Config.INSERTION_MUTATION,r1);
            this.mutate(Config.INSERTION_MUTATION,r2);

            //add to new pop
            //if(p.CURRENT_GENERATION == 10){
//                    newP.add(Main.generateTrivialSol());
            r1.id=start;
            start++;
            r2.id=start;
            //add to local pop
            newP.add(r1);
            //System.out.println(index);
            newP.add(r2);
            start++;
            //System.out.println(index);
        }
        System.out.println("Thread " + Thread.currentThread() + " built solutions of size "+ newP.size());

        return newP;
    }
    //returns a railroad with best fitness
    public Railroad getBestSolutions(){
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

    public Railroad getBestIndividual(){
        double bestScore = -100;
        int index = 0;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            Railroad r = solutions.get(i);
            if(r.fitness>bestScore){
                bestScore = r.fitness;
                index = i;
            }
        }
        //System.out.println("best at index "+index+" with fitness "+bestScore);
        Railroad r = solutions.get(index);
        return r;
    }
    public void setSolutions(List<Railroad> solutions) {
        this.solutions = solutions;
    }

}