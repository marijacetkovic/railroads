import util.Config;
import util.WorkSplitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class RParallel {

    private final int NUM_THREADS;
    private final Population population;
    private Railroad bestIndividual;
    private final BlockingQueue<Railroad> bestIndividualQueue;
    private static CyclicBarrier barrier = null;
    private final ExecutorService threadPool;
    private final WorkSplitter workSplitter;
    private ConcurrentLinkedQueue<List<Railroad>> results;

    public RParallel(int numThreads, Population population, Railroad bestIndividual, BlockingQueue<Railroad> bestIndividualQueue) {
        this.NUM_THREADS = numThreads;
        this.population = population;
        this.bestIndividual = bestIndividual;
        this.bestIndividualQueue = bestIndividualQueue;
        this.barrier = new CyclicBarrier(NUM_THREADS + 1);
        this.threadPool = Executors.newFixedThreadPool(NUM_THREADS);
        this.workSplitter = new WorkSplitter(population.getSolutions().size(), NUM_THREADS);
    }

    public void execute() {
        long startTime = System.currentTimeMillis();
        population.initializeSolutions();
        while (Population.getCurrentGeneration() < Config.NUM_GENERATIONS) {
            runGeneration();
            Population.increaseCurrentGeneration();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken to perform the parallel run: " + (endTime - startTime) + " ms");
    }

    private void runGeneration() {
        population.resetStatistics();
        evaluateInParallel();
        GA.adjustMutationRate(population);
        population.updateAllStatistics();
        population.printPopulationStatistics();
        List<Railroad> newPopulation = GA.selectElite(population);
        buildInParallel(newPopulation);
        GA.updateBestIndividual(population, bestIndividualQueue);
    }

    private void evaluateInParallel() {
        workSplitter.setSize(population.getSolutions().size());
        for (int i = 0; i < NUM_THREADS; i++) {
            int start = workSplitter.getStart(i);
            int end = workSplitter.getEnd(i);
            threadPool.submit(new PEvaluatorWorker(population, start, end));
        }
        awaitBarrier();
    }


    private void buildInParallel(List<Railroad> newPopulation) {
        int remainingCapacity = population.getPSize() - Config.ELITISM_K;
        workSplitter.setSize(remainingCapacity);
        results = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < NUM_THREADS; i++) {
            int start = workSplitter.getStart(i);
            int end = workSplitter.getEnd(i);
            threadPool.submit(new PBuilderWorker(population, start, end, results));
        }
        awaitBarrier();
        collectResults(newPopulation, remainingCapacity);
        population.setSolutions(newPopulation);
    }

    private void collectResults(List<Railroad> newPopulation, int remainingCapacity) {
        for (List<Railroad> list : results) {
            int sizeToAdd = Math.min(remainingCapacity, list.size());
            newPopulation.addAll(list.subList(0, sizeToAdd));
            remainingCapacity -= sizeToAdd;
            if (remainingCapacity <= 0) {
                break;
            }
        }
    }

    public static void awaitBarrier() {
        try {
           // System.out.println(Thread.currentThread().getName() + " waiting at barrier.");
            barrier.await();
            //System.out.println(Thread.currentThread().getName() + " passed the barrier.");
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
