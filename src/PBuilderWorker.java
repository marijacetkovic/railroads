import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;

public class PBuilderWorker implements Runnable {
    private Population p;
    private int start;
    private int end;
    private List<Railroad> workerP;
    private CyclicBarrier barrier;
    ConcurrentLinkedQueue<List<Railroad>> results;

    public PBuilderWorker(Population p, int start, int end, CyclicBarrier barrier,ConcurrentLinkedQueue<List<Railroad>> results) {
        this.p = p;
        this.start = start;
        this.end = end;
        this.workerP = new ArrayList<>(1);
        this.barrier = barrier;
        this.results = results;
    }

    @Override
    public void run() {
        // Process each solution in the chunk
        while(start<end){
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
            //add to local pop
            workerP.add(r1);
            //System.out.println(index);
            workerP.add(r2);
            start+=2;
            //System.out.println(index);
        }
        try {
            results.add(workerP);
            //updateResults(workerP);
            System.out.println("PBuilderWorker " + Thread.currentThread().getId() + " reached the barrier");
            barrier.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
//    public synchronized void updateResults(List<Railroad> r){
//        results.add(r);
//    }

    public List<Railroad> getIntermediateList() {
        return workerP;
    }
}
