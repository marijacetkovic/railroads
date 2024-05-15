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

    public PBuilderWorker(Population p, int start, int end, CyclicBarrier barrier, ConcurrentLinkedQueue<List<Railroad>> results) {
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
        p.buildPopulation(start,end,workerP);
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
