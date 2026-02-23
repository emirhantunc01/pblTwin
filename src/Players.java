import enigma.console.Console;
import java.awt.event.KeyEvent;
import enigma.console.TextAttributes;
import java.awt.Color;

public class Players {
    private int aRow, aCol;
    private int bRow,bCol;
    private int life = 1000;
    private int score = 0;
    private int mode = 1;
    private Console console;

    public Players(Console console, int startRow, int startCol) {
        this.console = console;
        this.aRow = startRow;
        this.aCol = startCol;
        this.bRow = startRow;
        this.bCol = startCol;
    }
    public void render() {
        TextAttributes attr;
        if(mode == 1) {
            attr = new TextAttributes(Color.GREEN, Color.BLACK); // Mod 1: Yeşil
        } else {
            attr = new TextAttributes(Color.MAGENTA, Color.BLACK); // Mod -1: E
        }
        if (aRow == bRow && aCol == bCol) {
            console.getTextWindow().output(aCol, aRow, 'A',attr);
        } else {
            console.getTextWindow().output(aCol, aRow, 'A',attr);
            console.getTextWindow().output(bCol, bRow, 'B',attr);
        }
    }
    private void clear() {
        console.getTextWindow().output(aCol, aRow, ' ');
        console.getTextWindow().output(bCol, bRow, ' ');
    }
    public void move(int key, Maze maze) {

        clear();

        int dRow = 0, dCol = 0;
        if (key == KeyEvent.VK_LEFT) dCol = -1;
        else if (key == KeyEvent.VK_RIGHT) dCol = 1;
        else if (key == KeyEvent.VK_UP) dRow = -1;
        else if (key == KeyEvent.VK_DOWN) dRow = 1;

        if (maze.isValidMove(aRow + dRow, aCol + dCol)) {
                aRow += dRow;
                aCol += dCol;
        }
        int dRowB = dRow * mode;
        int dColB = dCol * mode;

        if (maze.isValidMove(bRow + dRowB, bCol + dColB)) {
            bRow += dRowB;
            bCol += dColB;
        }

        render();
    }
}