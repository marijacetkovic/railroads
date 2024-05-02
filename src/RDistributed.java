import mpi.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class RDistributed {
    private int NUM_THREADS;
    private Population p;
    private Railroad bestIndividual;
    public RDistributed(int numThreads, Population population, Railroad bestIndividual, BlockingQueue<Railroad> bestIndividualQueue) {
        this.NUM_THREADS = numThreads;
        this.p = population;
        this.bestIndividual = bestIndividual;
        this.bestIndividualQueue = bestIndividualQueue;
        this.barrier = new CyclicBarrier(NUM_THREADS+1);
    }

    public void execute(String[] args){
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        while(p.CURRENT_GENERATION<Config.NUM_GENERATIONS){
            p.resetStatistics();
            //get its own chunk of population
            int start = rank * (p.solutions.size() / size);
            int end = Math.min(p.solutions.size(), (rank + 1) * (p.solutions.size() / size));

            //perform eval on chunk of population
            List<Railroad> mySolutions = p.performEvaluationD(start,end);

            //gather the solutions
            List<List<Railroad>> allSolutions = gatherSolutions(mySolutions, rank, size);
            MPI.COMM_WORLD.Bcast(allSolutions.toArray(), 0, allSolutions.size(), MPI.OBJECT, 0);

            List<Railroad> evaluatedSolutions = combineSolutions(allSolutions);
            //update local p
            p.setSolutions(evaluatedSolutions);
            System.out.println("Process with rank "+rank+" reached the barrier");
            MPI.COMM_WORLD.Barrier();

            if (rank == 0){ // just root process printing stats
                p.printPopulationStatistics();
            }

            List<Railroad> newP = new ArrayList<>(10);
            int index=0;

            //choose the elite
            for (int i = 0; i < Config.ELITISM_K; i++) {
                Railroad r = p.getBestSolutions();
                // r.selected=false;
                newP.add(r);
                index++;
            }

            //building the population with leftover p size - elitism places
            int CAPACITY = p.POPULATION_SIZE - Config.ELITISM_K;
            System.out.println(CAPACITY+"capacity");
            results = new ConcurrentLinkedQueue<>();

            //get chunk of population which process x will be building
            start = rank * (CAPACITY / size);
            end = Math.min(CAPACITY, (rank + 1) * (CAPACITY / size));
            System.out.println("start "+start+" end "+end +" for process "+rank);
            List<Railroad> myChunk = buildPopulation(start,end,evaluatedSolutions);
           // tp.submit(new PBuilderWorker(p, start, end, barrier,results));


            MPI.COMM_WORLD.Barrier();

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

            bestIndividual = p.getBestIndividual(); //solution to represent per generation
            bestIndividualQueue.offer(bestIndividual);
            System.out.println("best solution id "+bestIndividual.id+" with fitness "+bestIndividual.fitness+ " and generation "+p.CURRENT_GENERATION );

            Population.CURRENT_GENERATION++;
            //System.out.println("current gen "+p.CURRENT_GENERATION);

        }

        MPI.Finalize();
    }

    static List<List<Railroad>> gatherSolutions(List<Railroad> mySolutions, int rank, int size) {
        List<List<Railroad>> allSolutions = new ArrayList<>();

        //if process is root
        if (rank == 0) {
            //root's sols
            allSolutions.add(mySolutions);
            // receive sols from others
            for (int i = 1; i < size; i++) {
                //reserve an array for receiving
                Railroad[] receivedSolutions = new Railroad[mySolutions.size()];
                MPI.COMM_WORLD.Recv(receivedSolutions, 0, mySolutions.size(), MPI.OBJECT, i, 0);
                allSolutions.add(Arrays.asList(receivedSolutions));
            }
        } else {
            //create arr from list
            Railroad[] mySolutionsArray = mySolutions.toArray(new Railroad[mySolutions.size()]);
            //send chunk of evaluated pop to root process
            MPI.COMM_WORLD.Send(mySolutionsArray, 0, mySolutionsArray.length, MPI.OBJECT, 0, 0);
        }
        return allSolutions;
    }

    static List<Railroad> combineSolutions(List<List<Railroad>> allSolutions) {
        List<Railroad> combinedSolutions = new ArrayList<>();
        //flatten the solution list
        for (List<Railroad> solutions : allSolutions) {
            combinedSolutions.addAll(solutions);
        }
        return combinedSolutions;
    }

} 