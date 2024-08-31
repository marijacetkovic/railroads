package util;

public class Config {
    public static final int NUM_GENERATIONS = 100;
    public static int WORLD_SIZE = 20;
    public static int NUM_TRAINS = 50;
    public static final int POPULATION_SIZE = 1000;
    public static final double CROSSOVER_RATE = 0.85;
    public static final int STAGNATION_BOUND = 10;
    public static final double DEFAULT_MUTATION = 0.01;
    public static double CROSSROAD_NUMBER = 0.27;
    public static double MUTATION_RATE = 0.01;
    public static final double PEAK_MUTATION = 0.001;
    public final static int ELITISM_K = (int)(0.01*POPULATION_SIZE);
    public static final int CANVAS_SIZE = 800;
    public static final int RANDOM_SEED = 3;
    public static boolean RENDER_GUI = false;
    public static final double NUM_TRAINS_SF = 10000.0;
    public static final double TILE_PRICING_SF = 500/WORLD_SIZE;

    public final static int ROULETTE_WHEEL_SELECTION = 0;
    public final static int TRUNCATION_SELECTION = 1;
    public final static int TEST_SELECTION = 2;
    public final static int SINGLE_POINT_CROSSOVER = 0;
    public final static int OTHER_CROSSOVER = 1;
    public final static int INSERTION_MUTATION = 0;
    public final static int OTHER_MUTATION = 1;
}
