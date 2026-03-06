import enigma.console.Console;


public class Lasers {

    // =====================================================
    // WHAT IS THIS CLASS?
    // This class manages everything about lasers in the game.
    // - Packed lasers (@) that the player can pick up
    // - Active laser blocks (+) that spread from A to B
    // =====================================================

    // ----- HOW MANY PACKED LASERS THE PLAYER HAS -----
    // When the player walks over a '@', this number goes up by 1.
    // When the player presses space, this number goes down by 1.
    public static int packedLaserCount = 0;

    // ----- ACTIVE LASER BLOCKS ON THE BOARD -----
    // We need to track every '+' block on the board.
    // Each '+' block has: x position, y position, and remaining lifetime.
    //
    // MAX_ACTIVE = 200 means we can have at most 200 '+' blocks at once.
    // This is more than enough for our 23x53 board.
    public static final int MAX_ACTIVE = 200;

    // Arrays to store the x, y, and lifetime of each active '+' block.
    // activeX[0], activeY[0], activeLife[0] = first laser block's x, y, lifetime
    // activeX[1], activeY[1], activeLife[1] = second laser block's x, y, lifetime
    // ... and so on.
    public static int[] activeX    = new int[MAX_ACTIVE];
    public static int[] activeY    = new int[MAX_ACTIVE];
    public static int[] activeLife = new int[MAX_ACTIVE];

    // How many active '+' blocks are currently on the board
    public static int activeCount = 0;

    // ----- LASER FIRE QUEUE -----
    // When the player presses space, we don't place all '+' at once.
    // They spread one by one, one block per time unit.
    // So we need a queue of positions where '+' will appear.
    //
    // Think of it like a line of dominoes falling one by one.
    public static final int MAX_QUEUE = 100;

    // The queue stores the x,y positions that the laser will visit
    public static int[] queueX = new int[MAX_QUEUE];
    public static int[] queueY = new int[MAX_QUEUE];

    // queueSize  = how many positions are in the queue total
    // queueIndex = which position we are currently at (next to place)
    public static int queueSize  = 0;
    public static int queueIndex = 0;

    // Is a laser currently being fired? (are '+' blocks still spreading?)
    public static boolean isFiring = false;


    // ==========================================================
    // METHOD: collectPacked
    // ==========================================================
    // WHEN TO CALL: When player (A or B) walks onto a '@' square.
    // WHAT IT DOES: Increases the player's packed laser count by 1.
    //               Removes the '@' from the maze.
    // ==========================================================
    public static void collectPacked(int x, int y) {

        // Add 1 to the player's laser inventory
        packedLaserCount++;

        // Remove the '@' from the maze array (replace with empty space)
        Maze.maze[y][x] = ' ';
    }


