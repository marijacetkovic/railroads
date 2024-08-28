import util.Config;
import util.PathFinder;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.*;

public class Railroad implements Serializable, Comparable<Railroad> {
    //represents a chromosome - collection of genes
    int N=Config.WORLD_SIZE;
    double fitness;
    int[][] world;
    int[][] worldTransformed;
    List<int[]> trains;
    int generation;
    int numTrains;
    double scalingFactor=1.5;
    Random random = new Random();
    private double mutationRate;
    boolean selected = false;
    int id;
    double tilePricing;
    double orgTilePricing;
    private double scaledTilePricing;
    private double numTrainsScaled;
    private int countCrossroads;

    // list of solutions??
    public Railroad(List<int[]> trains,int id){
        this.trains = trains;
        this.id = id;
        //this.N = N;
        this.world = generateRandomMatrix(N);
        this.worldTransformed = Main.dict.transform(this.world);
        orgTilePricing = getSum();
    }

    public void setFitness(int x){
        this.fitness=x;
    }
    public void setWorld(int[][] world){this.world=world;
        this.worldTransformed = Main.dict.transform(this.world);}
    public double getFitness(){return this.fitness;}
    public int getNumTrains() {return this.numTrains;}
    //dfs as evaluation helper func


    public void transformBack (){
        int[][] m = this.worldTransformed;
        int n = m.length;
        int[][] submatrix = new int[3][3];
        for (int i = 0; i < n;) {
            for (int j = 0; j < n;) {
                submatrix = new int[3][3];
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        submatrix[k][l] = m[i+k][j+l];
                    }
                }
                this.world[i/3][j/3]=Main.dict.getKey(submatrix);
                j+=3;
            }
            i+=3;
        }
    }


    private void transformSubmatrix(int i, int j) {
        int[][] m = new int[3][3];
        for (int x = i - 1; x <= i + 1; x++) {
            for (int y = j - 1; y <= j + 1; y++) {
                int rowIndex = (x - (i - 1)) % 3;
                int colIndex = (y - (j - 1)) % 3;
                if (x >= 0 && x < worldTransformed.length && y >= 0 && y < worldTransformed[0].length) {
                    m[rowIndex][colIndex] = worldTransformed[x][y];
                }
            }
        }
        int tileKey = Main.dict.getKey(m);
        world[(i - 1) / 3][(j - 1) / 3] = tileKey;
    }


    //generate random railroad instance
    public int[][] generateRandomMatrix(int size) {
        int[][] matrix = new int[size][size];
        if (true){
            //System.out.println("manja budalo");
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
                        countCrossroads++;
                    } else {
                        matrix[i][j] = random.nextInt(10) + 1;
                    }
                }
            }
        }
//        else {
//            for (int i = 0; i < size; i++) {
//                for (int j = 0; j < size; j++) {
//                    matrix[i][j] = random.nextInt(11)+1;
//
//                    //System.out.print(matrix[i][j]+ " ");
//                }
//                //System.out.println();
//            }
//        }

        //System.out.println("distribution of crossroads is "+countCrossroads/Math.pow(size,2)*100);
        return matrix;
    }

    private boolean containsArray(List<int[]> list, int[] array) {
        for (int[] arr : list) {
            if (Arrays.equals(arr, array)) {
                return true;
            }
        }
        return false;
    }


    //get tile
    public int getTile(int i, int j){
        return this.world[i][j];
    }
    //set tile func
    public void setTile(int i, int j, int tileKey){
        this.world[i][j]=tileKey;
    }

    //mutate one railroad instance by switching a random tile
    public void produceRandomIndividual(){
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world.length; j++) {
                    int tileKey = random.nextInt(11)+1;
                    setTile(i,j,tileKey);
            }
        }
    }


    public void insertionMutation(){
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world.length; j++) {
                if(Math.random()<Config.MUTATION_RATE) {
                    int tileKey = random.nextInt(11) + 1;
                    setTile(i, j, tileKey);
                }
            }
        }
    }
    //returns sum of all tile costs
    public int getSum() {
        return Arrays.stream(world)
                .flatMapToInt(Arrays::stream)
                .map(Main.dict::getPrice)
                .sum();
    }

    //fitness evaluation function
    public double rateFitness() {
        fitness=0;
        selected = false;
        for (int[] t : trains) {
            //train coordinates are generated wrt tiles encoded by types
            //to transform them into binary matrix i placed them in the center of the tile, 3*i+1
            fitness += PathFinder.findPath(worldTransformed,3*t[0]+1,3*t[1]+1,3*t[2]+1,3*t[3]+1);
        }
        numTrains = (int) fitness;
        return fitness;
    }

    public double rateFitnessWithPricing() {
        numTrains=0;
        selected = false;
        for (int[] t : trains) {
            //train coordinates are generated wrt tiles encoded by types
            //to transform them into binary matrix i placed them in the center of the tile, 3*i+1
            numTrains+= PathFinder.findPath(worldTransformed,3*t[0]+1,3*t[1]+1,3*t[2]+1,3*t[3]+1);
        }
        tilePricing = getSum();
        scaledTilePricing = (tilePricing*Config.TILE_PRICING_SF);
        //this.numTrainsScaled = 100 * (Config.NUM_TRAINS - numTrains); //scaled num of trains that dont finish
        numTrainsScaled = numTrains*Config.NUM_TRAINS_SF;
        fitness = (Math.round((numTrainsScaled - scaledTilePricing)*100))/100;
       // System.out.println("id "+id+"num trains that finish "+numTrains+" scaled to "+numTrainsScaled+" scaledTilePricing "+scaledTilePricing);
        //System.out.println(" and final fitness is "+fitness);
        return fitness;
    }

    @Override
    public int compareTo(Railroad o) {
        // Compare based on fitness
        if (this.fitness < o.getFitness()) {
            return -1;
        } else if (this.fitness > o.getFitness()) {
            return 1;
        } else {
            return 0;
        }
    }


}
