//Name Surname: Sude Naz Aslan
//Student ID: 2024400336

import java.awt.event.KeyEvent;

public class Mario {

    public int x;
    public int y;
    public double speedY;
    public double gravity;

    private int size;
    private int deathCount;
    private boolean isDead;
    public String direction;
    private boolean canJump;

    boolean isWalking;

    private String marioStatus = "assets/standRight.png";

    public Mario(int x, int y) {
        this.x = x;
        this.y = y;
        this.speedY = 0;
        this.gravity = 1.2;
        this.size = 32;
        this.deathCount = 0;
        this.isDead = false;
        this.canJump = false;
        this.isWalking = false;
        this.direction = "right";

    }

    public int getSize() {
        return size;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public boolean isDefeated() {
        return isDead;
    }

    public int handleInput(boolean onGround, int frame) {
        isWalking = false;
        int move = 0;
        if (onGround) {
            canJump = true;
        } else {
            canJump = false; //to prevent mid air jump
        }
        //handles movements with buttons pressed
        if (StdDraw.isKeyPressed(KeyEvent.VK_A)) {
            isWalking = true;
            move = -5;
            direction = "left";
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_D)) {
            isWalking = true;

            move = 5;
            direction = "right";
        }
        if (StdDraw.isKeyPressed(KeyEvent.VK_W) && canJump) {
            jump();
        }
        return move;

    }

    public void jump() {
        if (canJump) {
            speedY = 17;
        }

        canJump = false;

    }

    //checks collision with enemies
    public boolean checkEnemyCollision(Enemy enemy) {
        int enemey_x = enemy.getX();
        int enemy_y = enemy.getY();
        int enemy_size = enemy.getSize();

        int distance_x = this.x - enemey_x;
        int distance_y = this.y - enemy_y;
        int distance = (this.size / 2) + (enemy_size / 2);

        return distance_x * distance_x + distance_y * distance_y < distance * distance;

    }
    //checks collision with bullets

    public boolean checkCollisionWithBullet(int bulletX, int bulletY) {
        int distance_x = this.x - bulletX;
        int distance_y = this.y - bulletY;
        int distance = this.size / 2;

        return distance_x * distance_x + distance_y * distance_y < distance * distance;
    }

    public void die() {
        isDead = true;
        deathCount++;
        speedY = 10;

    }

    public void spawn(int x, int y) {
        this.x = x;
        this.y = y;
        this.speedY = 0;
        isWalking = false;
        canJump = false;
    }

    public void respawn() {
        Map.lowerflag=false;

        spawn(60, 780); //spawning to first pipe after it dies or starts the game.
        isDead = false;
    }

    //returns appropriate image based on mario's state
    private void mariostat(int frame) {

        if (isDead) {
            marioStatus = "assets/dead.png";
        } else if(Map.lowerflag==true){
            marioStatus="assets/crouch.png";

        }else if (canJump == false) {
            if ("left".equals(direction)) {
                marioStatus = "assets/jumpLeft.png";
            } else {
                marioStatus = "assets/jumpRight.png";

            }

        } else if (isWalking) {
            //changes walking image every 6 frames for animation effect
            if ((frame % 10 < 5)) {
                if ("left".equals(direction)) {
                    marioStatus = "assets/walkLeft1.png";
                } else {
                    marioStatus = "assets/walkRight1.png";
                }
            } else {
                if ("left".equals(direction)) {
                    marioStatus = "assets/walkLeft2.png";
                } else {
                    marioStatus = "assets/walkRight2.png";
                }

            }
        } else {
            if ("left".equals(direction)) {
                marioStatus = "assets/standLeft.png";
            } else {
                marioStatus = "assets/standRight.png";
            }
        }

    }

    public void draw(int frame) {
        mariostat(frame);

        StdDraw.picture(x, y + 3, marioStatus, size, size + 5);
    }
}
