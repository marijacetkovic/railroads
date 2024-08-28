package util;
public class PathFinder {

    private static int[][] directions = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };
    /**
     * Performs a DFS on the given 2D matrix to determine if a path exists from start to end.
     * @param matrix The transformed world matrix.
     * @param startI Starting row index.
     * @param startJ Starting column index.
     * @param endI Ending row index.
     * @param endJ Ending column index.
     * @return 1 if a path exists, 0 otherwise.
     */
    public static double findPath(int[][] matrix, int startI, int startJ, int endI, int endJ) {
        boolean[][] visited = new boolean[matrix.length][matrix.length];

        boolean found = depthFirstSearch(matrix, startI, startJ, endI, endJ, visited);

        return found ? 1.0 : 0.0;
    }

    /**
     * A recursive method to perform depth-first search.
     * @param matrix The transformed world matrix.
     * @param i Current row index.
     * @param j Current column index.
     * @param endI Ending row index.
     * @param endJ Ending column index.
     * @param visited A matrix to track visited nodes.
     * @return True if the end is reached, false otherwise.
     */
    private static boolean depthFirstSearch(int[][] matrix, int i, int j, int endI, int endJ, boolean[][] visited) {
        if (isValidPosition(matrix, i, j, visited)) return false;

        visited[i][j] = true;

        if (i == endI && j == endJ) return true;

        for (int[] dir : directions) {
            if (depthFirstSearch(matrix, i + dir[0], j + dir[1], endI, endJ, visited)) return true;
        }

        return false;
    }

    /**
     * Utility method to check if a position is out of bounds or already visited.
     * @param matrix The transformed world matrix.
     * @param i Row index.
     * @param j Column index.
     * @param visited A matrix to track visited nodes.
     * @return True if out of bounds or visited, false otherwise.
     */
    private static boolean isValidPosition(int[][] matrix, int i, int j, boolean[][] visited) {
        return (i < 0 || j < 0 || i >= matrix.length || j >= matrix[0].length || visited[i][j] || matrix[i][j] != 1);
    }
}
