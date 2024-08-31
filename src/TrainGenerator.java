import util.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrainGenerator {



    public static List<int[]> getRandomTrains(int numT) {
        List<int[]> trains = new ArrayList<>();
        for (int i = 0; i < numT; i++) {
            //System.out.println("TRAIN " + i);
            trains.add(generateRandomTrain());
        }
        return trains;
    }

    private static int[] generateRandomTrain() {
        int[] train = new int[4];
        for (int i = 0; i < 4; i++) {
            train[i] = GA.random.nextInt(0, Config.WORLD_SIZE);
            //System.out.print(train[i] + " ");
        }
        //System.out.println();
        return train;
    }
}
