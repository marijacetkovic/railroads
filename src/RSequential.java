import util.Config;
import util.RChart;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class RSequential {

    private Population population;
    private Railroad bestIndividual;
    private final BlockingQueue<Railroad> bestIndividualQueue;

    public RSequential(Population population, Railroad bestIndividual, BlockingQueue<Railroad> bestIndividualQueue) {
        this.population = population;
        this.bestIndividual = bestIndividual;
        this.bestIndividualQueue = bestIndividualQueue;
    }

    public void execute() {
        long startTime = System.currentTimeMillis();
        population.initializeSolutions();
        while (population.getCurrentGeneration() < Config.NUM_GENERATIONS) {
            GA.performEvaluation(population);
            List<Railroad> newPopulation = GA.selectElite(population);
           // population.adjustMutationRate();
            System.out.println("Mutation rate: " + Config.MUTATION_RATE);
            GA.buildAndSetNewPopulation(population,newPopulation);
            GA.updateBestIndividual(population,bestIndividualQueue);
            Population.increaseCurrentGeneration();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken to perform the algorithm: " + (endTime - startTime) + " ms");
        RChart.saveChart(population.getPData());
    }
}