    // ==========================================================
    // METHOD: fireLaser
    // ==========================================================
    // WHEN TO CALL: When the player presses the SPACE key.
    // WHAT IT DOES: Calculates an L-shaped path from A to B
    //               using the Manhattan algorithm, and stores
    //               those positions in the queue.
    //               The '+' blocks will spread one per time unit.
    //
    // HOW MANHATTAN WORKS:
    //   Instead of a diagonal straight line, we move in two steps:
    //   Step 1: Move horizontally from A to B's x position
    //   Step 2: Move vertically from A's y to B's y position
    //   This creates an L-shaped path, like walking city blocks.
    //
    //   Example (A at top-left, B at bottom-right):
    //   A + + + + .
    //             +
    //             +
    //             B
    //
    // PARAMETERS:
    //   ax, ay = position of player A (start point)
    //   bx, by = position of player B (end point)
    // ==========================================================
    public static void fireLaser(int ax, int ay, int bx, int by) {

        // --- CHECK 1: Does the player have any packed lasers? ---
        if (packedLaserCount <= 0) {
            // No lasers to fire, do nothing
            return;
        }

        // --- CHECK 2: Is a laser already being fired? ---
        // We only allow one laser fire at a time.
        if (isFiring) {
            return;
        }

        // --- Use one packed laser ---
        packedLaserCount--;

        // --- CALCULATE THE L-SHAPED PATH FROM A TO B ---
        // We use the Manhattan algorithm.
        // First go horizontally (left or right) until we reach B's x.
        // Then go vertically (up or down) until we reach B's y.

        // Reset the queue
        queueSize  = 0;
        queueIndex = 0;

        // --- STEP 1: Move horizontally from ax to bx ---
        // currentX starts at A's x position
        // We move one step at a time toward B's x
        // Y stays the same as A's y during this phase

        int currentX = ax;

        while (currentX != bx) {

            // Move one step in the direction of bx
            if (bx > currentX) {
                currentX++;   // B is to the right, move right
            } else {
                currentX--;   // B is to the left, move left
            }

            // Add this position to the queue
            // Y is still ay because we haven't started moving vertically yet
            if (queueSize < MAX_QUEUE) {
                queueX[queueSize] = currentX;
                queueY[queueSize] = ay;
                queueSize++;
            }
        }

        // --- STEP 2: Move vertically from ay to by ---
        // currentY starts at A's y position
        // We move one step at a time toward B's y
        // X is now fixed at bx (we already reached it in step 1)

        int currentY = ay;

        while (currentY != by) {

            // Move one step in the direction of by
            if (by > currentY) {
                currentY++;   // B is below, move down
            } else {
                currentY--;   // B is above, move up
            }

            // Add this position to the queue
            // X is now bx because we finished the horizontal movement
            if (queueSize < MAX_QUEUE) {
                queueX[queueSize] = bx;
                queueY[queueSize] = currentY;
                queueSize++;
            }
        }

        // --- Start the firing process ---
        isFiring = true;
    }


    // ==========================================================
    // METHOD: updateLaserSpread
    // ==========================================================
    // WHEN TO CALL: Every time unit (every 50ms game tick).
    // WHAT IT DOES: Places the next '+' block from the queue
    //               onto the board, if the square is empty.
    //               "The laser can go through any object.
    //                But spreading can use only empty squares."
    //
    // PARAMETER:
    //   cn = the console, so we can draw the '+' on screen
    // ==========================================================
    public static void updateLaserSpread(Console cn) {

        // If no laser is currently being fired, do nothing
        if (!isFiring) {
            return;
        }

        // If we have placed all blocks in the queue, stop firing
        if (queueIndex >= queueSize) {
            isFiring = false;
            return;
        }

        // Get the next position from the queue
        int x = queueX[queueIndex];
        int y = queueY[queueIndex];

        // Move to the next position in the queue for next time
        queueIndex++;

        // --- CHECK: Is this square empty? ---
        // "Spreading can use only empty squares"
        // The laser LINE passes through walls and objects,
        // but '+' can only be PLACED on empty squares.
        if (Maze.maze[y][x] == ' ') {

            // Place the '+' in the maze array
            Maze.maze[y][x] = '+';

            // Draw the '+' on the screen
            cn.getTextWindow().output(x, y, '+');

            // --- Register this as a new active laser block ---
            if (activeCount < MAX_ACTIVE) {
                activeX[activeCount]    = x;
                activeY[activeCount]    = y;
                activeLife[activeCount] = 100;  // 100 time units lifetime
                activeCount++;
            }
        }
        // If the square is NOT empty, we skip it (laser passes through)
        // but we don't place a '+' there.
    }


