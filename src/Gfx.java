import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Gfx extends JPanel {
    //public int[][] railroad;
    public List<int[]> trains;
    public int pointWidth = 10;
    public int N;
    List<Railroad> solutions = Population.solutions;
    public static int canvasSize = 800;
    int circleDiameter;
    int generation=0;
    int offset = 10;

    public Gfx(List<int[]> trains){
        //this.solutions = solutions;

        this.trains = trains;
        this.N = Main.N*3;
        circleDiameter = canvasSize/N;

    }


    @Override
    protected void paintComponent(Graphics g) {

        //g.setColor(Color.black);
        for (int s = 0; s < solutions.size() ; s++) {
            int[][] railroad = Main.dict.transform(solutions.get(s).world);
            //System.out.println(s + " curr s");
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    int x = j * circleDiameter + offset;
                    int y = i * circleDiameter + offset;

                    // border
                    g.setColor(Color.GRAY);
                    g.drawRect(x, y, circleDiameter, circleDiameter);

                    // dimensions
                    int innerDiameter = (int) (circleDiameter * 0.95);
                    int innerX = x + (circleDiameter - innerDiameter) / 2;
                    int innerY = y + (circleDiameter - innerDiameter) / 2;

                    if (railroad[i][j] == 1) {
                        g.setColor(Color.decode("#8B0000"));
                        g.fillRect(innerX, innerY, innerDiameter, innerDiameter);
                    } else {
                        g.setColor(Color.WHITE);
                        g.fillRect(innerX, innerY, innerDiameter, innerDiameter);
                    }
                }
            }


            g.setColor(Color.WHITE);
            g.fillRect(0, (N + 1) * circleDiameter, getWidth(), getHeight());

            // Draw the updated generation number
            g.setColor(Color.BLACK);
            g.drawString("generation " + generation, circleDiameter, (N + 2) * circleDiameter);
            generation++;

            for (int i = 0; i < trains.size(); i++) {
                int s1 = (trains.get(i)[0] * 3 + 1) * circleDiameter + offset / 2 + circleDiameter / 2;
                int s2 = (trains.get(i)[1] * 3 + 1) * circleDiameter + offset / 2 + circleDiameter / 2;
                g.setColor(Color.blue);
                g.fillOval(s2, s1, circleDiameter / 2, circleDiameter / 2);
                g.setColor(Color.BLACK);
                g.drawString((i + 1) + "", s2, s1 + circleDiameter / 2);
                int e1 = (trains.get(i)[2] * 3 + 1) * circleDiameter + offset / 2 + circleDiameter / 2;
                int e2 = (trains.get(i)[3] * 3 + 1) * circleDiameter + offset / 2 + circleDiameter / 2;
                g.setColor(Color.green);
                //g.drawOval(e2,e1,circleDiameter/10,circleDiameter/2);
                g.fillOval(e2, e1, circleDiameter / 2, circleDiameter / 2);
                g.setColor(Color.BLACK);
                g.drawString((i + 1) + "", e2, e1 + circleDiameter / 2);

            }
            repaint();
            revalidate();

        }
//        repaint();
//        revalidate();



    }
}
