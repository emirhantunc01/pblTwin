import java.util.Random;
import enigma.console.Console;
import enigma.console.TextAttributes;
import java.awt.Color;

public class RobotX {
    private int x, y;
    private int direction;
    private int life = 1000;
    private int score = 0;
    private Console cn;
    private Random rnd = new Random();
    private TextAttributes colorX = new TextAttributes(Color.YELLOW, Color.BLACK);

//
    public RobotX(Console cn) {
        this.cn = cn;
        do {
            x = rnd.nextInt(Maze.COLS - 2) + 1;
            y = rnd.nextInt(Maze.ROWS - 2) + 1;
        } while (Maze.map[y][x] == '#');
        direction = rnd.nextInt(4);
    }


    public RobotX(Console cn, int startX, int startY) {
        this.cn = cn;
        this.x = startX;
        this.y = startY;
        this.direction = rnd.nextInt(4);
    }

    public void move() {

        if (rnd.nextInt(100) < 25) {
            direction = rnd.nextInt(4);
        }

        int nextX = x;
        int nextY = y;

        if (direction == 0) nextX++;
        else if (direction == 1) nextY++;
        else if (direction == 2) nextX--;
        else if (direction == 3) nextY--;

        if (isValidMove(nextX, nextY)) {
            erase();
            x = nextX;
            y = nextY;
            draw();
        } else {
            direction = rnd.nextInt(4);
        }
    }

    private boolean isValidMove(int nx, int ny) {
        if (nx < 0 || nx >= Maze.COLS || ny < 0 || ny >= Maze.ROWS) return false;
        if (Maze.map[ny][nx] == '#') return false;
        return true;
    }

    public void draw() {
        cn.getTextWindow().output(x, y, 'X', colorX);
    }

    public void erase() {
        cn.getTextWindow().output(x, y, ' ');
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getLife() { return life; }
    public int getScore() { return score; }

    public void addScore(int points) {
        score += points;
    }

    public void addLife(int amount) {
        life += amount;
    }
}