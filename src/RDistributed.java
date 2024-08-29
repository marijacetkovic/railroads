import mpi.*;
import util.Config;
import util.WorkSplitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class RDistributed {
    private static Population p = new Population();
    private static Railroad bestIndividual;
    public static List<int[]> trains;
    private static BlockingQueue<Railroad> bestIndividualQueue = new LinkedBlockingQueue<>();
    private static WorkSplitter wSplitter;

    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        trains = Main.getRandomTrains(Config.NUM_TRAINS);
        double startTime = 0, endTime = 0;

        if (rank == 0) {
            startTime = System.currentTimeMillis();

            // Initialize the population on the root process
            p.initializeSolutionsD();

            // Convert the initial population list to an array
            Railroad[] pArray = p.getSolutions().toArray(new Railroad[0]);

            // Broadcast the population array to all processes
            MPI.COMM_WORLD.Bcast(pArray, 0, p.getSolutions().size(), MPI.OBJECT, 0);

            // Set the best individual on the root process
            bestIndividual = p.getSolutions().get(0);

            Main.renderGui(trains, bestIndividualQueue);
        } else {
            // If not the root process, prepare to receive the population array
            Railroad[] pArray = new Railroad[Config.POPULATION_SIZE];

            // Receive the broadcasted population array
            MPI.COMM_WORLD.Bcast(pArray, 0, Config.POPULATION_SIZE, MPI.OBJECT, 0);

            // Convert the received array back to a list and set it as the solutions in the population
            p.setSolutions(Arrays.asList(pArray));

            // Set the best individual on non-root processes as well
            bestIndividual = p.getSolutions().get(0);
        }

        MPI.COMM_WORLD.Barrier();

        wSplitter = new WorkSplitter(Config.POPULATION_SIZE, size);

        while (p.getCurrentGeneration() < Config.NUM_GENERATIONS) {
            p.resetStatistics();
            //get its own chunk of population
            wSplitter.setSize(Config.POPULATION_SIZE);
            int start = wSplitter.getStart(rank);
            int end = wSplitter.getEnd(rank);

            //perform eval on chunk of population
            List<Railroad> mySolutions = p.performEvaluationD(start, end, rank);

            //gather the solutions
            MPI.COMM_WORLD.Barrier();
            List<List<Railroad>> allSolutions = gatherSolutions(mySolutions, rank, size);

            List<Railroad>[] solutionsArray = new List[size];
            allSolutions.toArray(solutionsArray);

            MPI.COMM_WORLD.Bcast(solutionsArray, 0, size, MPI.OBJECT, 0);
            allSolutions = Arrays.asList(solutionsArray);


            List<Railroad> evaluatedSolutions = combineSolutions(allSolutions, p.getPSize());

            //update local p
            p.setSolutions(evaluatedSolutions);

            MPI.COMM_WORLD.Barrier();
            List<Railroad> eliteP = new ArrayList<>();

            if (rank == 0) { // just root process printing stats

                p.updateAllStatistics();
                p.printPopulationStatistics();
                eliteP = GA.selectElite(p);
            }
            //building the population with leftover p size - elitism places
            int CAPACITY = p.getPSize() - Config.ELITISM_K;

            //get chunk of population which process x will be building
            wSplitter.setSize(CAPACITY);

            //process builds its chunk of pop
            List<Railroad> workerP = p.buildPopulation(wSplitter.getStart(rank), wSplitter.getEnd(rank), new ArrayList<>());
            List<List<Railroad>> builtP = gatherSolutions(workerP, rank, size); //root has all pop

            List<Railroad> builtPFlattened = new ArrayList<>();

            if (rank == 0) {
                builtPFlattened = combineSolutions(builtP, CAPACITY);
                builtPFlattened.addAll(eliteP);
            }

            Railroad[] builtPArray = builtPFlattened.toArray(new Railroad[Config.POPULATION_SIZE]);

            MPI.COMM_WORLD.Bcast(builtPArray, 0, Config.POPULATION_SIZE, MPI.OBJECT, 0);
            builtPFlattened = Arrays.stream(builtPArray).toList();
            p.setSolutions(builtPFlattened);

            if (rank == 0) {
                GA.updateBestIndividual(p, bestIndividualQueue);
                Population.increaseCurrentGeneration();
            }
            MPI.COMM_WORLD.Barrier();
        }
        if (rank == 0) {
            endTime = System.currentTimeMillis();
            System.out.println("Time taken to perform the algorithm is " + (endTime - startTime));
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
                //reserve buffer array for receiving
                Railroad[] receivedSolutions = new Railroad[mySolutions.size()];
                MPI.COMM_WORLD.Recv(receivedSolutions, 0, mySolutions.size(), MPI.OBJECT, i, 0);
                allSolutions.add(Arrays.asList(receivedSolutions));
            }
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