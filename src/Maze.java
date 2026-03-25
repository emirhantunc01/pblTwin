import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import enigma.console.Console;

/**
 * Maze.java
 * Handles the game's map structure, boundaries, and validation.
 * Supports loading layouts from a text file, validating strict boundary requirements,
 * and ensuring that all empty areas are fully connected via a Flood-Fill algorithm.
 */
public class Maze {
    public static final int ROWS = 23;
    public static final int COLS = 53;
    public static char[][] map = new char[ROWS][COLS];

    private Random rnd = new Random();

    public Maze() {
        resetMap();
    }

    public static void resetMap() {
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

    // --- Load from File ---
    public static Maze loadFromFile(String path) throws Exception {
        resetMap(); // Ensure map is clean before loading
        Maze maze = new Maze();
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        int row = 0;
        while ((line = br.readLine()) != null && row < ROWS) {
            for (int col = 0; col < Math.min(line.length(), COLS); col++) {
                map[row][col] = line.charAt(col);
            }
            row++;
        }
        br.close();
        
        if (row == 0) throw new Exception("File is empty or unreadable!");
        return maze;
    }

    // --- Validation ---
    public String validate() {
        // 1) Size check — map is already ROWS x COLS when loaded from file
        // but let's check if the file content was properly filled

        // 2) Outer border check
        for (int x = 0; x < COLS; x++) {
            if (map[0][x] != '#')
                return "Error: Top border missing! (row 0, col " + x + ")";
            if (map[ROWS - 1][x] != '#')
                return "Error: Bottom border missing! (row " + (ROWS - 1) + ", col " + x + ")";
        }
        for (int y = 0; y < ROWS; y++) {
            if (map[y][0] != '#')
                return "Error: Left border missing! (row " + y + ", col 0)";
            if (map[y][COLS - 1] != '#')
                return "Error: Right border missing! (row " + y + ", col " + (COLS - 1) + ")";
        }

        // 3) Connectivity check
        if (!isConnected()) {
            return "Error: There are unreachable empty areas in the maze! All empty spaces must be connected.";
        }

        return null; // Valid
    }

    // --- Flood-Fill Connectivity Check ---
    public boolean isConnected() {
        boolean[][] visited = new boolean[ROWS][COLS];

        // Find the first empty cell
        int startY = -1, startX = -1;
        int totalEmpty = 0;

        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                if (map[y][x] != '#') {
                    totalEmpty++;
                    if (startY == -1) {
                        startY = y;
                        startX = x;
                    }
                }
            }
        }

        if (totalEmpty == 0)
            return true; // Completely wall — considered valid

        // Flood-fill (iterative — no stack overflow risk)
        // We use a manual stack structure (arrays for X and Y) to explore all
        // connected empty cells recursively. If the total explored cells equal
        // the total empty cells counted earlier, it means no areas are isolated.
        int[] stackY = new int[ROWS * COLS];
        int[] stackX = new int[ROWS * COLS];
        int top = 0;
        int reachable = 0;

        stackY[top] = startY;
        stackX[top] = startX;
        top++;
        visited[startY][startX] = true;

        int[] dy = { -1, 1, 0, 0 };
        int[] dx = { 0, 0, -1, 1 };

        while (top > 0) {
            top--;
            int cy = stackY[top];
            int cx = stackX[top];
            reachable++;

            for (int d = 0; d < 4; d++) {
                int ny = cy + dy[d];
                int nx = cx + dx[d];
                if (ny >= 0 && ny < ROWS && nx >= 0 && nx < COLS
                        && !visited[ny][nx] && map[ny][nx] != '#') {
                    visited[ny][nx] = true;
                    stackY[top] = ny;
                    stackX[top] = nx;
                    top++;
                }
            }
        }

        return reachable == totalEmpty;
    }

    // --- Random Maze Generation ---
    public void generateMaze() {
        int attempts = 0;
        do {
            // Reset map
            for (int y = 0; y < ROWS; y++) {
                for (int x = 0; x < COLS; x++) {
                    if (y == 0 || y == ROWS - 1 || x == 0 || x == COLS - 1) {
                        map[y][x] = '#';
                    } else {
                        map[y][x] = ' ';
                    }
                }
            }

            addRandomWalls(4, 8);
            addRandomWalls(6, 6);
            addRandomWalls(20, 4);
            addRandomWalls(5, 3);

            attempts++;
        } while (!isConnected() && attempts < 100);
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
                for (int i = 0; i < ROWS; i++) {
                    for (int j = 0; j < COLS; j++) {
                        backupMap[i][j] = map[i][j];
                    }
                }
                placeWall(startY, startX, length, direction);

                if (!checkArea()) {
                    for (int i = 0; i < ROWS; i++) {
                        for (int j = 0; j < COLS; j++) {
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
            if (c + len >= COLS - 1)
                return false;
        } else {
            if (r + len >= ROWS - 1)
                return false;
        }
        return true;
    }

    private void placeWall(int r, int c, int len, int dir) {
        for (int i = 0; i < len; i++) {
            if (dir == 0)
                map[r][c + i] = '#';
            else
                map[r + i][c] = '#';
        }
    }

    private boolean checkArea() {
        if (!scanArea(2, 3))
            return false;
        if (!scanArea(3, 5))
            return false;
        if (!scanArea(4, 7))
            return false;
        if (!scanArea(6, 15))
            return false;
        return true;
    }

    private boolean scanArea(int size, int maxWalls) {
        for (int y = 0; y <= ROWS - size; y++) {
            for (int x = 0; x <= COLS - size; x++) {
                int count = 0;
                for (int dy = 0; dy < size; dy++) {
                    for (int dx = 0; dx < size; dx++) {
                        if (map[y + dy][x + dx] == '#')
                            count++;
                    }
                }
                if (count > maxWalls)
                    return false;
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