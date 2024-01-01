import java.util.*;

public class Railroad {
    //represents a chromosome - collection of genes
    int N=Main.N;
    int score;
    //List<int[][]> world;
    int[][] world;
    List<List<Integer>> trains;
    // list of solutions??
    public Railroad(List<List<Integer>> trains){
        this.trains = trains;
        //this.N = N;
        this.world = generateRandomMatrix(N);
    }

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


    public int[][] generateRandomMatrix(int size) {
        Random random = new Random();
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
}
