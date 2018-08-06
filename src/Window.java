import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {
    private static int WIDTH = 1400, HEIGHT = 800;
    private static int AMOUNT = 15000;
    private static int BORDER = 2;
    private JButton restart, solve;
    private JPanel panel;

    private Stars stars;
    private boolean solved;

    public Window() {
        super("Spot the difference!");
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        restart = new JButton("Randomize");
        restart.setBounds(0, 0, WIDTH/2, 100);

        solve = new JButton("Solution");
        solve.setBounds(WIDTH/2, 0, WIDTH/2, 100);

        panel = new JPanel(null);
        this.setContentPane(panel);
        panel.add(restart);
        panel.add(solve);

        setVisible(true);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {}
        Insets i = getInsets();
        setSize(i.left+i.right + WIDTH, i.top + i.bottom + HEIGHT);

        restart.addActionListener((e)-> {
                stars = new Stars(WIDTH / 2, (HEIGHT-100), AMOUNT);
                solved = false;
                redraw();
            }
        );

        solve.addActionListener((e) -> {
            solved = true;
            redraw();
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (stars == null) return;
                int y = mouseEvent.getY() - 100;
                int x = mouseEvent.getX();
                if (x > WIDTH/2) x -= WIDTH/2;

                if (Math.max(Math.abs(x-stars.getMissingStar()[0]), Math.abs(y-stars.getMissingStar()[1])) < 10) {
                    solved = true;
                    redraw();
                }
            }
        });

        stars = new Stars(WIDTH / 2, (HEIGHT-100), AMOUNT);
        solved = false;
        redraw();
    }

    public void redraw() {
        if (stars != null) {
            Graphics g1 = panel.getGraphics();
            g1.drawImage(stars.getLeft(), 0, 100, WIDTH / 2, HEIGHT - 100, null);
            g1.drawImage(stars.getRight(), WIDTH / 2, 100, WIDTH / 2, HEIGHT - 100, null);

            g1.setColor(Color.RED);
            g1.drawLine(WIDTH / 2, 100, WIDTH / 2, HEIGHT);

            if (solved) {
                g1.setColor(Color.GREEN);
                g1.drawRect(stars.missingStar[0] - 10, stars.missingStar[1] + 100 - 10, 20, 20);
                g1.drawRect(WIDTH / 2 + stars.missingStar[0] - 10, stars.missingStar[1] + 100 - 10, 20, 20);
            }
        }
    }

    private static class Stars {
        private List<int[]> stars;
        private int[] missingStar;
        private BufferedImage left, right;

        public Stars(int width, int height, int amount) {
            stars = new ArrayList<>();

            left = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
            right = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

            Graphics g1 = left.getGraphics();
            Graphics g2 = right.getGraphics();

            g1.setColor(Color.black);
            g2.setColor(Color.black);
            g1.fillRect(0, 0, width, height);
            g2.fillRect(0, 0, width, height);

            g1.setColor(Color.white);
            g2.setColor(Color.white);

            for (int i = 0; i <= amount; i++) {
                int[] newPos;
                do {
                    newPos = new int[] {(int) (Math.random()*width), (int) (Math.random()*height)};
                } while(newPos[0] < BORDER || newPos[1] < BORDER || newPos[0] >= width - BORDER || newPos[1] >= height - BORDER || minDistance(newPos, stars) < 2 * BORDER);

                int size = (int) (Math.random() * (BORDER-1))+1;

                g1.fillOval(newPos[0]-size, newPos[1]-size, 2*size, 2* size);

                if (i == amount) {
                    missingStar = newPos;
                } else {
                    g2.fillOval(newPos[0]-size, newPos[1]-size, 2*size, 2*size);
                    stars.add(newPos);
                }
            }
        }

        public BufferedImage getLeft() {
            return left;
        }

        public BufferedImage getRight() {
            return right;
        }

        public int[] getMissingStar() {
            return missingStar;
        }

        private static double minDistance(int[] newPos, List<int[]> stars) {
            double minDistance = 3*BORDER;

            for (int i = 0; i < stars.size(); i++) {
                double d = Math.max( Math.abs(stars.get(i)[0] - newPos[0]), Math.abs(stars.get(i)[1] - newPos[1]));
                if (d < minDistance) minDistance = d;
            }

            return minDistance;
        }
    }

    public static void main(String[] args){
        try {
            if (args.length > 0) WIDTH = Integer.valueOf(args[0]);
            if (args.length > 1) HEIGHT = Integer.valueOf(args[1]);
            if (args.length > 2) AMOUNT = Integer.valueOf(args[2]);
            if (args.length > 3) BORDER = Integer.valueOf(args[3]);
        } catch (Exception e) {
            System.out.println("Error reading arguments. Proceeding with default values.");
        }

        new Window();

    }
}
