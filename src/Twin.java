import enigma.core.Enigma;
import enigma.event.TextMouseEvent;
import enigma.event.TextMouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import enigma.console.Console;
import java.util.Random;
import java.util.Scanner;

public class Twin {
    public Console cn;
    public TextMouseListener tmlis;
    public KeyListener klis;

    public int mousepr;
    public int mousex, mousey;
    public int keypr;
    public int rkey;

    public Maze maze;
    public Player player;
    public Laser laser;

    public static final int MAX_ITEMS = 100;
    public static final int MAX_ROBOTS = 50;

    public Item[] items = new Item[MAX_ITEMS];
    public int itemCount = 0;

    public RobotX[] robots = new RobotX[MAX_ROBOTS];
    public int robotCount = 0;

    int loopCounter = 0;
    Random rnd = new Random();

    public Twin() throws Exception {

        // --- Başlangıç Menüsü ---
        Scanner scanner = new Scanner(System.in);
        boolean mazeReady = false;

        while (!mazeReady) {
            System.out.println("========================================");
            System.out.println("           TWINS GAME");
            System.out.println("========================================");
            System.out.println("  1 - Rastgele Labirent Olustur");
            System.out.println("  2 - Dosyadan Labirent Yukle (.txt)");
            System.out.println("========================================");
            System.out.print("  Seciminiz (1/2): ");

            int choice = 0;
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            }
            scanner.nextLine(); // satır sonu temizle

            if (choice == 1) {
                maze = new Maze();
                maze.generateMaze();
                System.out.println("  Rastgele labirent olusturuldu!");
                mazeReady = true;

            } else if (choice == 2) {
                System.out.print("  Dosya yolunu girin: ");
                String path = scanner.nextLine().trim();

                try {
                    maze = Maze.loadFromFile(path);
                    String error = maze.validate();
                    if (error != null) {
                        System.out.println("  " + error);
                        System.out.println("  Lutfen gecerli bir dosya ile tekrar deneyin.\n");
                    } else {
                        System.out.println("  Labirent basariyla yuklendi!");
                        mazeReady = true;
                    }
                } catch (Exception e) {
                    System.out.println("  Hata: Dosya okunamadi! (" + e.getMessage() + ")");
                    System.out.println("  Lutfen gecerli bir dosya yolu girin.\n");
                }

            } else {
                System.out.println("  Gecersiz secim! Lutfen 1 veya 2 girin.\n");
            }
        }
        scanner.close();
        // --- Enigma---
        cn = Enigma.getConsole("Twins Game", 100, 30, 20);

        tmlis = new TextMouseListener() {
            public void mouseClicked(TextMouseEvent arg0) {
            }

            public void mousePressed(TextMouseEvent arg0) {
                if (mousepr == 0) {
                    mousepr = 1;
                    mousex = arg0.getX();
                    mousey = arg0.getY();
                }
            }

            public void mouseReleased(TextMouseEvent arg0) {
            }
        };
        cn.getTextWindow().addTextMouseListener(tmlis);

        klis = new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                if (keypr == 0) {
                    keypr = 1;
                    rkey = e.getKeyCode();
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        };
        cn.getTextWindow().addKeyListener(klis);

        maze.draw(cn);

        player = new Player(cn, maze);
        player.draw();

        laser = new Laser(cn);

        for (int i = 0; i < 10; i++) {
            addRandomSpawn();
        }

        // --- Game Loop ---
        while (true) {

            if (keypr == 1) {
                if (rkey == KeyEvent.VK_M)
                    player.switchMode();
                else if (rkey == KeyEvent.VK_SPACE)
                    laser.fire(player.getAX(), player.getAY(), player.getBX(), player.getBY());
                else
                    player.move(rkey);
                keypr = 0;
            }

            checkItemPickup();
            laser.update();

            loopCounter++;

            if (loopCounter % 4 == 0) {

                for (int i = 0; i < robotCount; i++) {
                    robots[i].move();
                }
            }

            if (loopCounter % 20 == 0) {
                addRandomSpawn();
            }

            cn.getTextWindow().setCursorPosition(55, 0);
            cn.getTextWindow().output("Time : " + (loopCounter / 20) + "  ");
            cn.getTextWindow().setCursorPosition(55, 3);
            cn.getTextWindow().output("P.Score: " + player.getScore() + "  ");
            cn.getTextWindow().setCursorPosition(55, 4);
            cn.getTextWindow().output("P.Life : " + player.getLife() + "  ");
            cn.getTextWindow().setCursorPosition(55, 5);
            cn.getTextWindow().output("P.Laser: " + laser.getPackedCount() + "  ");

            Thread.sleep(50);
        }
    }

    public void addRandomSpawn() {

        if (itemCount >= MAX_ITEMS || robotCount >= MAX_ROBOTS)
            return;

        int x, y;
        do {
            x = rnd.nextInt(Maze.COLS - 2) + 1;
            y = rnd.nextInt(Maze.ROWS - 2) + 1;
        } while (Maze.map[y][x] == '#' || isOccupied(x, y));

        int chance = rnd.nextInt(11);
        //
        // Treasures and Laser
        if (chance <= 1)
            spawnItem(x, y, '1'); // Treasure-1
        else if (chance <= 3)
            spawnItem(x, y, '2'); // Treasure-2
        else if (chance <= 5)
            spawnItem(x, y, '3'); // Treasure-3
        else if (chance <= 8)
            spawnItem(x, y, '@'); // Laser

        else if (chance == 9)
            spawnRobotX(x, y);
        else
            spawnRobotX(x, y); // X-Robot
    }

    // Adding treasure
    private void spawnItem(int x, int y, char type) {
        if (itemCount < MAX_ITEMS) {
            Item item = new Item(x, y, type, cn);
            items[itemCount] = item;
            itemCount++;
            item.draw();
        }
    }

    // Adding robot
    private void spawnRobotX(int x, int y) {
        if (robotCount < MAX_ROBOTS) {
            RobotX bot = new RobotX(cn, x, y);
            robots[robotCount] = bot;
            robotCount++;
            bot.draw();
        }
    }

    private void checkItemPickup() {
        int ax = player.getAX();
        int ay = player.getAY();
        int bx = player.getBX();
        int by = player.getBY();

        for (int i = 0; i < itemCount; i++) {
            int ix = items[i].getX();
            int iy = items[i].getY();
            boolean touchedA = (ix == ax && iy == ay);
            boolean touchedB = (ix == bx && iy == by);

            if (touchedA || touchedB) {
                char type = items[i].getType();

                if (type == '@') {
                    laser.addPacked();
                } else if (type == '1') {
                    player.addScore(3);
                } else if (type == '2') {
                    player.addScore(10);
                } else if (type == '3') {
                    player.addScore(30);
                }

                itemCount--;
                items[i] = items[itemCount];
                i--;
            }
        }
    }

    private boolean isOccupied(int x, int y) {
        // check trasures
        for (int i = 0; i < itemCount; i++) {
            if (items[i].getX() == x && items[i].getY() == y)
                return true;
        }
        // check robots
        for (int i = 0; i < robotCount; i++) {
            if (robots[i].getX() == x && robots[i].getY() == y)
                return true;
        }

        return false;
    }
}
