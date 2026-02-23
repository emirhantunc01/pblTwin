import enigma.console.Console;

public class Maze {

    private char[][] maze;
    private int rows = 23;
    private int cols = 53;
    private Console console;

    public Maze(Console console) {
        this.console = console;
        this.maze = new char[rows][cols];
    }

    public void initMaze() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
                    maze[r][c] = '#';
                } else {
                    maze[r][c] = ' ';
                }
            }
        }
    }
    public void render() {

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                console.getTextWindow().output(c, r, maze[r][c]);
            }
        }
    }
    public boolean isValidMove(int r, int c) {

        if (r < 0 || r >=rows || c < 0 || c >= cols) return false;
        if (maze[r][c] == '#') return false;
        return true;
    }
    public char[][] getMazeData () {
        return maze;
    }
}