    // ==========================================================
    // METHOD: updateLifetimes
    // ==========================================================
    // WHEN TO CALL: Every time unit (every 50ms game tick).
    // WHAT IT DOES: Decreases the lifetime of every active '+'
    //               block by 1. If lifetime reaches 0, the '+'
    //               disappears from the board.
    //
    // PARAMETER:
    //   cn = the console, so we can erase '+' from the screen
    // ==========================================================
    public static void updateLifetimes(Console cn) {

        // We go through all active laser blocks
        // We use 'i' as our index, starting from 0
        int i = 0;

        while (i < activeCount) {

            // Decrease this block's lifetime by 1
            activeLife[i]--;

            // --- Has this block's lifetime run out? ---
            if (activeLife[i] <= 0) {

                // Get the position of this block
                int x = activeX[i];
                int y = activeY[i];

                // Remove '+' from the maze array
                // (only if it's still a '+', it might have been
                //  overwritten by something else)
                if (Maze.maze[y][x] == '+') {
                    Maze.maze[y][x] = ' ';
                    cn.getTextWindow().output(x, y, ' ');
                }

                // --- REMOVE THIS BLOCK FROM THE ARRAYS ---
                // We do this by moving the last element to this position.
                // This is a common trick to avoid shifting all elements.
                //
                // Example: If we have [A, B, C, D, E] and remove C:
                // We put E where C was: [A, B, E, D] and reduce count.
                activeCount--;
                activeX[i]    = activeX[activeCount];
                activeY[i]    = activeY[activeCount];
                activeLife[i] = activeLife[activeCount];

                // DON'T increase i here, because we need to check
                // the element we just moved to this position.

            } else {
                // This block is still alive, move to next
                i++;
            }
        }
    }


    // ==========================================================
    // METHOD: harmRobots
    // ==========================================================
    // WHEN TO CALL: Every time unit (every 50ms game tick).
    // WHAT IT DOES: Checks every active '+' block. If a C or X
    //               robot is in any of the 4 neighbor squares
    //               (up, down, left, right), that robot takes
    //               50 damage.
    //
    // NOTE: This method does not handle the actual robot HP
    //       reduction. It returns an array of [x, y] positions
    //       where robots are being harmed, so the main game
    //       loop can apply the damage.
    //
    //       Alternatively, if you have a Robot class, you can
    //       directly reduce HP here. For now, we return
    //       the count of hits at each position.
    //
    // PARAMETERS:
    //   (none - reads from maze array and active laser arrays)
    //
    // RETURNS:
    //   A 2D array: harmed[row][col] = how many laser blocks
    //   are next to a robot at that position.
    //   (Each neighboring '+' = 50 damage, so total damage
    //    = harmed[row][col] * 50)
    // ==========================================================
    public static int[][] harmRobots() {

        // Create a 2D array to count hits for each position
        // harmed[y][x] = number of neighboring '+' blocks
        int[][] harmed = new int[Maze.MazeX][Maze.MazeY];

        // The 4 neighbor directions: up, down, left, right
        // dx and dy arrays make it easy to check all 4 neighbors
        // dx[0]=0, dy[0]=-1 means "up" (same column, one row up)
        // dx[1]=0, dy[1]=1  means "down"
        // dx[2]=-1,dy[2]=0  means "left"
        // dx[3]=1, dy[3]=0  means "right"
        int[] dx = { 0,  0, -1, 1};
        int[] dy = {-1,  1,  0, 0};

        // Go through every active '+' block
        for (int i = 0; i < activeCount; i++) {

            int lx = activeX[i];   // laser block x position
            int ly = activeY[i];   // laser block y position

            // Check all 4 neighbors of this '+' block
            for (int d = 0; d < 4; d++) {

                int nx = lx + dx[d];   // neighbor x
                int ny = ly + dy[d];   // neighbor y

                // Make sure the neighbor is inside the maze
                if (ny >= 0 && ny < Maze.MazeX && nx >= 0 && nx < Maze.MazeY) {

                    // Is there a robot at this neighbor position?
                    char cell = Maze.maze[ny][nx];

                    if (cell == 'C' || cell == 'X') {
                        // This robot is next to a '+' block!
                        // Add 1 hit to this position
                        harmed[ny][nx]++;
                    }
                }
            }
        }

        // Return the hit counts
        // The main game loop will use this to apply damage:
        // damage = harmed[y][x] * 50
        return harmed;
    }
}
