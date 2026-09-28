import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class GameModel {
    private int[][] maze;
    private int pacmanX, pacmanY;
    private int[][] ghostPos;
    private int score, lives, rows, cols;
    private long startTime;
    private int totalCollectables, collectedCollectables;
    private List<int[]> upgrades; // [type, x, y] max 2 upgrades in a same time
    private boolean[] activeUpgrades; // 5 upgrades
    private int[] upgradeDurations; // timer for upgrades
    private Random random = new Random();
    private boolean gameOver;
    private int pacmanFps; // animations

    public GameModel(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        maze = new int[rows][cols];
        ghostPos = new int[4][2];
        score = 0;
        lives = 3;
        startTime = System.currentTimeMillis();
        totalCollectables = 0;
        collectedCollectables = 0;
        upgrades = new ArrayList<>();
        activeUpgrades = new boolean[5]; // pacman speed, ghost freeze, Double point, Invincibility against ghost, Extra Life
        upgradeDurations = new int[5];
        gameOver = false;
        pacmanFps = 0;
        mazeAlg();
        pointsPlaces();
    }

    public void mazeAlg() {
        for (int y = 0; y < rows; y++) Arrays.fill(maze[y], 1);
        mazeCavities(1, 1);
        maze[pacmanY][pacmanX] = 2; // Pacman
        for (int i = 0; i < ghostPos.length; i++) {
            maze[ghostPos[i][1]][ghostPos[i][0]] = 3; // Ghosts
        }
    }

    private void mazeCavities(int x, int y) {
        int[][] dirs = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}};
        arrayShuffling(dirs);
        if (maze[y][x] == 1) {
            maze[y][x] = 0;
            pacmanX = x;
            pacmanY = y;
        }
        int ghostCount = 0;
        for (int[] dir : dirs) {
            int nx = x + dir[0], ny = y + dir[1];
            if (ny > 0 && ny < rows - 1 && nx > 0 && nx < cols - 1 && maze[ny][nx] == 1) {
                maze[ny][nx] = 0;
                maze[y + dir[1]/2][x + dir[0]/2] = 0;
                if (ghostCount < 4 && random.nextInt(10) < 2) {
                    ghostPos[ghostCount][0] = nx;
                    ghostPos[ghostCount][1] = ny;
                    ghostCount++;
                }
                mazeCavities(nx, ny);
            }
        }
    }

    private void arrayShuffling(int[][] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    private void pointsPlaces() {
        totalCollectables = (int) (rows * cols * 0.10);
        int placed = 0;
        while (placed < totalCollectables) {
            int x = random.nextInt(cols);
            int y = random.nextInt(rows);
            if (maze[y][x] == 0) {
                maze[y][x] = 4;
                placed++;
            }
        }
    }

    // Getters and setters
    public int[][] getMaze() {
        return maze; }
    public int getPacmanX() {
        return pacmanX; }
    public int getPacmanY() {
        return pacmanY; }
    public void setPacmanPosition(int x, int y) {
        pacmanX = x; pacmanY = y; }
    public int[][] getghostPos() {
        return ghostPos; }
    public void setGhostPosition(int index, int x, int y) {
        ghostPos[index][0] = x; ghostPos[index][1] = y; }
    public int getScore() {
        return score; }
    public void addScore(int points) {
        score += points; }
    public int getLives() {
        return lives; }
    public void loseLife() {
        lives--; if (lives <= 0) gameOver = true; }
    public long getTime() {
        return (System.currentTimeMillis() - startTime) / 1000; }
    public int getTotalCollectables() {
        return totalCollectables; }
    public int getCollectedCollectables() {
        return collectedCollectables; }
    public void collect() {
        collectedCollectables++; }
    public List<int[]> getUpgrades() {
        return upgrades; }
    public boolean[] getActiveUpgrades() {
        return activeUpgrades; }
    public int[] getUpgradeDurations() {
        return upgradeDurations; }
    public boolean isGameOver() {
        return gameOver; }
    public void setGameOver(boolean over) {
        gameOver = over; }
    public int getpacmanFps() {
        return pacmanFps; }
    public void nextpacmanFps() {
        pacmanFps = (pacmanFps + 1) % 3; }
}