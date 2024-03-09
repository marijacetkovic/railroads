import java.util.*;

public class Railroad implements Comparable<Railroad>{
    //represents a chromosome - collection of genes
    int N=Main.N;
    double fitness;
    //List<int[][]> world;
    int[][] world;
    List<int[]> trains;
    Random random = new Random();
    private double mutationRate;
    boolean selected = false;

    // list of solutions??
    public Railroad(List<int[]> trains){
        this.trains = trains;
        //this.N = N;
        this.world = generateRandomMatrix(N);
    }

    public void setFitness(int x){
        this.fitness=x;
    }
    public double getFitness(){return this.fitness;}

    //dfs as evaluation helper func

    public void DFS(int startI, int startJ, int endI, int endJ) {
        boolean[][] visited = new boolean[N][N];
        Arrays.fill(visited, false);

        if (world[startI][startJ] != 1) {
            throw new RuntimeException("Invalid train start position.");
        }

        boolean found = depthFirstSearch(startI, startJ, endI, endJ, visited);

        if (found) {
            System.out.println("found");
        } else {
            System.out.println("not found");
        }
    }
    private boolean depthFirstSearch(int i, int j, int endI, int endJ, boolean[][] visited) {
        if (i < 0 || j < 0 || i >= world.length || j >= world[0].length || visited[i][j] || world[i][j] != 1) {
            return false;
        }

        visited[i][j] = true;

        if (i == endI && j == endJ) {
            return true;
        }

        int[][] directions = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };
        for (int[] dir : directions) {
            int newRow = i + dir[0];
            int newCol = j + dir[1];

            if (depthFirstSearch(newRow, newCol, endI, endJ, visited)) {
                return true;
            }
        }
        return false;
    }

    //generate random railroad instance
    public int[][] generateRandomMatrix(int size) {
        int[][] matrix = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = random.nextInt(11)+1;
                System.out.print(matrix[i][j]+ " ");
            }
            System.out.println();
        }
        return matrix;
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
    public void insertionMutation(){
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world.length; j++) {
                    int tileKey = random.nextInt(11)+1;
                    setTile(i,j,tileKey);
            }
        }
    }


    //returns sum of all tile costs
    public int getSum(){
        int sum = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                sum+= Main.dict.getPrice(world[i][j]);
            }
        }
        return sum;
    }

    @Override
    public int compareTo(Railroad o) {
        return Double.compare(this.fitness, o.fitness);
    }

    //fitness evaluation function
    public double rateFitness() {
        for (int i = 0; i < trains.size(); i++) {
            int[] t = trains.get(i);
            DFS(t[0],t[1],t[2],t[3]);
        }

        return 1;
    }
}
