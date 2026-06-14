//Name Surname: Sude Naz Aslan
//Student ID: 2024400336

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Game {

    private ArrayList<Level> levels;
    private Map map;
    private int width = 800; // Screen width
    private int height = 800;// Screen height
    public static String gameState;

    private Mario mymario;
    private int levelnum = 0;
    private long gamestart;
    private int pause = 30;
    private int frameCount = 0;
    private boolean cameraFollow = false;
    public boolean onGround;

    //to ensure coins are only initialized once per level, not on every respawn
    private int resetCoins = 0;

    public Game(ArrayList<Level> levels, Map map) {
        this.levels = levels;
        this.map = map;

    }

    public void run() {
        //creates the character
        mymario = new Mario(70, 220);

        gameState = "menu";

        while (true) {
            //game loop

            if ("menu".equals(gameState)) {
                startScreen(mymario);
            } else if ("play".equals(gameState)) {
                playLevel();

            } else if ("end".equals(gameState)) {
                endScreen(getTime(gamestart), mymario.getDeathCount());

            }
        }
    }
//method for formatting the elapsed time.

    private String getTime(long startingTime) {
        long elapsed = System.currentTimeMillis() - startingTime;
        long minutes = elapsed / 60000;
        long seconds = (elapsed % 60000) / 1000;
        long millis = (elapsed % 1000) / 10;
        return String.format("%02d:%02d:%02d", minutes, seconds, millis);
    }

    private void startScreen(Mario mario) {

        while (true) {
            StdDraw.setXscale(0, width);
            StdDraw.setYscale(0, height);
            if (mymario.x > width - 20) {
                mymario.x = width - 20;

            }
            //preventing from going out of bounds in the start screen
            if (mymario.x < 20) {
                mymario.x = 20;
            }
            frameCount++;
            StdDraw.clear(new Color(100, 150, 255));

            StdDraw.setPenColor(139, 69, 19);
            StdDraw.filledRectangle(width / 2, height * 2 / 3, 200, 60);

            StdDraw.setPenColor(255, 255, 255);
            StdDraw.setFont(new Font("Arial", Font.BOLD, 30));
            StdDraw.text(width / 2, height * 2 / 3 + 20, "SUPER");
            StdDraw.text(width / 2, height * 2 / 3 - 20, "MARIO BROS");

            StdDraw.setFont(new Font("Arial", Font.PLAIN, 16));
            StdDraw.text(width / 2, height / 2, "Press SPACE to start");
            StdDraw.text(width / 2, height / 2 - 30, "MOVE: [A] [D] [W]");

            StdDraw.text(width / 2, height / 2 - 60, "FPS: " + (int) (1000.0 / pause) + "  Adjust: <- ->");
            map.startScreen(); // Draw start screen blocks
            mymario.isWalking = false;

            onGround = map.isOnGround(
                    mymario.x, // Mario's X position
                    mymario.y - 1, // Mario's Y position
                    mymario.getSize() / 2.0, // Mario's half-size (16)
                    mymario.speedY, // Current falling speed
                    width, // Screen width
                    height // Screen height 

            );
            if (!onGround) {
                applyGravity(mymario);
            } else if (mymario.speedY < 0) { //reset speed when landing 
                mymario.speedY = 0;
            }

            // Handle input and update animation
            int moveX = mymario.handleInput(onGround, frameCount);
            mymario.x += moveX;

            // FPS adjustment
            if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT)) {
                pause = Math.min(120, pause + 2);
                StdDraw.pause(20);
            }
            if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)) {
                pause = Math.max(5, pause - 2);
                StdDraw.pause(20);
            }

            if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {

                gamestart = System.currentTimeMillis();
                gameState = "play";
                frameCount = 0;
                break;
            }
            mymario.y += mymario.speedY; //applying gravity to the y position
            if (mymario.x < 0) {
                mymario.x = 0;
            } else if (mymario.x > width) {
                mymario.x = width;
            }
            if (mymario.y < 0) {
                mymario.y = 0;
            } else if (mymario.y > height) {
                mymario.y = height;
            }

            if (onGround) {
                for (int[] block : Map.startScreenBlocks) {
                    int blockX = block[0];
                    int blockY = block[1];
                    int blockW = block[2];
                    int blockH = block[3];

                    if (mymario.x > blockX - blockW && mymario.x < blockX + blockW
                            && mymario.y - 16 < blockY + blockH && mymario.y > blockY - blockH) {
                        while (mymario.y - 16 < blockY + blockH && mymario.x > blockX - blockW && mymario.x < blockX + blockW) {
                            mymario.y += 1;
                        }
                    }
                }
            }

            mymario.draw(frameCount);
            StdDraw.show();
            StdDraw.pause(pause);
        }
    }

    private void playLevel() {
        if (levelnum >= levels.size()) {
            gameState = "end";
            return;
        }
        if (levelnum == 0) {
            mymario = new Mario(60, 780);
        }

        Level currlevel = levels.get(levelnum);
        //spawn mario at the start of the level
        mymario.respawn();

        // only initialize coins when entering a level for the first time, not on respawn
        if (resetCoins != 1) {
            map.setCoins(currlevel.getCoins());
            resetCoins = 1;
        }
        while (true) {

            frameCount++;
            onGround = !mymario.isDefeated() && map.isOnGround(
                    mymario.x, // Mario's X position
                    mymario.y, // Mario's Y position
                    mymario.getSize() / 2.0, // Mario's half-size (16)
                    mymario.speedY, // Current falling speed
                    width, // Screen width (400)
                    height // Screen height (600)
            );
            //for death animation, mario will be affected by gravity and can't move until the animation finishes
            int move;
            if (mymario.isDefeated()) {
                move = 0;
            } else {
                move = mymario.handleInput(onGround, frameCount);
            }
            //this condition makes mario move a bit downwards, then handles teleportation
            if (Map.lowerflag) {
                if (mymario.y < map.getPortal(3)[1] - 15) {
                    mymario.spawn(map.getPortal(1)[0], map.getPortal(1)[1] - 40);
                    Map.lowerflag = false;
                    mymario.speedY = -1;
                    Map.lowerflag = false;
                }
            }

            mymario.x += move;
            if (mymario.x < 0) {
                mymario.x = 0;
            } else if (mymario.x > width) {
                mymario.x = width;
            }
            if (mymario.y < 0) {
                mymario.y = 0;
            } else if (mymario.y > height) {
                mymario.y = height;
            }
            // Checking collision with obstacles, pipes, and portals
            if (!mymario.isDefeated() && map.hasCollision(mymario.x, mymario.y, mymario.getSize() / 2.0)) {
                mymario.x -= move; // Undo move if colliding
            }

            // Gravity
            if (!onGround) {
                applyGravity(mymario);
            } //

            // Ground detection
            if (onGround && mymario.speedY < 0) {
                //if mario is on th ground its speed should be 0.
                mymario.speedY = 0;
            }
            if (onGround && !mymario.isDefeated()) {

                map.lowerflag = false;
                while (map.hasCollision(mymario.x, mymario.y - 1, mymario.getSize() / 2.0)) {
                    //to prevent from embedding into the ground we increase it's y position
                    mymario.y += 5;
                }
            }
            if (map.checkCoinCollision(mymario.x, mymario.y, mymario.getSize() / 2.0)) {

                //checking coin collection
                map.collectCoin(mymario);

            }

            // Ceiling detection
            if (!mymario.isDefeated() && map.isOnCeiling(mymario.x, mymario.y, mymario.getSize() / 2.0, mymario.speedY, width - 10, height - 10)) {
                mymario.speedY = -1;
                // pushing downwards to prevent sticking to the ceiling
                while (map.isOnCeiling(mymario.x, mymario.y, mymario.getSize() / 2.0, 1, width - 10, height - 10)) {
                    mymario.y -= 1;
                }
            }

            // Enemy collision
            for (Enemy enemy : currlevel.getEnemies()) {
                enemy.move();
                if (mymario.checkEnemyCollision(enemy) && !mymario.isDefeated()) {
                    mymario.die();
                }
            }

            for (Enemy enemy : currlevel.getEnemies()) {
                // Move and check bullet collisions
                for (int i = 0; i < enemy.bulletX.size(); i++) {
                    if (enemy.bulletActive.get(i)) {
                        // Move bullet
                        enemy.bulletX.set(i, enemy.bulletX.get(i) + enemy.bulletSpeedX.get(i));
                        enemy.bulletY.set(i, enemy.bulletY.get(i) + enemy.bulletSpeedY.get(i));

                        // Check collision with Mario
                        if (mymario.checkCollisionWithBullet(enemy.bulletX.get(i), enemy.bulletY.get(i)) && !mymario.isDefeated()) {
                            mymario.die();
                        }

                        // Check collision with obstacles
                        if (map.hasCollision(enemy.bulletX.get(i), enemy.bulletY.get(i), 5)) {
                            enemy.bulletActive.set(i, false);
                        }
                    }
                }
            }

            // Portal teleport from below
            if (StdDraw.isKeyPressed(KeyEvent.VK_S) && !mymario.isDefeated()) {
                map.handleTeleport(mymario, true);
            }// Portal teleport from above
            if (!mymario.isDefeated() && Map.checkCollisionAbove(mymario.x, mymario.y, mymario.getSize() / 2.0, map.getPortal(1))) {
                map.handleTeleport(mymario, false);
            }

            if (StdDraw.isKeyPressed(KeyEvent.VK_C)) {
                cameraFollow = !cameraFollow;//toggling camera follow
                StdDraw.pause(200);
            }

            // Restarting the game
            if (StdDraw.isKeyPressed(KeyEvent.VK_R)) {
                levelnum = 0;
                resetCoins = 0; // Reset so coins are reinitialized
                gameState = "play";
                gamestart = System.currentTimeMillis();
                frameCount = 0;

                return;
            }

            // Check exit
            if (map.isAtExit(mymario)) {
                if (levelnum == 2 && map.collectCoin(mymario) > 0) {

                    // to pass level 3 mario needs to collect all coins
                } else {
                    levelBanner(levelnum + 1, currlevel, currlevel.getEnemies(), mymario, getTime(gamestart));
                    levelnum++;
                    resetCoins = 0;

                    return;
                }
            }
            if (mymario.isDefeated() && mymario.y <= 0) {
                mymario.respawn();
                //death animation finishes
                return;
            }

            mymario.y += mymario.speedY;

            //checking for boundaries
            if (mymario.x < 0) {
                mymario.x = 0;
            } else if (mymario.x > width) {
                mymario.x = width;
            }
            if (mymario.y < 0) {
                mymario.y = 0;
            } else if (mymario.y > height) {
                mymario.y = height;
            }

            // Enemy shooting logic
            for (Enemy enemy : currlevel.getEnemies()) {
                //for custom enemies
                if (enemy.canShoot) {
                    if (Math.random() < 0.007) {
                        enemy.randomshut(mymario.x, mymario.y);
                    }
                }
            }

            // Draw
            drawGame(currlevel);

            // Draw bullets after game scene
            for (Enemy enemy : currlevel.getEnemies()) {
                StdDraw.setPenColor(255, 120, 0);
                for (int i = 0; i < enemy.bulletX.size(); i++) {
                    if (enemy.bulletActive.get(i)) {
                        StdDraw.filledRectangle(enemy.bulletX.get(i), enemy.bulletY.get(i), 9, 9);
                    }
                }
            }

            StdDraw.show();
            StdDraw.pause(pause);

        }

    }

    private void applyGravity(Mario mario) {
        mario.speedY -= mario.gravity;

    }

    private long levelBanner(int id, Level currentLevel, ArrayList<Enemy> currentEnemies, Mario mario, String elapsedTime) {
        long bannerStart = System.currentTimeMillis();
        while (true) {
            StdDraw.setXscale(0, width);
            StdDraw.setYscale(0, height);
            StdDraw.setPenColor(255, 255, 255);

            StdDraw.filledRectangle(width / 2, height / 2, width, height / 5 - 50);

            StdDraw.setPenColor(255, 215, 0);
            StdDraw.setFont(new Font("Arial", Font.BOLD, 30));
            StdDraw.text(width / 2, height * 2 / 3 - 50, "LEVEL " + id + " COMPLETE");

            if (levelnum < 3) {
                StdDraw.text(width / 2, height / 2 - 50, "Press SPACE to continue");
                //at the last level, game ends
            } else {
                return bannerStart;
            }

            if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                //proceeding to next level
                StdDraw.pause(150);
                return bannerStart;
            }
            StdDraw.show();
            StdDraw.pause(30);
        }
    }

    private int endScreen(String elapsedTime, int deathCount) {

        int selected = 0;

        while (true) {
            StdDraw.setXscale(0, width);
            StdDraw.setYscale(0, height);
            StdDraw.clear(new Color(30, 50, 100));

            StdDraw.setPenColor(255, 215, 0);
            StdDraw.setFont(new Font("Arial", Font.BOLD, 40));
            StdDraw.text(width / 2, height * 2 / 3, "YOU WON!");

            StdDraw.setFont(new Font("Arial", Font.PLAIN, 20));
            StdDraw.text(width / 2, height / 2 + 50, "Time: " + elapsedTime);
            StdDraw.text(width / 2, height / 2 + 20, "Deaths: " + deathCount);

            if (selected == 0) {
                StdDraw.setPenColor(255, 215, 0);
                StdDraw.text(width / 2, height / 2 - 30, ">Exit<");
                StdDraw.setPenColor(150, 150, 150);
                StdDraw.text(width / 2, height / 2 - 80, "Restart");
            } else {
                StdDraw.setPenColor(150, 150, 150);
                StdDraw.text(width / 2, height / 2 - 30, "Exit");
                StdDraw.setPenColor(255, 215, 0);
                StdDraw.text(width / 2, height / 2 - 80, ">Restart<");

            }

            if (StdDraw.isKeyPressed(KeyEvent.VK_UP)) {
                selected = 0;
            }
            if (StdDraw.isKeyPressed(KeyEvent.VK_DOWN)) {
                selected = 1;
            }

            if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                if (selected == 0) {
                    System.exit(0);
                } else {
                    gameState = "play";
                    gamestart = System.currentTimeMillis();
                    levelnum = 0;
                    resetCoins = 0;
                    break;
                }
            }

            StdDraw.show();
            StdDraw.pause(100);
        }
        return 1;
    }

    private void drawGame(Level level) {
        if (cameraFollow) {
            //centering camera on mario and in boundaries of the map
            double camera_x = Math.max(200, Math.min(width - 200, mymario.x));
            double camera_y = Math.max(200, Math.min(height - 200, mymario.y));
            StdDraw.setXscale(camera_x - 200, camera_x + 200);
            StdDraw.setYscale(camera_y - 200, camera_y + 200);
        } else {
            StdDraw.setXscale(0, width);
            StdDraw.setYscale(0, height);
        }
        StdDraw.clear(new Color(100, 150, 255));
        map.draw();
        for (Enemy enemy : level.getEnemies()) {
            enemy.draw();
        }
        mymario.draw(frameCount);
        if(Map.lowerflag==true){
            map.draw();
        }

        //drawing the hud
        StdDraw.setPenColor(30, 50, 100);
        StdDraw.filledRectangle(width / 2, 50, width / 2, 50);
        StdDraw.setPenColor(255, 255, 255);
        StdDraw.setFont(new Font("Arial", Font.PLAIN, 14));
        StdDraw.text(100, 70, "Level: " + (levelnum + 1));
        StdDraw.text(width / 2, 50, level.getClue());
        StdDraw.text(width - 100, 30, "Deaths: " + mymario.getDeathCount());
        StdDraw.text(width - 100, 50, "Time: " + getTime(gamestart));
        StdDraw.text(100, 50, "MOVE: [A] [D] [W]");
        StdDraw.text(100, 30, "RESTART: [R]");
        StdDraw.text(100, 10, "CAMERA: [C]");

    }

}
