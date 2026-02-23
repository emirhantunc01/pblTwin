import java.util.Random;
import enigma.console.Console;
import enigma.console.TextAttributes;
import java.awt.Color;

public class RobotX {
    private int life = 1000;
    private int score = 0;
    Random rnd = new Random();
    private int initRow = rnd.nextInt(1,23);
    private int initCol = rnd.nextInt(1,53);
    private Console console;


    public RobotX(Console console){
        this.console = console;
    }
    public void render(){
        TextAttributes attr;
        attr = new TextAttributes(Color.YELLOW, Color.BLACK);
        console.getTextWindow().output(initCol, initRow, 'X',attr);
    }
    public void move(Maze maze) throws InterruptedException {
        while(true){
            int direction = rnd.nextInt(1,5);
            if(direction == 1){
                if(maze.isValidMove(initRow -1 ,initCol)) {
                    clear();
                    initRow--; //up
                }
            }
            else if(direction==2){
                if(maze.isValidMove(initRow+1,initCol)){
                    clear();
                    initRow++;//down
                }
            }
            else if(direction == 3){
                if(maze.isValidMove(initRow,initCol+1)){
                    clear();
                    initCol++;//right
                }
            }
            else{
                if(maze.isValidMove(initRow,initCol-1)){
                    clear();
                    initCol--;//left
                }
            }
            render();
            Thread.sleep(200);

        }
    }

    private void clear() {
        console.getTextWindow().output(initCol, initRow, ' ');
        console.getTextWindow().output(initCol, initRow, ' ');
    }
}
