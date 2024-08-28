package util;

public class Config {
    public static final int NUM_GENERATIONS = 1000;
    public static final int WORLD_SIZE = 15;
    public static final int NUM_TRAINS = 30;
    public static final int POPULATION_SIZE = 1000;
    public static final double CROSSOVER_RATE = 0.85;
    public static final int STAGNATION_BOUND = 10;
    public static final double DEFAULT_MUTATION = 0.03;
    public static final double CROSSROAD_NUMBER = 0.25;
    public static double MUTATION_RATE = 0.03;
    public static final double PEAK_MUTATION = 0.2;
    public final static int ELITISM_K = (int)(0.001*POPULATION_SIZE);
    public static final int CANVAS_SIZE = 800;
    public static final int RANDOM_SEED = 4;
    public static final boolean RENDER_GUI = true;
    public static final double TILE_PRICING_SF = 1/(Math.pow(WORLD_SIZE,2));
    public static final double NUM_TRAINS_SF = 30;

    public final static int ROULETTE_WHEEL_SELECTION = 0;
    public final static int TRUNCATION_SELECTION = 1;
    public final static int TEST_SELECTION = 2;
    public final static int SINGLE_POINT_CROSSOVER = 0;
    public final static int OTHER_CROSSOVER = 1;
    public final static int INSERTION_MUTATION = 0;
    public final static int OTHER_MUTATION = 1;
}
