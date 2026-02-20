import enigma.core.Enigma;
import enigma.console.Console;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main {

    public static int keypr = 0;
    public static int rkey = 0;

    public static void main(String[] args) throws InterruptedException {

        Console console = Enigma.getConsole("CME1252 - Twins Game");
        Maze maze = new Maze(console);
        maze.createMaze();
        maze.render();

        Players player = new Players(console, 5, 5);
        player.render();
        KeyListener klis = new KeyListener() {
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
        console.getTextWindow().addKeyListener(klis);
        while (true) {
            if (keypr == 1) {
                player.move(rkey, maze);
                keypr = 0;
            }
            Thread.sleep(50);
        }
    }
}