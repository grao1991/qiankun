import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.io.*;
import javax.imageio.*;

public class Main {
    public static void main(String[] args) {
        Controller worker = new Controller();
        MainFrame mainFrame = new MainFrame();
    }
}

class MainFrame extends JFrame {
    static AnswerList alist = new AnswerList();
    static JScrollPane js = new JScrollPane(alist);
    static Grid grid = new Grid();
    static StartButton sbutton = new StartButton();
    static RunButton rbutton = new RunButton();
    MainFrame() {
        this.setTitle("xyq");
        this.setSize(800, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(1000, 100);
        this.setLayout(null);
        this.add(grid);
        this.add(sbutton);
        this.add(rbutton);
        js.setSize(300, 200);
        js.setLocation(450, 70);
        this.add(js);
        this.setVisible(true);
    }
}

class GridButton extends JButton {
    final int x, y;
    GridButton(int x, int y) {
        this.setSize(41, 41);
        this.x = x;
        this.y = y;
        this.setLocation(10 + 41 * x, 10 + 41 * y);
        this.update();
//        this.setBorder(new EtchedBorder(Color.red, Color.black));
        this.setBorder(BorderFactory.createLineBorder(Color.red, 3));
        this.setBorderPainted(false);
    }
    void update() {
        this.setIcon(Controller.bi[Controller.state.map[x][y]]);
        this.updateUI();
    }
}

class State {
    State() {
    }
    State(final State state) {
        for (int i = 0; i < 8; ++i) {
            map[i] = Arrays.copyOf(state.map[i], 8);
        }
    }
    int map[][] = new int[8][8];
    boolean bingo[][] = new boolean[8][8];
    int run() {
        int ret = 0;
        int tmp = 0;
        do {
            tmp = go();
            ret += tmp;
        } while (tmp != 0);
        return ret;
    }
    private boolean same(int x, int y, int z) {
        if (x == 0 || y == 0 || z == 0)
            return false;
        if (x == 6) {
            return y == z || y == 6 || z == 6;
        } else {
            return (y == x || y == 6) && (z == x || z == 6);
        }
    }
    private int go() {
        int ret = 0;
        for (int i = 0; i < 8; ++i) {
            Arrays.fill(bingo[i], false);
        }
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (i < 6 && same(map[i][j], map[i + 1][j], map[i + 2][j])) {
                    bingo[i][j] = true;
                    bingo[i + 1][j] = true;
                    bingo[i + 2][j] = true;
                }
                if (j < 6 && same(map[i][j], map[i][j + 1], map[i][j + 2])) {
                    bingo[i][j] = true;
                    bingo[i][j + 1] = true;
                    bingo[i][j + 2] = true;
                }
            }
        }
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (bingo[i][j]) {
                    ++ret;
                }
            }
        }
        for (int i = 0; i < 8; ++i) {
            int tmp[] = new int[8];
            int now = 7;
            for (int j = 7; j >= 0; --j) {
                if (!bingo[i][j]) {
                    tmp[now--] = map[i][j];
                }
            }
            map[i] = Arrays.copyOf(tmp, 8);
        }
        return ret;
    }
}

class Controller {
    static State state = new State();
    static ImageIcon bi[] = new ImageIcon[7];
    Controller() {
        Random rand = new Random();
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                state.map[i][j] = rand.nextInt(5) + 1;
            }
        }
        try {
            bi[0] = new ImageIcon("../res/init.jpg");
            bi[1] = new ImageIcon("../res/black.jpg");
            bi[2] = new ImageIcon("../res/blue.jpg");
            bi[3] = new ImageIcon("../res/red.jpg");
            bi[4] = new ImageIcon("../res/violet.jpg");
            bi[5] = new ImageIcon("../res/yellow.jpg");
            bi[6] = new ImageIcon("../res/all.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Grid extends JPanel {
    static GridButton data[][] = new GridButton[8][8];
    Grid() {
        this.setLayout(null);
        int x = 0;
        int y = 0;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                data[i][j] = new GridButton(i, j);
                this.add(data[i][j]);
            }
        }
        this.setSize(420, 420);
        this.updateUI();
    }
    void clearBorder() {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                data[i][j].setBorderPainted(false);
            }
        }
    }
    void setBorder(int x, int y) {
        data[x][y].setBorderPainted(true);
    }
}

