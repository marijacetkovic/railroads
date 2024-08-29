import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PBuilderWorker implements Runnable {
    private Population p;
    private int start;
    private int end;
    private List<Railroad> workerP;
    ConcurrentLinkedQueue<List<Railroad>> results;

    public PBuilderWorker(Population p, int start, int end, ConcurrentLinkedQueue<List<Railroad>> results) {
        this.p = p;
        this.start = start;
        this.end = end;
        this.workerP = new ArrayList<>(1);
        this.results = results;
    }

    @Override
    public void run() {
        // Process each solution in the chunk
        p.buildPopulation(start,end,workerP);
        results.add(workerP);
        //System.out.println(Thread.currentThread().getName()+ " has built size "+workerP.size());
        RParallel.awaitBarrier();
    }

}
