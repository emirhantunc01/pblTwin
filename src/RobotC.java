import enigma.console.Console;
import enigma.console.TextAttributes;
import java.awt.Color;
import java.util.Random;

/**
 * RobotC.java
 * Represents the intelligent computer-controlled enemy (Type C).
 * This robot actively searches for the nearest uncollected treasure
 * using the Manhattan distance algorithm and navigates towards it.
 * If no treasures are present, it reverts to random movement.
 */
public class RobotC {
    // --- Constants ---
    public static final int RANDOM_MOVE_CHANCE = 25; // 25% chance to change direction when idle
    public static final int INITIAL_MAX_HP = 1000;
    private int x, y;
    private int direction;
    private int life = INITIAL_MAX_HP;
    private int score = 0;
    private Console cn;
    private Random rnd = new Random();
    private TextAttributes colorC = new TextAttributes(Color.CYAN, Color.BLACK);

    public RobotC(Console cn, int startX, int startY) {
        this.cn = cn;
        this.x = startX;
        this.y = startY;
        this.direction = rnd.nextInt(4);
    }

    public void draw() {
        cn.getTextWindow().output(x, y, 'C', colorC);
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

    private boolean isValidMove(int nx, int ny, RobotX[] robots, int robotCount,
                                 RobotC[] robotsC, int robotCCount) {
        if (nx < 0 || nx >= Maze.COLS || ny < 0 || ny >= Maze.ROWS) return false;
        if (Maze.map[ny][nx] == '#') return false;

        // Collision with other RobotXs
        for (int i = 0; i < robotCount; i++) {
            if (robots[i].getX() == nx && robots[i].getY() == ny) return false;
        }
        // Collision with other RobotCs (excluding self)
        for (int i = 0; i < robotCCount; i++) {
            if (robotsC[i] == this) continue;
            if (robotsC[i].getX() == nx && robotsC[i].getY() == ny) return false;
        }

        return true;
    }

    public void move(Item[] items, int itemCount, RobotX[] robots, int robotCount,
                     RobotC[] robotsC, int robotCCount) {

        // Find nearest treasure using Manhattan distance ('1', '2', '3')
        // The Manhattan distance (or Taxicab geometry) calculates the distance 
        // between two points in a grid based on a strictly horizontal/vertical path:
        // distance = |x1 - x2| + |y1 - y2|
        // Here we iterate all items to find the global minimum distance, essentially
        // acting as a localized pathfinding heuristic to pick a target goal.
        int bestDist = Integer.MAX_VALUE;
        int targetX = -1;
        int targetY = -1;

        for (int i = 0; i < itemCount; i++) {
            char type = items[i].getType();
            if (type == '1' || type == '2' || type == '3') {
                int dist = Math.abs(x - items[i].getX()) + Math.abs(y - items[i].getY());
                if (dist < bestDist) {
                    bestDist = dist;
                    targetX = items[i].getX();
                    targetY = items[i].getY();
                }
            }
        }

        int nextX = x;
        int nextY = y;

        if (targetX != -1) {
            // Directional movement towards target
            int dx = 0;
            int dy = 0;
            if (targetX > x) dx = 1;
            else if (targetX < x) dx = -1;
            if (targetY > y) dy = 1;
            else if (targetY < y) dy = -1;

            // Try preferred direction first, if invalid try others sequentially
            int[][] tries = {
                {dx, 0}, {0, dy}, {0, -dy}, {-dx, 0}
            };

            boolean moved = false;
            for (int[] t : tries) {
                if (t[0] == 0 && t[1] == 0) continue;
                nextX = x + t[0];
                nextY = y + t[1];
                if (isValidMove(nextX, nextY, robots, robotCount, robotsC, robotCCount)) {
                    erase();
                    x = nextX;
                    y = nextY;
                    draw();
                    moved = true;
                    break;
                }
            }
            if (!moved) {
                // Stay in place
            }
        } else {
            // If no treasure, random movement like RobotX
            if (rnd.nextInt(100) < RANDOM_MOVE_CHANCE) {
                direction = rnd.nextInt(4);
            }

            if (direction == 0) nextX = x + 1;
            else if (direction == 1) nextY = y + 1;
            else if (direction == 2) nextX = x - 1;
            else if (direction == 3) nextY = y - 1;

            if (isValidMove(nextX, nextY, robots, robotCount, robotsC, robotCCount)) {
                erase();
                x = nextX;
                y = nextY;
                draw();
            } else {
                direction = rnd.nextInt(4);
            }
        }
    }
}
