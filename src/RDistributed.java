import mpi.*;
import util.Config;
import util.WorkSplitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class RDistributed {
    private static Population p = new Population();
    private static Railroad bestIndividual = p.getSolutions().get(0);
    private static ConcurrentLinkedQueue<Railroad> bestIndividualQueue = new ConcurrentLinkedQueue<>();
    private static WorkSplitter wSplitter;


    public RDistributed() {
        //this.NUM_THREADS = numThreads;
        // this.bestIndividualQueue = bestIndividualQueue;
        //this.barrier = new CyclicBarrier(NUM_THREADS+1);
    }

    public static void main(String[] args){
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        double startTime = 0,endTime=0;
        if (rank == 0){
            startTime = System.currentTimeMillis();
        }

        wSplitter = new WorkSplitter(p.getSolutions().size(), size);

        while(p.getCurrentGeneration()<Config.NUM_GENERATIONS){
            p.resetStatistics();
            //get its own chunk of population
            wSplitter.setSize(p.getSolutions().size());
            int start = wSplitter.getStart(rank);
            int end = wSplitter.getEnd(rank);
            System.out.println("for process "+rank+" start is "+start+" and end is "+end);

            //perform eval on chunk of population
            List<Railroad> mySolutions = p.performEvaluationD(start,end,rank);
            System.out.println("start and end for "+start+" "+end +" "+"mySolutions length "+mySolutions.size()+" for worker "+rank);
            //gather the solutions
            int minChunkSize = wSplitter.getMinChunkSize();
            System.out.println("minChunksize "+minChunkSize);

            List<List<Railroad>> allSolutions = gatherSolutions(mySolutions, rank, size, minChunkSize);
            List<Railroad>[] solutionsArray = new List[size];
            allSolutions.toArray(solutionsArray);
            MPI.COMM_WORLD.Bcast(solutionsArray, 0, size, MPI.OBJECT, 0);
            allSolutions = Arrays.asList(solutionsArray);

            //System.out.println("for process with rank "+rank+" size of allSolutions is "+allSolutions.size());
            List<Railroad> evaluatedSolutions = combineSolutions(allSolutions,p.getPSize());
            System.out.println("for process with rank "+rank+" size of evaluatedSolutions is "+evaluatedSolutions.size());

            //update local p
            p.setSolutions(evaluatedSolutions);

            MPI.COMM_WORLD.Barrier();
            List<Railroad> eliteP=new ArrayList<>();
            if (rank == 0){ // just root process printing stats
                System.out.println(p.getSolutions().size()+" sol size");
                for (Railroad r: p.getSolutions()) {
                //    System.out.println(r);
                }
                p.updateAllStatistics();
                p.printPopulationStatistics();
                int index=0;

                //choose the elite
                for (int i = 0; i < Config.ELITISM_K; i++) {
                    System.out.println("index +"+i+" from process w rank "+rank);
                    Railroad r = p.getBestSolutions();
                    // r.selected=false;
                    eliteP.add(r);
                    index++;
                }
            }
            //building the population with leftover p size - elitism places
            int CAPACITY = p.getPSize() - Config.ELITISM_K;
            System.out.println(CAPACITY+"capacity"+" for worker "+rank);

            //get chunk of population which process x will be building
            wSplitter.setSize(CAPACITY);
            start = wSplitter.getStart(rank);
            end = wSplitter.getEnd(rank);

            //process builds its chunk of pop
            List<Railroad> workerP = p.buildPopulation(start,end,new ArrayList<>());

            System.out.println("workerP length "+workerP.size()+" for worker "+rank);

            List<List<Railroad>> builtP = gatherSolutions(workerP, rank, size, minChunkSize); //root has all pop
            List<Railroad> builtPFlattened = new ArrayList<>();

            if (rank==0){
                builtPFlattened = combineSolutions(builtP,CAPACITY);
                builtPFlattened.addAll(eliteP);
            }

            Railroad[] builtPArray = builtPFlattened.toArray(new Railroad[Config.POPULATION_SIZE]);

            MPI.COMM_WORLD.Bcast(builtPArray, 0, Config.POPULATION_SIZE, MPI.OBJECT, 0);
            builtPFlattened = Arrays.stream(builtPArray).toList();
            System.out.println("for process with rank "+rank+" size of builtP is "+builtPFlattened.size());
            p.setSolutions(builtPFlattened);

            if(rank==0){
                bestIndividual = p.getBestIndividual();
                bestIndividualQueue.offer(bestIndividual);
                System.out.println("best solution id "+bestIndividual.id+" with fitness "+bestIndividual.fitness+ " and generation "+p.getCurrentGeneration() );
                Population.increaseCurrentGeneration();
                System.out.println("current gen "+p.getCurrentGeneration());
            }
            MPI.COMM_WORLD.Barrier();
        }
        if (rank == 0){
            endTime = System.currentTimeMillis();
            System.out.println("Time taken to perform the algorithm is "+(endTime-startTime));
        }
        MPI.Finalize();
    }

    static List<List<Railroad>> gatherSolutions(List<Railroad> mySolutions, int rank, int size, int minChunkSize) {
        List<List<Railroad>> allSolutions = new ArrayList<>();
        //if process is root
        if (rank == 0) {
            //root's sols
            allSolutions.add(mySolutions);
            // receive sols from others
            for (int i = 1; i < size; i++) {
                //reserve buffer array for receiving
                Railroad[] receivedSolutions = new Railroad[mySolutions.size()];
                MPI.COMM_WORLD.Recv(receivedSolutions, 0,mySolutions.size(), MPI.OBJECT, i, 0);
                allSolutions.add(Arrays.asList(receivedSolutions));
            }
            System.out.println("IMHERE length of allsols "+allSolutions.size());
        } else {
            //create arr from list
            Railroad[] mySolutionsArray = mySolutions.toArray(new Railroad[mySolutions.size()]);
            //send chunk of evaluated pop to root process
            MPI.COMM_WORLD.Send(mySolutionsArray, 0, mySolutions.size(), MPI.OBJECT, 0, 0);
        }
        return allSolutions;
    }

    static List<Railroad> combineSolutions(List<List<Railroad>> allSolutions, int capacity) {
        List<Railroad> combinedSolutions = new ArrayList<>();
        //flatten the solution list
        for (List<Railroad> solutions : allSolutions) {
            int sizeToAdd = Math.min(capacity, solutions.size());
            combinedSolutions.addAll(solutions.subList(0, sizeToAdd));
            capacity -= sizeToAdd;
            if (capacity <= 0) { //cut if a larger chunk is present, so capacity is not exceeded
                break;
            }
        }
        return combinedSolutions;
    }
} 