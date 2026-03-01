import enigma.console.Console;
import enigma.console.TextAttributes;
import java.awt.Color;
import java.awt.event.KeyEvent;

public class Player {

    private int ax, ay;
    private int bx, by;
    private int life = 1000;
    private int score = 0;
    private int mode = 1;


    private Console cn;
    private Maze maze;


    private TextAttributes colorGreen = new TextAttributes(Color.GREEN, Color.BLACK);
    private TextAttributes colorMagenta = new TextAttributes(Color.MAGENTA, Color.BLACK);

    public Player(Console cn, Maze maze) {
        this.cn = cn;
        this.maze = maze;

        ax = 5; ay = 5;
        while(Maze.map[ay][ax] == '#') {
            ax++;
            if(ax >= Maze.COLS - 1) { ax = 1; ay++; }
        }

        bx = ax;
        by = ay;
    }

    public void move(int key) {
        int dx = 0;
        int dy = 0;

        if(key == KeyEvent.VK_LEFT) dx = -1;
        else if(key == KeyEvent.VK_RIGHT) dx = 1;
        else if(key == KeyEvent.VK_UP) dy = -1;
        else if(key == KeyEvent.VK_DOWN) dy = 1;

        if (dx == 0 && dy == 0) return;

        erase();

        if (isValidMove(ax + dx, ay + dy)) {
            ax += dx;
            ay += dy;
        }

        int bDx = dx * mode;
        int bDy = dy * mode;

        if (isValidMove(bx + bDx, by + bDy)) {
            bx += bDx;
            by += bDy;
        }

        draw();
    }

    public void draw() {
        TextAttributes currentColor = (mode == 1) ? colorGreen : colorMagenta;

        cn.getTextWindow().output(bx, by, 'B', currentColor);

        cn.getTextWindow().output(ax, ay, 'A', currentColor);
    }

    public void erase() {
        cn.getTextWindow().output(bx, by, ' ');
        cn.getTextWindow().output(ax, ay, ' ');
    }


    private boolean isValidMove(int x, int y) {
        if (x < 0 || x >= Maze.COLS || y < 0 || y >= Maze.ROWS) return false;
        if (Maze.map[y][x] == '#') return false;

        return true;
    }

    public void switchMode() {
        mode *= -1;
        draw();
    }


    public int getScore() { return score; }
    public int getLife() { return life; }
}