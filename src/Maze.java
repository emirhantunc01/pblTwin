import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import enigma.console.Console;

public class Maze {
    public static final int ROWS = 23;
    public static final int COLS = 53;
    public static char[][] map = new char[ROWS][COLS];

    private Random rnd = new Random();

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

    // --- Dosyadan Yükleme ---
    public static Maze loadFromFile(String path) throws Exception {
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
        return maze;
    }

    // --- Doğrulama ---
    public String validate() {
        // 1) Boyut kontrolü — dosyadan yüklendiğinde map zaten ROWS x COLS
        // ama dosya içeriğinin doğru doldurulup doldurulmadığını kontrol edelim

        // 2) Dış çerçeve kontrolü
        for (int x = 0; x < COLS; x++) {
            if (map[0][x] != '#')
                return "Hata: Ust cerceve eksik! (satir 0, sutun " + x + ")";
            if (map[ROWS - 1][x] != '#')
                return "Hata: Alt cerceve eksik! (satir " + (ROWS - 1) + ", sutun " + x + ")";
        }
        for (int y = 0; y < ROWS; y++) {
            if (map[y][0] != '#')
                return "Hata: Sol cerceve eksik! (satir " + y + ", sutun 0)";
            if (map[y][COLS - 1] != '#')
                return "Hata: Sag cerceve eksik! (satir " + y + ", sutun " + (COLS - 1) + ")";
        }

        // 3) Bağlantılılık kontrolü
        if (!isConnected()) {
            return "Hata: Labirentte erisilemeyen bos alanlar var! Tum bosluklar baglantili olmali.";
        }

        return null; // Geçerli
    }

    // --- Flood-Fill Bağlantılılık Kontrolü ---
    public boolean isConnected() {
        boolean[][] visited = new boolean[ROWS][COLS];

        // İlk boş hücreyi bul
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
            return true; // Tamamen duvar — geçerli sayılır

        // Flood-fill (iteratif — stack overflow riski yok)
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

    // --- Rastgele Labirent Oluşturma ---
    public void generateMaze() {
        int attempts = 0;
        do {
            // Haritayı sıfırla
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