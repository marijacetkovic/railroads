package util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TileDictionary {
    private HashMap<Integer, int[][]> tileMap;

    public TileDictionary() {
        tileMap = new HashMap<>();

        // define tiles as int matrices
        int[][] tile1 = {
                {0, 0, 0},
                {1, 1, 1},
                {0, 0, 0}
        };
        int[][] tile2 = {
                {0, 1, 0},
                {0, 1, 0},
                {0, 1, 0}
        };
        int[][] tile3 = {
                {0, 1, 0},
                {0, 1, 1},
                {0, 0, 0}
        };
        int[][] tile4 = {
                {0, 1, 0},
                {1, 1, 0},
                {0, 0, 0}
        };
        int[][] tile5 = {
                {0, 0, 0},
                {1, 1, 0},
                {0, 1, 0}
        };
        int[][] tile6 = {
                {0, 0, 0},
                {0, 1, 1},
                {0, 1, 0}
        };
        int[][] tile7 = {
                {0, 1, 0},
                {1, 1, 1},
                {0, 0, 0}
        };
        int[][] tile8 = {
                {0, 0, 0},
                {1, 1, 1},
                {0, 1, 0}
        };
        int[][] tile9 = {
                {0, 1, 0},
                {0, 1, 1},
                {0, 1, 0}
        };
        int[][] tile10 = {
                {0, 1, 0},
                {1, 1, 0},
                {0, 1, 0}
        };
        int[][] tile11 = {
                {0, 1, 0},
                {1, 1, 1},
                {0, 1, 0}
        };

        // populate the dict
        tileMap.put(1, tile1);
        tileMap.put(2, tile2);
        tileMap.put(3, tile3);
        tileMap.put(4, tile4);
        tileMap.put(5, tile5);
        tileMap.put(6, tile6);
        tileMap.put(7, tile7);
        tileMap.put(8, tile8);
        tileMap.put(9, tile9);
        tileMap.put(10, tile10);
        tileMap.put(11, tile11);
    }

    public int[][] getTile(int tileNumber) {
        return tileMap.get(tileNumber);
    }

    public int getKey(int[][] tile) {
        for (Map.Entry<Integer, int[][]> e:tileMap.entrySet()) {
            if(Arrays.deepEquals(e.getValue(),tile)){
                return e.getKey();
            }
        }
        return 0;
    }

    public int[][] transform(int[][] m) {
        int n = m.length;
        int[][] m2 = new int[3 * n][3 * n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
               // System.out.print( m[i][j]+" ");
                int[][] tile = tileMap.get(m[i][j]);
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        m2[3 * i + x][3 * j + y] = tile[x][y];
                    }
                }
            }
        }
        return m2;
    }

    public int getPrice(int tileNumber) {
        int price;
        switch (tileNumber) {
            case 1:
            case 2:
                price = 1;
                break;
            case 3:
            case 4:
            case 5:
            case 6:
                price = 2;
                break;
            case 7:
            case 8:
            case 9:
            case 10:
                price = 3;
                break;
            case 11:
                price = 4;
                break;
            default:
                price = 0;
                break;
        }
        return price;
    }
}
