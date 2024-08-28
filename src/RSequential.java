import util.Config;
import util.RChart;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class RSequential {

    private Population p;
    private Railroad bestIndividual;

    private BlockingQueue<Railroad> bestIndividualQueue;


    public RSequential(Population population, Railroad bestIndividual, BlockingQueue<Railroad> bestIndividualQueue) {
        this.p = population;
        this.bestIndividual = bestIndividual;
        this.bestIndividualQueue = bestIndividualQueue;
    }
    public void execute(){
        double startTime = System.currentTimeMillis(),endTime=0;
        while(p.getCurrentGeneration()< Config.NUM_GENERATIONS){
            p.performEvaluation();
           // p.performEvaluationWithPricing();
            List<Railroad> newP = new ArrayList<>(10);
            int index=0;
            //choose the elite
            for (int i = 0; i < Config.ELITISM_K; i++) {
                Railroad r = p.getBestSolutions();
                // r.selected=false;
                newP.add(r);
                index++;
            }
            p.adjustMutationRate();
            System.out.println("Mutation rate "+Config.MUTATION_RATE);
            p.buildPopulation(index, p.getSolutions().size(),newP);
            p.setSolutions(newP);
            Population.increaseCurrentGeneration();
            bestIndividual = p.getBestIndividual(); //solution to represent per generation
            bestIndividual.generation = Population.getCurrentGeneration();
            bestIndividualQueue.add(bestIndividual);
            p.updateBestSolution(bestIndividual);
            System.out.println("Best sol id "+bestIndividual.id+" with fitness "+bestIndividual.fitness+ " and numTrainsFinish " +
                    " "+ bestIndividual.numTrains+"and tile price"+bestIndividual.tilePricing +" for generation "+Population.getCurrentGeneration() );
            //System.out.println("current gen "+p.CURRENT_GENERATION);
            //printMatrix(bestIndividual.world);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Time taken to perform the algorithm is "+(endTime-startTime));
        RChart.saveChart(p.getPData());
    }
    public void printMatrix(int[][] matrix) {

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}

