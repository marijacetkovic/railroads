import util.Config;
import util.PathFinder;

import java.io.Serializable;
import java.util.*;

/**
 * The {@code Railroad} class represents an individual solution in this genetic algorithm
 * for optimizing train routes on a grid. Each instance consists of a world configuration,
 * a set of trains, and methods to evaluate the fitness of the configuration based on
 * train path success and/or tile costs.
 */


public class Railroad implements Serializable, Comparable<Railroad> {
    int N=Config.WORLD_SIZE;
    double fitness;
    int[][] world;
    int[][] worldTransformed;
    List<int[]> trains;
    int generation;
    int numTrains=0;
    double scalingFactor=1.5;
    private double mutationRate;
    boolean selected = false;
    int id;
    double tilePricing;
    double orgTilePricing;
    private double scaledTilePricing;
    private double numTrainsScaled;

    // list of solutions??
    public Railroad(List<int[]> trains,int id){
        this.trains = trains;
        this.id = id;
        this.world = GA.generateRandomIndividual(N);
        this.worldTransformed = Main.dict.transform(this.world);
        orgTilePricing = getSum();
    }

    public void setFitness(int x){
        this.fitness=x;
    }
    public void setWorld(int[][] world){this.world=world;
        this.worldTransformed = Main.dict.transform(this.world);}
    public double getFitness(){return this.fitness;}
    public int[][] getWorld(){return this.world;}
    public int getNumTrains() {return this.numTrains;}


    //get tile
    public int getTile(int i, int j){
        return this.world[i][j];
    }
    //set tile func
    public void setTile(int i, int j, int tileKey){
        this.world[i][j]=tileKey;
    }

    //returns sum of all tile costs
    private int getSum() {
        return Arrays.stream(world)
                .flatMapToInt(Arrays::stream)
                .map(Main.dict::getPrice)
                .sum();
    }

    //fitness evaluation function
    public double rateFitness() {
        this.fitness=0;
        this.selected = false;
        for (int[] t : trains) {
            //train coordinates are generated wrt tiles encoded by types
            //to transform them into binary matrix i placed them in the center of the tile, 3*i+1
            this.fitness += PathFinder.findPath(worldTransformed,3*t[0]+1,3*t[1]+1,3*t[2]+1,3*t[3]+1);
        }
        tilePricing = getSum();
        this.numTrains = (int) this.fitness;
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
        numTrainsScaled = numTrains*Config.NUM_TRAINS_SF;
        fitness = (Math.round((numTrainsScaled - scaledTilePricing)*100))/100;
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
