//Name Surname: Sude Naz Aslan
//Student ID: 2024400336
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    static int canvas_width = 800;
    static int canvas_height = 800;

    public static void main(String[] args) {
        //setting the canvas
        StdDraw.setCanvasSize(canvas_width, canvas_height);
        StdDraw.setXscale(0.0, canvas_width);
        StdDraw.setYscale(0.0, canvas_height);
        StdDraw.enableDoubleBuffering();

        //starting with menu screen.
        // Load map data from files
        int[][] obstacles = loadData("data/obstacles.txt");
        int[][] pipes = loadData("data/pipes.txt");
        int[][] portals = loadData("data/portals.txt");

        // Create map 
        Map map = new Map(obstacles, pipes, portals);

        // Create levels
        ArrayList<Level> levels = new ArrayList<>();

        ArrayList<Enemy> level1Enemies = new ArrayList<>();
        Level level1 = new Level(1, level1Enemies, "Reach the exit pipe!", new int[0][4]);
        levels.add(level1);


        //adding enemies
        ArrayList<Enemy> level2Enemies = new ArrayList<>();
        Enemy e1 = new Enemy(370, 300, 40, 1, 370, 490);
        Enemy e2 = new Enemy(290, 580, 40, 1, 290, 430);
        level2Enemies.add(e1);
        level2Enemies.add(e2);

        Level level2 = new Level(2, level2Enemies, "Avoid the enemies!", new int[0][4]);
        levels.add(level2);

        ArrayList<Enemy> level3Enemies = new ArrayList<>();
        int[][] level3Coins = loadData("data/coins.txt");
        level3Enemies.add(e1);
        level3Enemies.add(e2);
        Level level3 = new Level(3, level3Enemies, "Collect all coins!", level3Coins);
        levels.add(level3);

        ArrayList<Enemy> level4Enemies = new ArrayList<>();

        level4Enemies.add(new Enemy(290, 580, true));
        level4Enemies.add(new Enemy(490, 300, true));
        level4Enemies.add(new Enemy(290, 260, true));
        level4Enemies.add(new Enemy(490, 620, true));

        Level level4 = new Level(4, level4Enemies, "Custom Level!", new int[0][4]);
        levels.add(level4);

        Game game = new Game(levels, map);

        game.run();
    }

    private static int[][] loadData(String filename) {
        ArrayList<int[]> data = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length == 4) {
                    int[] record = new int[4];
                    for (int i = 0; i < 4; i++) {
                        record[i] = Integer.parseInt(parts[i].trim());
                    }
                    data.add(record);
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not load file " + filename + ": " + e.getMessage());
        }

        return data.toArray(new int[0][4]);
    }
}