class StartButton extends JButton implements ActionListener{
    StartButton() {
        this.setSize(90, 30);
        this.setLocation(450, 20);
        this.setText("Go");
        this.addActionListener(this);
    }
/*    private boolean same(int x, int y) {
        final int delta = 10;
        if (Math.abs(((x >> 16) & 0xff) - ((y >> 16) & 0xff)) > delta)
            return false;
        if (Math.abs(((x >> 8) & 0xff) - ((y >> 8) & 0xff)) > delta)
            return false;
        if (Math.abs((x & 0xff) - (y & 0xff)) > delta)
            return false;
        return true;
    }*/
    private boolean match(int x, int y, final BufferedImage aim, final BufferedImage tot) {
        int ret = 0;
        int width = tot.getWidth();
        int height = tot.getHeight();
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                if (x + i < width && y + j < height) {
                    if (aim.getRGB(i, j) == tot.getRGB(x + i, y + j)) {
                        ++ret;
                    } 
                }
            }
        }
        return ret == 25;
    }
    private boolean isColor(int x, int y, BufferedImage color, BufferedImage tot) {
        int width = tot.getWidth();
        int height = tot.getHeight();
        int ret = 0;
        int[] dx = {8, 8, 30, 30};
        int[] dy = {8, 30, 8, 30};
        Random rand = new Random();
        for (int i = 0; i < 4; ++i) {
            if (x + dx[i] < width && y + dy[i] < height) {
                if (color.getRGB(dx[i], dy[i]) == tot.getRGB(x + dx[i], y + dy[i])) {
                    ++ret;
                }
            }
        }
        return ret > 2;
    }
    private void getImage() throws Exception {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int width = toolkit.getScreenSize().width;
        int height = toolkit.getScreenSize().height;
        BufferedImage image = (new Robot()).createScreenCapture(new Rectangle(0, 0, width, height));
        System.out.println(image.getWidth() + " " + image.getHeight());
        BufferedImage black = ImageIO.read(new File("../res/black.bmp"));
        BufferedImage blue = ImageIO.read(new File("../res/blue.bmp"));
        BufferedImage red = ImageIO.read(new File("../res/red.bmp"));
        BufferedImage violet = ImageIO.read(new File("../res/violet.bmp"));
        BufferedImage yellow = ImageIO.read(new File("../res/yellow.bmp"));
        BufferedImage all = ImageIO.read(new File("../res/all.bmp"));
        int minI = width;
        int minJ = height;
        for (int i = 0; i < minI; ++i) {
            for (int j = 0; j < minJ; ++j) {
                if (match(i, j, black, image) || match(i, j, blue, image) || match(i, j, red, image) || match(i, j, violet, image) || match(i, j, yellow, image) || match(i, j, all, image)) {
                    System.out.println(i + " " + j);
                    minI = Math.min(i, minI);
                    minJ = Math.min(j, minJ);
                }
            }
        }
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                int nowI = minI + i * 39;
                int nowJ = minJ + j * 39;
                if (isColor(nowI, nowJ, black, image)) {
                    Controller.state.map[i][j] = 1;
                } else if (isColor(nowI, nowJ, blue, image)) {
                    Controller.state.map[i][j] = 2;
                } else if (isColor(nowI, nowJ, red, image)) {
                    Controller.state.map[i][j] = 3;
                } else if (isColor(nowI, nowJ, violet, image)) {
                    Controller.state.map[i][j] = 4;
                } else if (isColor(nowI, nowJ, yellow, image)) {
                    Controller.state.map[i][j] = 5;
                } else if (isColor(nowI, nowJ, all, image)) {
                    Controller.state.map[i][j] = 6;
                } else {
                    System.out.println("xxx");
                }
                Grid.data[i][j].update();
            }
        }
    }
    public void actionPerformed(ActionEvent event) {
        try {
            this.getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class RunButton extends JButton implements ActionListener {
    RunButton() {
        this.setSize(90, 30);
        this.setLocation(660, 20);
        this.setText("Solve");
        this.addActionListener(this);
    }
    public void actionPerformed(ActionEvent event) {
        Solver.run();
    }
}

class AnswerList extends JList implements ListSelectionListener{
    Vector array = new Vector();
    Vector<Integer> answer = new Vector<Integer>();
    AnswerList() {
        this.setListData(array);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.addListSelectionListener(this);
    }
    void add(int x0, int y0, int x1, int y1, int num) {
        array.addElement(String.format("(%d, %d) --- (%d, %d)    [%d]", x0, y0, x1, y1, num));
        answer.addElement(x0 * 1 + y0 * 8 + x1 * 64 + y1 * 512);
        this.setListData(array);
    }
    void clear() {
        array.clear();
        answer.clear();
        this.setListData(array);
    }
    public void valueChanged(ListSelectionEvent e) {
        int value = answer.elementAt(this.getSelectedIndex());
        int x0 = value & 7;
        value >>= 3;
        int y0 = value & 7;
        value >>= 3;
        int x1 = value & 7;
        value >>= 3;
        int y1 = value;
        MainFrame.grid.clearBorder();
        MainFrame.grid.setBorder(x0, y0);
        MainFrame.grid.setBorder(x1, y1);
    }
}

class Solver {
    static void run() {
        MainFrame.alist.clear();
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (i != 7) {
                    State tmpState = new State(Controller.state);
                    int tmp = tmpState.map[i][j];
                    tmpState.map[i][j] = tmpState.map[i + 1][j];
                    tmpState.map[i + 1][j] = tmp;
                    int cnt = tmpState.run();
                    if (cnt > 7) {
                        MainFrame.alist.add(i, j, i + 1, j, cnt);
                        System.out.println(i + " " + j + " " + (i + 1) + " " + j + " " + cnt);
                    }
                }
                if (j != 7) {
                    State tmpState = new State(Controller.state);
                    int tmp = tmpState.map[i][j];
                    tmpState.map[i][j] = tmpState.map[i][j + 1];
                    tmpState.map[i][j + 1] = tmp;
                    int cnt = tmpState.run();
                    if (cnt > 7) {
                        MainFrame.alist.add(i, j, i, j + 1, cnt);
                        System.out.println(i + " " + j + " " + i + " " + (j + 1) + " " + cnt);
                    }
                }
            }
        }
    }
}

