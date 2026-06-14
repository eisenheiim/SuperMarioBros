//Name Surname: Sude Naz Aslan
//Student ID: 2024400336

import java.util.ArrayList;

public class Map {

    private int[][] obstacles;
    private int[][] pipes;
    private int[][] portals;
    private int[][] coins;
    public boolean[] coinCollected;
    public static boolean lowerflag;
    public static ArrayList<int[]> startScreenBlocks;

    public Map(int[][] obstacles, int[][] pipes, int[][] portals) {
        this.obstacles = obstacles;
        this.pipes = pipes;
        this.portals = portals;
        this.coins = new int[0][4];
        this.coinCollected = new boolean[coins.length];
        this.lowerflag = false; //true when mario uses portals
        Map.startScreenBlocks = new ArrayList<>();
    }

    public int[][] getObstacles() {
        return obstacles;
    }

    public int[][] getPipes() {
        return pipes;
    }

    public int[][] getPortals() {
        return portals;
    }

    public int[][] getCoins() {
        return coins;
    }

    public int[] getPipe(int index) {
        return pipes[index];
    }

    public int[] getPortal(int index) {
        return portals[index];
    }

    //for the  3. level coins.
    public void setCoins(int[][] newCoins) {
        this.coins = newCoins;
        this.coinCollected = new boolean[coins.length];
    }

    public boolean isOnGround(int x, int y, double halfSize, double speedY, int worldW, int worldH) {
        if (speedY > 0) {
            return false; //if falling itsn not on ground

        }
        if (y - halfSize <= 0) {
            return true;
        }

        if ("menu".equals(Game.gameState)) {
            for (int[] block : startScreenBlocks) {
                if (checkCollisionBelow(x, y, halfSize, block)) {
                    return true;
                }
            }

        } else {

            for (int[] obs : obstacles) {
                if (checkCollisionBelow(x, y, halfSize, obs)) {
                    return true;
                }
            }
            // checking collision below with exit pipes.
            for (int i = 0; i < pipes.length; i++) {
                int[] pipe = pipes[i];
                // Only check collision for exit pipe (pipes[3],[2]), skip entry pipes
                if (i == 3 || i == 2) {
                    if (checkCollisionBelow(x, y, halfSize, pipe)) {
                        return true;
                    }
                }
            }
            for (int[] portal : portals) {
                //if player is on the  portal, we skip collision check
                if (lowerflag) {
                    return false;
                }
                if (checkCollisionBelow(x, y, halfSize, portal)) {
                    return true;
                }

            }
        }
        return false;

    }

    public boolean isOnCeiling(int x, int y, double halfSize, double speedY, int worldW, int worldH) {
        if (speedY <= 0) {
            return false; // checking only when moving upwards

        }
        if (y > worldH) {//above the height
            return true;
        }

        for (int[] obs : obstacles) {
            if (checkCollisionAbove(x, y, halfSize, obs)) {
                return true;
            }
        }
        for (int[] pipe : pipes) {
            if (checkCollisionAbove(x, y, halfSize, pipe)) {
                return true;
            }
        }
        return false;
    }

    public void handleTeleport(Mario mario, boolean sPressed) {
        if (sPressed && checkCollisionBelow(mario.x, mario.y, mario.getSize() / 2.0, portals[3])) {
            lowerflag = true;
            //moves mario downwards
            mario.speedY = -2;
        } else if (!sPressed && checkCollisionAbove(mario.x, mario.y, mario.getSize() / 2.0, portals[1]) && !lowerflag) {
            mario.spawn(portals[3][0], portals[3][1]);

        }
    }
//helper menthods for collision checks

    public boolean checkCollisionBelow(int player_x, int player_y, double halfSize, int[] rect) {
        int rectangle_x = rect[0], rectangle_y = rect[1], rect_width = rect[2], rect_height = rect[3];
        return player_x >= rectangle_x - rect_width && player_x <= rectangle_x + rect_width
                && player_y - halfSize >= rectangle_y - rect_height && player_y - halfSize <= rectangle_y + rect_height;
    }

