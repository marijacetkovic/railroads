import util.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * The {@code Population} class manages a collection of {@link Railroad} instances,
 * representing potential solutions in a genetic algorithm.
 */

public class Population {
    private static List<Railroad> solutions;
    private static int currentGeneration;
    private final int pSize;
    private double totalFitness;
    private double maxFitness;
    private double avgFitness;
    private static int tsIndex = -1;
    private PriorityQueue<Railroad> eQueue;
    public static List<double[]> pData = new ArrayList<>();
    private Railroad trivialSol;
    private double previousMaxFitness;
    private int genWithoutImprovement;
    private int prevNumTrains = 0;
    private int maxNumTrains = 0;
    private Railroad bestSolutionFound;

    public Population() {
        pSize = Config.POPULATION_SIZE;
        solutions = new ArrayList<>();
        currentGeneration = 1;
    }

    public static void increaseCurrentGeneration() {
        currentGeneration++;
    }

    public List<Railroad> getSolutions() {
        return solutions;
    }

    public int getPSize() {
        return pSize;
    }

    public double getTotalFitness() {
        return totalFitness;
    }

    public void setTotalFitness(double totalFitness) {
        this.totalFitness = totalFitness;
    }

    public double getMaxFitness() {
        return maxFitness;
    }

    public void setMaxFitness(double maxFitness) {
        this.maxFitness = maxFitness;
    }

    public double getAvgFitness() {
        return avgFitness;
    }

    public void setAvgFitness(double avgFitness) {
        this.avgFitness = avgFitness;
    }

    public static int getCurrentGeneration() {
        return currentGeneration;
    }

    public static List<double[]> getPData() {
        return pData;
    }

    public void initializeSolutions() {
        for (int i = 0; i < pSize; i++) {
            solutions.add(new Railroad(Main.trains, i)); //train coordinates should be supplied
        }
    }

    public void initializeSolutionsD() {
        System.out.println("pSize"+pSize);
        for (int i = 0; i < pSize; i++) {

            solutions.add(new Railroad(RDistributed.trains, i)); //train coordinates should be supplied
        }
        System.out.println("turci sadhasd"+ solutions.size());
    }

    public void performEvaluation() {
        //evaluate all solutions
        this.totalFitness = 0.0;
        this.maxFitness = Double.MIN_VALUE;
        this.avgFitness = 0;
        this.maxNumTrains = Integer.MIN_VALUE;
        for (int i = 0; i < pSize; i++) {
            //double f = solutions.get(i).rateFitness();
            Railroad r = solutions.get(i);
            double f = r.rateFitness();
            this.totalFitness += f;
            this.maxFitness = Math.max(maxFitness, f);
            this.maxNumTrains = Math.max(maxNumTrains, r.getNumTrains());
        }
        this.avgFitness = totalFitness / pSize;
        printPopulationStatistics();
        savePopulationStatistics();
    }

    public void updateBestSolution(Railroad r) {
        if (bestSolutionFound == null || r.fitness > bestSolutionFound.fitness) {
            bestSolutionFound = r;
        }
    }

    private void savePopulationStatistics() {
        pData.add(new double[]{maxFitness, avgFitness});
    }

    public void performEvaluationWithPricing() {
        //evaluate all solutions
        this.totalFitness = 0.0;
        this.maxFitness = Double.MIN_VALUE;
        this.avgFitness = 0;
        this.maxNumTrains = Integer.MIN_VALUE;
        for (int i = 0; i < pSize; i++) {
            Railroad r = solutions.get(i);
            double f = r.rateFitnessWithPricing();
            this.totalFitness += f;
            this.maxFitness = Math.max(maxFitness, f);
            this.maxNumTrains = Math.max(maxNumTrains, r.getNumTrains());
        }
        this.avgFitness = totalFitness / pSize;
        printPopulationStatistics();
        savePopulationStatistics();
    }

    public void performEvaluation2(int start, int end) {
        //System.out.println("Thread " + Thread.currentThread().getName() + " evaluating solutions from " + start + " to " + (end));
        //evaluate all solutions
        for (int i = start; i < end; i++) {
            double f = solutions.get(i).rateFitnessWithPricing();
           // double f = solutions.get(i).rateFitness();

        }
    }

    public List<Railroad> performEvaluationD(int start, int end, int rank) {
        System.out.println("Thread " + Thread.currentThread() + " evaluating solutions from " + start + " to " + (end - 1));
        List<Railroad> mySolutions = new ArrayList<>();
        System.out.println("sol size "+solutions.size());
        //evaluate all solutions
        for (int i = start; i < end; i++) {
            if (!solutions.isEmpty()&&solutions.get(i)!=null) {
                Railroad r = solutions.get(i);
                r.rateFitness();
                mySolutions.add(r);
            }
        }
        return mySolutions;
    }

