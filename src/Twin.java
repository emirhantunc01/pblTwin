import enigma.core.Enigma;
import enigma.event.TextMouseEvent;
import enigma.event.TextMouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import enigma.console.Console;
import java.util.Random;

public class Twin {
    public Console cn = Enigma.getConsole("Twins Game", 100, 30, 20);
    public TextMouseListener tmlis;
    public KeyListener klis;

    public int mousepr;
    public int mousex, mousey;
    public int keypr;
    public int rkey;

    public Maze maze;
    public Player player;

    public static final int MAX_ITEMS = 100;
    public static final int MAX_ROBOTS = 50;

    public Item[] items = new Item[MAX_ITEMS];
    public int itemCount = 0;

    public RobotX[] robots = new RobotX[MAX_ROBOTS];
    public int robotCount = 0;

    int loopCounter = 0;
    Random rnd = new Random();

    public Twin() throws Exception {


        tmlis = new TextMouseListener() {
            public void mouseClicked(TextMouseEvent arg0) {}
            public void mousePressed(TextMouseEvent arg0) {
                if(mousepr==0) { mousepr=1; mousex=arg0.getX(); mousey=arg0.getY(); }
            }
            public void mouseReleased(TextMouseEvent arg0) {}
        };
        cn.getTextWindow().addTextMouseListener(tmlis);

        klis = new KeyListener() {
            public void keyTyped(KeyEvent e) {}
            public void keyPressed(KeyEvent e) {
                if(keypr==0) { keypr=1; rkey=e.getKeyCode(); }
            }
            public void keyReleased(KeyEvent e) {}
        };
        cn.getTextWindow().addKeyListener(klis);

        maze = new Maze();
        maze.generateMaze();
        maze.draw(cn);

        player = new Player(cn, maze);
        player.draw();

        for(int i=0; i<10; i++) {
            addRandomInput();
        }

        // --- Game Loop ---
        while(true) {

            if(keypr==1) {
                if(rkey == KeyEvent.VK_M) player.switchMode();
                else player.move(rkey);
                keypr=0;
            }

            loopCounter++;

            if(loopCounter % 4 == 0) {

                for(int i = 0; i < robotCount; i++) {
                    robots[i].move();
                }
            }

            if(loopCounter % 20 == 0) {
                addRandomInput();
                cn.getTextWindow().setCursorPosition(55, 0);
                cn.getTextWindow().output("Time: " + (loopCounter / 20));
            }

            Thread.sleep(50);
        }
    }


    public void addRandomInput() {

        if (itemCount >= MAX_ITEMS || robotCount >= MAX_ROBOTS) return;

        int x, y;
        do {
            x = rnd.nextInt(Maze.COLS - 2) + 1;
            y = rnd.nextInt(Maze.ROWS - 2) + 1;
        } while (Maze.map[y][x] == '#' || isOccupied(x, y));

        int chance = rnd.nextInt(11);

        // Hazineler ve Lazer
        if (chance <= 1) spawnItem(x, y, '1');      // 1-Hazine
        else if (chance <= 3) spawnItem(x, y, '2'); // 2-Hazine
        else if (chance <= 5) spawnItem(x, y, '3'); // 3-Hazine
        else if (chance <= 8) spawnItem(x, y, '@'); // Lazer


        else if (chance == 9) spawnRobotX(x, y);
        else spawnRobotX(x, y);                     // X-Robot
    }

    // Hazine Ekleme
    private void spawnItem(int x, int y, char type) {
        if (itemCount < MAX_ITEMS) {
            Item item = new Item(x, y, type, cn);
            items[itemCount] = item;
            itemCount++;
            item.draw();
        }
    }

    // Robot Ekleme
    private void spawnRobotX(int x, int y) {
        if (robotCount < MAX_ROBOTS) {
            RobotX bot = new RobotX(cn, x, y);
            robots[robotCount] = bot;
            robotCount++;
            bot.draw();
        }
    }

    // Koordinat Dolu mu
    private boolean isOccupied(int x, int y) {
        // Hazineleri kontrol et
        for (int i = 0; i < itemCount; i++) {
            if (items[i].getX() == x && items[i].getY() == y) return true;
        }
        // Robotları kontrol et
        for (int i = 0; i < robotCount; i++) {
            if (robots[i].getX() == x && robots[i].getY() == y) return true;
        }
        // Oyuncu kontrolü (Şimdilik es geçildi)
        return false;
    }
}