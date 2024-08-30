import util.Config;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Gfx extends JPanel {
    public List<int[]> trains;
    public int N;
    Population p = Main.p;
    public static int canvasSize;
    int circleDiameter;
    int offsetCentre;
    BlockingQueue<Railroad> bestIndividualQueue;
    private int generationLabelWidth=30;

    public Gfx(List<int[]> trains, BlockingQueue<Railroad> bestIndividualQueue) {
        //this.solutions = solutions;
        this.trains = trains;
        this.N = Config.WORLD_SIZE * 3;
        canvasSize = 700;
        circleDiameter = canvasSize / N;
        offsetCentre = circleDiameter/2;
        this.bestIndividualQueue = bestIndividualQueue;
        this.setPreferredSize(new Dimension(500,500));
    }


    @Override
    protected void paintComponent(Graphics g) {
//        repaint();
//        revalidate();
        Railroad r = null;

        try {
            r = bestIndividualQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int totalGridWidth = N * circleDiameter;
        int totalGridHeight = N * circleDiameter;

        int offsetX = (getWidth() - totalGridWidth) / 2;
        int offsetY = (getHeight() - totalGridHeight) / 2;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int x = j * circleDiameter + offsetX + offsetCentre;
                int y = i * circleDiameter + offsetY + offsetCentre;

                // border
                g.setColor(Color.GRAY);
                g.drawRect(x, y, circleDiameter, circleDiameter);

                // dimensions
                int innerDiameter = (int) (circleDiameter * 0.95);
                int innerX = x + (circleDiameter - innerDiameter) / 2;
                int innerY = y + (circleDiameter - innerDiameter) / 2;

                if (r.worldTransformed[i][j] == 1) {
                    g.setColor(Color.decode("#8B0000"));
                    g.fillRect(innerX, innerY, innerDiameter, innerDiameter);
                } else {
                    g.setColor(Color.WHITE);
                    g.fillRect(innerX, innerY, innerDiameter, innerDiameter);
                }
            }
        }

        drawTrains(g);
        drawGenerationLabel(g,r);
        repaint();
        revalidate();
    }

    private void drawGenerationLabel(Graphics g, Railroad r) {
        g.setColor(Color.BLACK);
        String generationText = "Generation: " + r.generation + " individual with fitness " + r.fitness + " num of trains that finish "+r.numTrains
                + " and tile pricing "+r.tilePricing;
        Font font = new Font("SansSerif", Font.PLAIN, 20);
        g.setFont(font);

        int labelWidth = g.getFontMetrics().stringWidth(generationText);
        int x = (getWidth() - labelWidth) / 2;

        g.setColor(getBackground());
        g.fillRect(x, 10, (int) (1.5*labelWidth), 30);

        g.setColor(Color.BLACK);
        g.drawString(generationText, x, 30);
    }

    private void drawTrains(Graphics g) {
        int offsetY = calculateOffsetY();
        int offsetX = calculateOffsetX();
        for (int i = 0; i < trains.size(); i++) {
            int s1 = calculateTrainPosition(trains.get(i)[0]) + offsetY;
            int s2 = calculateTrainPosition(trains.get(i)[1]) + offsetX;
            int e1 = calculateTrainPosition(trains.get(i)[2]) + offsetY;
            int e2 = calculateTrainPosition(trains.get(i)[3]) + offsetX;

            g.setColor(Color.BLUE);
            g.fillOval(s2, s1, circleDiameter / 2, circleDiameter / 2);
            g.setColor(Color.BLACK);
            g.drawString((i + 1) + "", s2, s1 + circleDiameter / 2);

            g.setColor(Color.GREEN);
            g.fillOval(e2, e1, circleDiameter / 2, circleDiameter / 2);
            g.setColor(Color.BLACK);
            g.drawString((i + 1) + "", e2, e1 + circleDiameter / 2);
        }
    }



    private int calculateOffsetX() {
        int totalGridWidth = N * circleDiameter;
        return (getWidth() - totalGridWidth) / 2;
    }

    private int calculateOffsetY() {
        int totalGridHeight = N * circleDiameter;
        return (getHeight() - totalGridHeight) / 2;
    }

    private int calculateTrainPosition(int trainPos) {
        return (trainPos * 3 + 1) * circleDiameter + offsetCentre / 2 + circleDiameter / 2;
    }

}




