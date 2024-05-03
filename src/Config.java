public class Config {
    public static final int NUM_GENERATIONS = 2000;
    public static final int WORLD_SIZE = 10;
    public static final int NUM_TRAINS = 5;
    public static final int POPULATION_SIZE = 10;
    public static final double CROSSOVER_RATE = 0.8;
    public static final double MUTATION_RATE = 0.05;
    final static int ELITISM_K = (int)(0.0*POPULATION_SIZE);
    public static final int CANVAS_SIZE = 800;
    public static final int RANDOM_SEED = 4;
    public static final boolean RENDER_GUI = false;
    final static int ROULETTE_WHEEL_SELECTION = 0;
    public final static int TRUNCATION_SELECTION = 1;
    public final static int TEST_SELECTION = 2;
    public final static int SINGLE_POINT_CROSSOVER = 0;
    public final static int OTHER_CROSSOVER = 1;
    public final static int INSERTION_MUTATION = 0;
    public final static int OTHER_MUTATION = 1;
}
