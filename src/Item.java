import enigma.console.Console;
import enigma.console.TextAttributes;
import java.awt.Color;

/**
 * Item.java
 * Represents collectible items on the map, such as Treasures ('1', '2', '3')
 * and Laser pickups ('@').
 */
public class Item {
    private int x,y;
    private char type; // '1', '2', '3', '@'
    private int scoreValue;
    private Console cn;
//
    private static TextAttributes colorTreasure = new TextAttributes(Color.YELLOW, Color.BLACK);
    private static TextAttributes colorLaser = new TextAttributes(Color.CYAN, Color.BLACK);

    public Item(int x, int y, char type, Console cn) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.cn = cn;

        if (type == '1') scoreValue = 3;
        else if (type == '2') scoreValue = 10;
        else if (type == '3') scoreValue = 30;
        else scoreValue = 0;

    }

    public void draw() {
        if (type == '@') {
            cn.getTextWindow().output(x, y, type, colorLaser);
        } else {
            cn.getTextWindow().output(x, y, type, colorTreasure);
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public char getType() { return type; }


}
