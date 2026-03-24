import enigma.console.Console;
import enigma.console.TextAttributes;
import java.awt.Color;
import java.util.Random;

public class RobotC {
    private int x, y;
    private int direction;
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

    private boolean isValidMove(int nx, int ny) {
        if (nx < 0 || nx >= Maze.COLS || ny < 0 || ny >= Maze.ROWS) return false;
        if (Maze.map[ny][nx] == '#') return false;
        return true;
    }

    public void move(int playerAX, int playerAY, int playerBX, int playerBY) {
        // Hem A hem B'ye olan Öklid mesafesini hesapla
        double distA = Math.sqrt((x - playerAX) * (x - playerAX) + (y - playerAY) * (y - playerAY));
        double distB = Math.sqrt((x - playerBX) * (x - playerBX) + (y - playerBY) * (y - playerBY));

        // Yakın olanı hedef al
        int targetX, targetY;
        double dist;
        if (distA <= distB) {
            targetX = playerAX;
            targetY = playerAY;
            dist = distA;
        } else {
            targetX = playerBX;
            targetY = playerBY;
            dist = distB;
        }

        int nextX = x;
        int nextY = y;

        if (dist <= 5.0) {
            // Hedeften uzaklaşma modu
            int dx = 0;
            int dy = 0;
            if (x - targetX > 0) dx = 1;
            else if (x - targetX < 0) dx = -1;
            if (y - targetY > 0) dy = 1;
            else if (y - targetY < 0) dy = -1;

            // Önce tercih edilen yönde dene, geçersizse diğerlerini sırayla dene
            int[][] tries = {
                {dx, 0}, {0, dy}, {-dx, 0}, {0, -dy}
            };

            boolean moved = false;
            for (int[] t : tries) {
                if (t[0] == 0 && t[1] == 0) continue;
                nextX = x + t[0];
                nextY = y + t[1];
                if (isValidMove(nextX, nextY)) {
                    erase();
                    x = nextX;
                    y = nextY;
                    draw();
                    moved = true;
                    break;
                }
            }
            // Hiçbir yön geçerli değilse yerinde kal
            if (!moved) {
                // yerinde kal
            }
        } else {
            // RobotX gibi rastgele hareket
            if (rnd.nextInt(100) < 25) {
                direction = rnd.nextInt(4);
            }

            if (direction == 0) nextX = x + 1;
            else if (direction == 1) nextY = y + 1;
            else if (direction == 2) nextX = x - 1;
            else if (direction == 3) nextY = y - 1;

            if (isValidMove(nextX, nextY)) {
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
