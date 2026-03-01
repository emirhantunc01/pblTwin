import java.util.Random;
import enigma.console.Console;

public class Maze {
    public static final int ROWS = 23;
    public static final int COLS = 53;
    public static char[][] map = new char[ROWS][COLS];

    private  Random rnd = new Random();

    public Maze() {
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                if (y == 0 || y == ROWS - 1 || x == 0 || x == COLS - 1) {
                    map[y][x] = '#';
                } else {
                    map[y][x] = ' ';
                }
            }
        }
    }

    public void generateMaze() {
        addRandomWalls(4, 8);
        addRandomWalls(6, 6);
        addRandomWalls(20, 4);
        addRandomWalls(5, 3);
    }

    private void addRandomWalls(int count, int length) {
        int placed = 0;
        int attempts = 0;

        char[][] backupMap = new char[ROWS][COLS];

        while (placed < count && attempts < 2000) {
            attempts++;
            int direction = rnd.nextInt(2);
            int startY = rnd.nextInt(ROWS - 2) + 1;
            int startX = rnd.nextInt(COLS - 2) + 1;

            if (isValidWallPlacement(startY, startX, length, direction)) {
                for(int i=0; i<ROWS; i++) {
                    for(int j=0; j<COLS; j++) {
                        backupMap[i][j] = map[i][j];
                    }
                }
                placeWall(startY, startX, length, direction);

                if (!checkArea()) {
                    for(int i=0; i<ROWS; i++) {
                        for(int j=0; j<COLS; j++) {
                            map[i][j] = backupMap[i][j];
                        }
                    }
                } else {
                    placed++;
                }
            }
        }
    }
    private boolean isValidWallPlacement(int r, int c, int len, int dir) {
        if (dir == 0) {
            if (c + len >= COLS - 1) return false;
        } else {
            if (r + len >= ROWS - 1) return false;
        }
        return true;
    }
    private void placeWall(int r, int c, int len, int dir) {
        for (int i = 0; i < len; i++) {
            if (dir == 0) map[r][c + i] = '#';
            else map[r + i][c] = '#';
        }
    }
    private boolean checkArea() {

        if (!scanArea(2, 3)) return false;
        if (!scanArea(3, 5)) return false;
        if (!scanArea(4, 7)) return false;
        if (!scanArea(6, 15)) return false;

        return true;
    }

    private boolean scanArea(int size, int maxWalls) {
        for (int y = 0; y <= ROWS - size; y++) {
            for (int x = 0; x <= COLS - size; x++) {
                int count = 0;
                for (int dy = 0; dy < size; dy++) {
                    for (int dx = 0; dx < size; dx++) {
                        if (map[y + dy][x + dx] == '#') count++;
                    }
                }
                if (count > maxWalls) return false;
            }
        }
        return true;
    }

    public void draw(Console cn) {
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                cn.getTextWindow().output(x, y, map[y][x]);
            }
        }
    }
}