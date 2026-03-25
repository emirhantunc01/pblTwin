# Twins Game

A maze survival and treasure collection game built in Java using the Enigma console library.

## How to Play

### Initialization
When you start the game, you will see a main menu:
1. **Generate Random Maze:** Creates a random, fully-connected maze.
2. **Load Maze from File:** Allows you to load a pre-designed maze from a `.txt` file (e.g., `test_valid_maze`).

### Objectives
- **Survive:** Enemy robots (`X` and `C`) will roam the maze and attempt to touch you. If they reach you, you take 50 HP damage. If your HP drops to 0, the game is over.
- **Collect Treasures:** Navigate the maze to pick up treasures (`1`, `2`, `3`) to increase your score.
- **Defend Yourself:** Collect Laser items (`@`) to arm yourself. You can shoot lasers to destroy robots. Destroying a robot grants 100 points.

### Controls
You control two connected "Twins" (Twin A and Twin B).
- **Movement:**
  - `W` : Move Up
  - `S` : Move Down
  - `A` : Move Left
  - `D` : Move Right
- **Action:**
  - `M` : Swap control between Twin A and Twin B. (The twin you are currently controlling is primarily visible, the other follows/drags behind).
  - `SPACE` : Fire your laser in the direction you are facing. (Requires Laser ammo).

### Enemies
- **Robot X (`X`):** Moves randomly through the maze.
- **Robot C (`C`):** A smart enemy that actively paths towards the nearest treasure to steal it.

### UI Reference
The bottom dashboard shows your current stats:
- `Time`: Elapsed game time.
- `P.Score`: Your total score from treasures and robot kills.
- `P.Life`: Your current Health Points (HP).
- `P.Laser`: Number of laser shots available.
- `AI.Score / AI.Life / AI.Count`: Remaining robots and their cumulative stats.

Good luck surviving the maze!
