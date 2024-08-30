package util;
public class PathFinder {

    private static int[][] directions = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };

    public static double findPath(int[][] matrix, int startI, int startJ, int endI, int endJ) {
        boolean[][] visited = new boolean[matrix.length][matrix.length];

        boolean found = depthFirstSearch(matrix, startI, startJ, endI, endJ, visited);

        return found ? 1.0 : 0.0;
    }

    private static boolean depthFirstSearch(int[][] matrix, int i, int j, int endI, int endJ, boolean[][] visited) {
        if (isValidPosition(matrix, i, j, visited)) return false;

        visited[i][j] = true;

        if (i == endI && j == endJ) return true;

        for (int[] dir : directions) {
            if (depthFirstSearch(matrix, i + dir[0], j + dir[1], endI, endJ, visited)) return true;
        }

        return false;
    }

    private static boolean isValidPosition(int[][] matrix, int i, int j, boolean[][] visited) {
        return (i < 0 || j < 0 || i >= matrix.length || j >= matrix[0].length || visited[i][j] || matrix[i][j] != 1);
    }
}
