import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class RParallel {
    private int NUM_THREADS;
    private Population p;
    private Railroad bestIndividual;
    private BlockingQueue<Railroad> bestIndividualQueue;
    private CyclicBarrier barrier;
    ConcurrentLinkedQueue<List<Railroad>> results = new ConcurrentLinkedQueue<>();


    public RParallel(int numThreads, Population population, Railroad bestIndividual, BlockingQueue<Railroad> bestIndividualQueue) {
        this.NUM_THREADS = numThreads;
        this.p = population;
        this.bestIndividual = bestIndividual;
        this.bestIndividualQueue = bestIndividualQueue;
        this.barrier = new CyclicBarrier(NUM_THREADS+1);
    }

    public void execute(){
        ExecutorService tp = Executors.newFixedThreadPool(NUM_THREADS);

        while(p.CURRENT_GENERATION<Config.NUM_GENERATIONS){
            p.resetStatistics();
            for (int i = 0; i < NUM_THREADS; i++) {
                int start = i * (p.solutions.size() / NUM_THREADS);
                int end = Math.min(p.solutions.size(), (i + 1) * (p.solutions.size() / NUM_THREADS));
                tp.submit(new EvaluatorWorker(p, start, end, barrier));
            }
            try {
                barrier.await();
                System.out.println("Main " + Thread.currentThread().getId() + " reached the barrier");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            p.printPopulationStatistics();

            List<Railroad> newP = new ArrayList<>(10);
            int index=0;

            //choose the elite
            for (int i = 0; i < Config.ELITISM_K; i++) {
                Railroad r = p.getBestSolution();
                // r.selected=false;
                newP.add(r);
                index++;
            }
            //building the population with leftover p size - elitism places
            int CAPACITY = p.POPULATION_SIZE - Config.ELITISM_K;
            System.out.println(CAPACITY+"capacity");
            results = new ConcurrentLinkedQueue<>();
            for (int i = 0; i < NUM_THREADS; i++) {
                int start = i * (CAPACITY / NUM_THREADS);
                int end = Math.min(CAPACITY, (i + 1) * (CAPACITY / NUM_THREADS));
                System.out.println("start "+start+" end "+end +" for thread i ");
                tp.submit(new PBuilderWorker(p, start, end, barrier,results));
            }

            try {
                barrier.await();
                System.out.println("Main " + Thread.currentThread().getId() + " reached the barrier2");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // collect the solutions
            for(List<Railroad> l:results){
                newP.addAll(l);
            }
            System.out.println(newP.size()+" size of newp");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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

