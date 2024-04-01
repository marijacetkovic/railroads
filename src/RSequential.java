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
                Railroad r = p.getBestSolution();
                // r.selected=false;
                newP.add(r);
                index++;
            }
            // p.sortPopulation();
            while(index<p.solutions.size()){
                Railroad r1 = p.select(Config.ROULETTE_WHEEL_SELECTION);
                Railroad r2 = p.select(Config.ROULETTE_WHEEL_SELECTION);
                //crossover
                if(Math.random()<Config.CROSSOVER_RATE){
                    p.crossover(Config.SINGLE_POINT_CROSSOVER,r1,r2);
                }
                //mutate
                if(Math.random()<Config.MUTATION_RATE){
                    p.mutate(Config.INSERTION_MUTATION,r1);
                }
                if(Math.random()<Config.MUTATION_RATE){
                    p.mutate(Config.INSERTION_MUTATION,r2);
                }
                //add to new pop
                newP.add(r1);
                //System.out.println(index);
                newP.add(r2);
                index+=2;
                //System.out.println(index);
            }
            p.setSolutions(newP);
            bestIndividual = p.getBestSolution(); //solution to represent per generation
            bestIndividualQueue.offer(bestIndividual);
            System.out.println("best solution id "+bestIndividual.id+" with fitness "+bestIndividual.fitness+ " and generation "+p.CURRENT_GENERATION );

            Population.CURRENT_GENERATION++;
            //System.out.println("current gen "+p.CURRENT_GENERATION);

        }
    }

}

