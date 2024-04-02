import java.util.concurrent.CyclicBarrier;

class EvaluatorWorker implements Runnable {
    private Population p;
    private int start;
    private int end;
    private CyclicBarrier barrier;

    public EvaluatorWorker(Population p, int start, int end, CyclicBarrier barrier) {
        this.p = p;
        this.start = start;
        this.end = end;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        p.performEvaluation2(start, end);
        try {
            System.out.println("EvaluatorWorker " + Thread.currentThread().getId() + " reached the barrier");
            barrier.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