    public static boolean checkCollisionAbove(int player_x, int player_y, double halfSize, int[] rect) {
        int rectangle_x = rect[0], rectangle_y = rect[1], rect_width = rect[2], rect_height = rect[3];
        return player_x >= rectangle_x - rect_width && player_x <= rectangle_x + rect_width
                && player_y + halfSize >= rectangle_y - rect_height && player_y + halfSize <= rectangle_y + rect_height;
    }

    public int collectCoin(Mario mario) {
        //method to check coin collection

        for (int i = 0; i < coins.length; i++) {
            if (!coinCollected[i]) {
                int cx = coins[i][0], cy = coins[i][1];
                int cw = coins[i][2], ch = coins[i][3];

                if (Math.abs(mario.x - cx) < mario.getSize() / 2 + cw
                        && Math.abs(mario.y - cy) < mario.getSize() / 2 + ch) {
                    coinCollected[i] = true;

                }
            }
        }
        int count = 0;
        for (int i = 0; i < coins.length; i++) {
            if (!coinCollected[i]) {
                count++;
            }
        }
        return count;

    }

    public boolean isAtExit(Mario mario) {
        int exit_x = pipes[3][0];
        int exit_y = pipes[3][1];

        int exit_width = pipes[3][2];
        int exit_height = pipes[3][3];

        if (mario.x > exit_x && Math.abs(mario.x - exit_x) < mario.getSize() / 2 + exit_width
                && Math.abs(mario.y - exit_y) < mario.getSize() / 2 + exit_height) {
            return true;
        } else {
            return false;
        }
    }
//collision check for coins and exit pipe, used for level completion and coin collection.

    public boolean hasCollision(double x, double y, double playerHalfSize) {
        return checkCollision(x, y, playerHalfSize, obstacles)
                || checkCollision(x, y, playerHalfSize, pipes)
                || checkCollision(x, y, playerHalfSize, portals);
    }

    public boolean checkCoinCollision(double x, double y, double playerHalfSize) {
        return checkCollision(x, y, playerHalfSize, coins);
    }

    private boolean checkCollision(double nextX, double nextY, double playerHalfSize, int[][] obstacles) {
//collision check for single objects
        for (int[] obs : obstacles) {
            int object_x = obs[0], object_y = obs[1], object_width = obs[2], object_height = obs[3];
            if (nextX + playerHalfSize - 7 > object_x - object_width
                    && nextX - playerHalfSize + 7 < object_x + object_width
                    && nextY + playerHalfSize - 7 > object_y - object_height
                    && nextY - playerHalfSize + 7 < object_y + object_height) {
                return true;
            }
        }
        return false;

    }

    public void startScreen() {
        startScreenBlocks = new ArrayList<>();
        //placing startscreen blocks
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 30; i++) {
                int x = 20 + i * 40;
                int y = 20 + j * 40;
                startScreenBlocks.add(new int[]{x, y, 20, 20});
                StdDraw.picture(x, y, "assets/block.png", 40, 40);
            }
        }

    }

    public void draw() {
        //drawing everything
        for (int[] obs : obstacles) {
            StdDraw.picture(obs[0], obs[1], "assets/block.png", obs[2] * 2, obs[3] * 2);

        }
        StdDraw.setPenColor(255, 0, 0);
        for (int[] portal : portals) {
            StdDraw.filledRectangle(portal[0], portal[1], portal[2], portal[3]);
        }
        StdDraw.setPenColor(255, 255, 0);
        for (int[] pipe : pipes) {
            StdDraw.filledRectangle(pipe[0], pipe[1], pipe[2], pipe[3]);
        }

        for (int i = 0; i < coins.length; i++) {
            if (!coinCollected[i]) {
                StdDraw.picture(coins[i][0], coins[i][1], "assets/coin.png", coins[i][2] * 2, coins[i][3] * 2);

            }
        }

        StdDraw.setPenColor(255, 120, 0);

    }
}
