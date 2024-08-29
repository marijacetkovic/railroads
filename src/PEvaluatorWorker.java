public class PEvaluatorWorker implements Runnable {
    private Population p;
    private int start;
    private int end;

    public PEvaluatorWorker(Population p, int start, int end) {
        this.p = p;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        p.performEvaluation2(start, end);
        RParallel.awaitBarrier();
    }
}