    public synchronized void updateStatistics(double f) {
        this.totalFitness += f;
        //System.out.println(this.totalFitness);
        this.maxFitness = Math.max(maxFitness, f);
    }

    public void updateAllStatistics() {
        for (Railroad r : solutions) {
            if (r != null) {
                this.totalFitness += r.fitness;
                this.maxFitness = Math.max(maxFitness, r.fitness);
            }
        }
    }
    //collect statistics

    public void resetStatistics() {
        this.totalFitness = 0.0;
        this.maxFitness = Double.MIN_VALUE;
        this.avgFitness = 0;
    }

    public void printPopulationStatistics() {
        this.avgFitness = totalFitness / pSize;
        System.out.println("Total fitness: " + totalFitness + "   |   Max fitness: " + maxFitness + "   |   Avg fitness: " + avgFitness);
    }

    public Railroad rouletteWheelSelection() {
        //probabilistically chooses
        //the ones w greater fitness are more probable, but might also choose w lower fitness
        //keeps population diverse
        double rand = Math.random() * this.totalFitness; //rnd nr between 0 and total fitness of the population
        int index = 0;
        for (int i = 0; i < pSize; i++) {
            if(solutions.get(i)!=null) {
                rand -= solutions.get(i).getFitness();
                if (rand < 0) {
                    index = i;
                    break;
                }
            }
        }
        Railroad selected = solutions.get(index);
        Railroad r = new Railroad(selected.trains, -1);
        r.setWorld(selected.world);
        return r;
    }

    public Railroad testSelection() {
        // Scale up fitness values to avoid numerical precision issues

        double lowerBound = 0.5 * maxFitness;

        // Generate a random number between 0 and 1
        Railroad r = null;

        while (r == null) {
            int rand = (int) (Math.random() * pSize);
            r = solutions.get(rand);
            if (r.getFitness() >= lowerBound) {
                return r;
            }
            r = null;
        }
        throw new RuntimeException("Roulette wheel selection failed: no individual selected.");
    }

    public Railroad truncationSelection() {
        //chooses the ones with greater fitness first
        //assumes the solutions are sorted
        //might not converge well
        if (tsIndex > 0) {
            tsIndex--;
        }
        return solutions.get(tsIndex);
    }

    public Railroad select(int selectionType) {
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


    public List<Railroad> buildPopulation(int start, int end, List<Railroad> newP) {
        while (start < end) {

            Railroad r1 = this.select(Config.ROULETTE_WHEEL_SELECTION);
            Railroad r2 = this.select(Config.ROULETTE_WHEEL_SELECTION);

            if (Math.random() < Config.CROSSOVER_RATE) {
                GA.crossover(Config.SINGLE_POINT_CROSSOVER, r1, r2);
            }
            GA.mutate(Config.INSERTION_MUTATION, r1);
            GA.mutate(Config.INSERTION_MUTATION, r2);

            r1.id=start;
            start++;
            r2.id=start;
            newP.add(r1);
            newP.add(r2);
            start++;
        }
        // System.out.println("Thread " + Thread.currentThread() + " built solutions of size "+ newP.size());
        return newP;
    }

    public boolean checkStagnation() {
        if (maxNumTrains == prevNumTrains) {
            genWithoutImprovement++;
        } else {
            genWithoutImprovement = 0;
            prevNumTrains = maxNumTrains;
        }
        return genWithoutImprovement > Config.STAGNATION_BOUND;
    }

    //returns a railroad with best fitness

    public Railroad getBestSolutions() {
        double bestScore = -100;
        int index = 0;
        for (int i = 0; i < pSize; i++) {
            Railroad r = solutions.get(i);
            // System.out.println("koj go prai problem dali sum jas toj "+i);
            if (r != null && r.fitness > bestScore && !r.selected) {
                bestScore = r.fitness;
                index = i;
            }
        }
        Railroad selected = solutions.get(index);
        selected.selected = true;
        //System.out.println("best at index "+index+" with fitness "+bestScore);
        return selected;
    }

    public Railroad getBestIndividual() {
        double bestScore = -100;
        int index = 0;
        for (int i = 0; i < pSize; i++) {
            Railroad r = solutions.get(i);
            if (r != null && r.fitness > bestScore) {
                bestScore = r.fitness;
                index = i;
            }
        }
        Railroad r = solutions.get(index);
        return r;
    }

    public void setSolutions(List<Railroad> solutions) {
        this.solutions = solutions;
    }

}