import enigma.console.Console;
import enigma.console.TextAttributes;
import java.awt.Color;

public class Laser {

    public static final int MAX_BLOCKS = 300;
    public static final int BLOCK_LIFETIME = 100;

    private int[] blockX    = new int[MAX_BLOCKS];
    private int[] blockY    = new int[MAX_BLOCKS];
    private int[] blockLife = new int[MAX_BLOCKS];
    private int blockCount = 0;

    private int[] pathX = new int[MAX_BLOCKS];
    private int[] pathY = new int[MAX_BLOCKS];
    private int pathLength = 0;
    private int pathIndex = 0;

    private boolean firing = false;
    private int packedCount = 0;
    private Console cn;
    private TextAttributes colorLaser = new TextAttributes(Color.RED, Color.BLACK);

    public Laser(Console cn) {
        this.cn = cn;
    }

    public void addPacked() {
        packedCount++;
    }

    public int getPackedCount() {
        return packedCount;
    }

    public boolean fire(int ax, int ay, int bx, int by) {
        if (packedCount <= 0) {
            return false;
        }
        if (firing) {
            return false;
        }
        if (ax == bx && ay == by) {
            return false;
        }

        packedCount--;
        firing = true;
        pathLength = 0;
        pathIndex = 0;

        calculatePath(ax, ay, bx, by);
        return true;
    }

    private void calculatePath(int ax, int ay, int bx, int by) {
        int dx = bx - ax;
        int dy = by - ay;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        if (steps == 0) return;

        double xInc = (double) dx / steps;
        double yInc = (double) dy / steps;

        double x = ax;
        double y = ay;
        pathLength = 0;

        for (int i = 0; i <= steps; i++) {
            int px = (int) Math.round(x);
            int py = (int) Math.round(y);

            boolean isA = (px == ax && py == ay);
            boolean isB = (px == bx && py == by);

            if (!isA && !isB && pathLength < MAX_BLOCKS) {
                pathX[pathLength] = px;
                pathY[pathLength] = py;
                pathLength++;
            }

            x += xInc;
            y += yInc;
        }
    }

    public void update() {
        if (firing && pathIndex < pathLength) {
            int px = pathX[pathIndex];
            int py = pathY[pathIndex];
            pathIndex++;

            if (Maze.map[py][px] != '#' && blockCount < MAX_BLOCKS) {
                blockX[blockCount] = px;
                blockY[blockCount] = py;
                blockLife[blockCount] = BLOCK_LIFETIME;
                blockCount++;
                cn.getTextWindow().output(px, py, '+', colorLaser);
            }

            if (pathIndex >= pathLength) {
                firing = false;
            }
        }

        for (int i = 0; i < blockCount; i++) {
            blockLife[i]--;

            if (blockLife[i] <= 0) {
                cn.getTextWindow().output(blockX[i], blockY[i], ' ');
                blockCount--;
                blockX[i]    = blockX[blockCount];
                blockY[i]    = blockY[blockCount];
                blockLife[i] = blockLife[blockCount];
                i--;
            }
        }
    }

    public boolean isNeighborToLaser(int x, int y) {
        for (int i = 0; i < blockCount; i++) {
            int diffX = Math.abs(blockX[i] - x);
            int diffY = Math.abs(blockY[i] - y);

            if ((diffX == 1 && diffY == 0) || (diffX == 0 && diffY == 1)) {
                return true;
            }
        }
        return false;
    }

    public int countNeighborLasers(int x, int y) {
        int count = 0;
        for (int i = 0; i < blockCount; i++) {
            int diffX = Math.abs(blockX[i] - x);
            int diffY = Math.abs(blockY[i] - y);

            if ((diffX == 1 && diffY == 0) || (diffX == 0 && diffY == 1)) {
                count++;
            }
        }
        return count;
    }

    public int getBlockCount() {
        return blockCount;
    }

    public boolean isFiring() {
        return firing;
    }
}
