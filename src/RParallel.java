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
    ExecutorService tp;


    public RParallel(int numThreads, Population population, Railroad bestIndividual, BlockingQueue<Railroad> bestIndividualQueue) {
        this.NUM_THREADS = numThreads;
        this.p = population;
        this.bestIndividual = bestIndividual;
        this.bestIndividualQueue = bestIndividualQueue;
        this.barrier = new CyclicBarrier(NUM_THREADS+1);
        this.tp = Executors.newFixedThreadPool(NUM_THREADS);
    }

    public void execute(){

        while(Population.getCurrentGeneration()<Config.NUM_GENERATIONS){
            p.resetStatistics();
            int chunk = (int) Math.ceil((double) p.getSolutions().size()/NUM_THREADS);
            for (int i = 0; i < NUM_THREADS; i++) {
                int start = i * chunk;
                int end = Math.min(p.getSolutions().size(), (i + 1) * chunk);
                System.out.println("thread +"+i+"start "+start +" end "+end);
                tp.submit(new PEvaluatorWorker(p, start, end, barrier));
            }
            try {
                barrier.await();
                System.out.println("Main " + Thread.currentThread().getId() + " reached the barrier");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            p.updateAllStatistics();
            p.printPopulationStatistics();

            List<Railroad> newP = new ArrayList<>(10);
            int offset=0;

            //choose the elite
            for (int i = 0; i < Config.ELITISM_K; i++) {
                Railroad r = p.getBestSolutions();
                r.id=offset;
                // r.selected=false;
                newP.add(r);
                offset++;
            }
            //building the population with leftover p size - elitism places
            int CAPACITY = p.getPSize() - Config.ELITISM_K;
            chunk = (int) Math.ceil((double) CAPACITY/NUM_THREADS);
            System.out.println(CAPACITY+"capacity");
            results = new ConcurrentLinkedQueue<>();
            for (int i = 0; i < NUM_THREADS; i++) {
                int start = i * chunk;
                int end = Math.min(p.getSolutions().size(), (i + 1) * chunk);
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
                int sizeToAdd = Math.min(CAPACITY, l.size());
                newP.addAll(l.subList(0, sizeToAdd));
                CAPACITY -= sizeToAdd;
                if (CAPACITY <= 0) {
                    break;
                }
            }
            System.out.println(newP.size()+" size of newp");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            p.setSolutions(newP);

            bestIndividual = p.getBestIndividual(); //solution to represent per generation
            bestIndividualQueue.offer(bestIndividual);
            System.out.println("best solution id "+bestIndividual.id+" with fitness "+bestIndividual.fitness+ " and generation "+Population.getCurrentGeneration() );

            Population.increaseCurrentGeneration();
            //System.out.println("current gen "+p.CURRENT_GENERATION);

        }
    }

}

