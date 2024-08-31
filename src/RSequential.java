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
        Main.renderGui(Main.trains,bestIndividualQueue);
        while (population.getCurrentGeneration() < Config.NUM_GENERATIONS) {
            runGeneration();
            Population.increaseCurrentGeneration();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken to perform the algorithm: " + (endTime - startTime) + " ms");
    }

    public void runGeneration() {
        population.resetStatistics();
        GA.performEvaluation(population);
        GA.adjustMutationRate(population);
        GA.updatePopulationData(population);
        List<Railroad> newPopulation = GA.selectElite(population);
        GA.buildAndSetNewPopulation(population, newPopulation);
        GA.updateBestIndividual(population, bestIndividualQueue);
        //RChart.saveChart(population.getPData());
    }
}
