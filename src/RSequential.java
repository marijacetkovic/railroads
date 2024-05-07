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
        while(p.CURRENT_GENERATION<Config.NUM_GENERATIONS){
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
            p.buildPopulation(index,p.solutions.size(),newP);
            p.setSolutions(newP);
            bestIndividual = p.getBestIndividual(); //solution to represent per generation
            bestIndividualQueue.offer(bestIndividual);
            System.out.println("best solution id "+bestIndividual.id+" with fitness "+bestIndividual.fitness+ " and numttrains "+ bestIndividual.numTrains+"and generation "+p.CURRENT_GENERATION );
            Population.CURRENT_GENERATION++;
            //System.out.println("current gen "+p.CURRENT_GENERATION);

        }
    }

}

