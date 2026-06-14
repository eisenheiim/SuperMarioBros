//Name Surname: Sude Naz Aslan
//Student ID: 2024400336
import java.util.ArrayList;

public class Level {

    private int id;
    private ArrayList<Enemy> enemies;
    private String clue;
    private int[][] coins;

    public Level(int id, ArrayList<Enemy> enemies, String clue, int[][] coins) {
        this.id = id;
        this.enemies = enemies;
        this.clue = clue;
        this.coins = coins;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public String getClue() {
        return clue;
    }

    public int[][] getCoins() {
        return coins;
    }
}
