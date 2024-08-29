import util.Config;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class GA {
    static Random random = new Random(4);

    public static void performEvaluation(Population population) {
        population.performEvaluation();
        // population.performEvaluationWithPricing();
    }

    public static List<Railroad> selectElite(Population population) {
        List<Railroad> eliteList = new ArrayList<>(Config.ELITISM_K);
        Railroad elite = population.getBestSolutions();
        eliteList.add(elite);
        for (int i = 1; i < Config.ELITISM_K; i++) {
            elite = population.getBestSolutions();
            eliteList.add(elite);
        }
        return eliteList;
    }

    public static void buildAndSetNewPopulation(Population population, List<Railroad> newP) {
        int currentSize = population.getSolutions().size();
        int index = newP.size();
        population.buildPopulation(index, currentSize, newP);
        population.setSolutions(newP);
    }

    public static void updateBestIndividual(Population population, BlockingQueue<Railroad> bestIndividualQueue) {
        Railroad bestIndividual = population.getBestIndividual();
        bestIndividual.generation = Population.getCurrentGeneration();
        bestIndividualQueue.add(bestIndividual);
        population.updateBestSolution(bestIndividual);
        System.out.println("Best solution ID: " + bestIndividual.id +
                " with fitness: " + bestIndividual.fitness +
                ", number of trains finished: " + bestIndividual.numTrains +
                ", tile price: " + bestIndividual.tilePricing +
                " for generation: " + Population.getCurrentGeneration());
    }

    public static void crossover(int crossoverType, Railroad r1, Railroad r2) {
        switch (crossoverType) {
            case Config.SINGLE_POINT_CROSSOVER:
                singlePointCrossover(r1, r2);
                break;
            case Config.OTHER_CROSSOVER:
                break;
            default:
                System.out.println("invalid crossover type");

        }
    }
    private static void singlePointCrossover(Railroad r1, Railroad r2) {
        int[][] m1 = new int[Config.WORLD_SIZE][Config.WORLD_SIZE];
        int[][] m2 = new int[Config.WORLD_SIZE][Config.WORLD_SIZE];
        int crossoverPoint = random.nextInt(Config.WORLD_SIZE); // select a random crossover point

        for (int i = 0; i < Config.WORLD_SIZE; i++) {
            for (int j = 0; j < Config.WORLD_SIZE; j++) {
                if (j < crossoverPoint) {
                    m1[i][j] = r1.world[i][j];
                    m2[i][j] = r2.world[i][j];
                } else {
                    m1[i][j] = r2.world[i][j];
                    m2[i][j] = r1.world[i][j];
                }
            }
        }

        r1.setWorld(m1);
        r2.setWorld(m2);
    }


    public static void insertionMutation(Railroad railroad) {
        int[][] world = railroad.getWorld();
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[i].length; j++) {
                if (Math.random() < Config.MUTATION_RATE) {
                    int tileKey = random.nextInt(11) + 1;
                    railroad.setTile(i, j, tileKey);
                }
            }
        }
    }

    public static int[][] generateRandomIndividual(int size) {
        int[][] matrix = new int[size][size];
        if (Config.WORLD_SIZE>20){
            int crossroadsCount = (int) (Math.ceil(size*size * Config.CROSSROAD_NUMBER)); // % distribution
            List<int[]> crossroadPositions = new ArrayList<>();

            // Randomly place crossroads in the matrix
            while (crossroadPositions.size() < crossroadsCount) {
                int randRow = random.nextInt(size);
                int randCol = random.nextInt(size);
                crossroadPositions.add(new int[]{randRow,randCol});
            }

            // Fill the matrix
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (containsArray(crossroadPositions,new int[]{i,j})) {
                        matrix[i][j] = 11; //(crossroads)
                    } else {
                        matrix[i][j] = random.nextInt(10) + 1;
                    }
                }
            }
        }
        else {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = random.nextInt(11)+1;

                    //System.out.print(matrix[i][j]+ " ");
                }
                //System.out.println();
            }
        }
        return matrix;
    }

    private static boolean containsArray(List<int[]> list, int[] array) {
        for (int[] arr : list) {
            if (Arrays.equals(arr, array)) {
                return true;
            }
        }
        return false;
    }

    public static void mutate(int mutationType, Railroad railroad) {
        switch (mutationType) {
            case Config.INSERTION_MUTATION:
                insertionMutation(railroad);
                break;
            case Config.OTHER_MUTATION:
                break;
            default:
                System.out.println("Invalid mutation type");
        }
    }
}
