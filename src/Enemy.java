//Name Surname: Sude Naz Aslan
//Student ID: 2024400336

import java.util.ArrayList;
import java.util.Random;

public class Enemy {

    private int x;
    private int y;
    private int size;
    private int x1;
    private int x2;
    private int direction;
    private int speed;
    // bullet data stored in ArrayLists: x, y, speedX, speedY, active
    public ArrayList<Integer> bulletX = new ArrayList<>();
    public ArrayList<Integer> bulletY = new ArrayList<>();
    public ArrayList<Integer> bulletSpeedX = new ArrayList<>();
    public ArrayList<Integer> bulletSpeedY = new ArrayList<>();
    public ArrayList<Boolean> bulletActive = new ArrayList<>();

    public boolean canShoot;

//constructor for custom enemy
    public Enemy(int x, int y, boolean canShoot) {
        this.x = x;
        this.y = y;
        this.size = 40;
        this.canShoot = canShoot;

    }
//normal enemy constructor

    public Enemy(int x, int y, int size, int direction, int x1, int x2) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.x1 = x1;
        this.x2 = x2;
        this.direction = direction;
        this.speed = 2;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }

    public void move() {

        if (x + speed * direction <= x2 && x + speed * direction >= x1) {
            x += direction * speed;
        } else {
            direction *= -1;

        }

    }

    public void draw() {
        if (canShoot) {
            StdDraw.picture(x, y, "assets/images.png", this.size , this.size );
        } else {
            StdDraw.picture(x, y, "assets/mushroom.png", this.size , this.size );
        }
    }
//disappear and appear methods for custom enemy

    public void disappear() {
        this.size = 0;
        this.x = 0;
        this.y = 0;

    }

    public void appear() {
        //randomly respawn at one of the predefined locations
        int[][] poz = {{390, 300}, {740, 700}, {220, 660}, {290, 580}, {490, 300}, {290, 260}, {490, 620}};
        Random rand = new Random();
        int[] newPoz = poz[rand.nextInt(poz.length)];
        this.x = newPoz[0];
        this.y = newPoz[1];
        this.size = 40;
    }

    public void randomshut(int targetX, int targetY) {
//calculates angle and speed for the bullet to move towards the target.
        double angle = Math.atan2(targetY - y, targetX - x);
        int bulletSpeed = 5;
        int speedX = (int) (Math.cos(angle) * bulletSpeed)*2;
        int speedY = (int) (Math.sin(angle) * bulletSpeed)*2;
        bulletX.add(x);
        bulletY.add(y);
        bulletSpeedX.add(speedX);
        bulletSpeedY.add(speedY);
        bulletActive.add(true);

        disappear();
        appear();

    }

}
