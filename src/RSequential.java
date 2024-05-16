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
        while(p.getCurrentGeneration()< Config.NUM_GENERATIONS){
            p.performEvaluation();
            List<Railroad> newP = new ArrayList<>(10);
            int index=0;
            //choose the elite
            for (int i = 0; i < Config.ELITISM_K; i++) {
                Railroad r = p.getBestSolutions();
                // r.selected=false;
                newP.add(r);
                index++;
            }
           // p.adjustMutationRate();
            System.out.println("Mutation rate "+Config.MUTATION_RATE);
            p.buildPopulation(index, p.getSolutions().size(),newP);
            p.setSolutions(newP);
            Population.increaseCurrentGeneration();
            bestIndividual = p.getBestIndividual(); //solution to represent per generation
            bestIndividual.generation = Population.getCurrentGeneration();
            bestIndividualQueue.offer(bestIndividual);
            System.out.println("Best sol id "+bestIndividual.id+" with fitness "+bestIndividual.fitness+ " and numTrainsFinish "+ bestIndividual.numTrains+" for generation "+Population.getCurrentGeneration() );
            //System.out.println("current gen "+p.CURRENT_GENERATION);

        }
        RChart.saveChart(p.getPData());
    }

}

