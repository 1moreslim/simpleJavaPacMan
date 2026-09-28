import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

 class GamePhys {
    private static GamePhys instance;
    private GameModel model;
    private GameView view;
    private Thread pacmanThr, ghostThr, upgrd, animationThr;
    private Random random = new Random();
    private List<HighScore> highScores;

    private GamePhys() {
        highScores = loadHighScores();
    }

    public static GamePhys getInstance() {
        if (instance == null) instance = new GamePhys();
        return instance;
    }

    public void startingGame(int rows, int cols) {
        model = new GameModel(rows, cols);
        view = new GameView(model);
        startThreads();
    }

    private void startThreads() {
        pacmanThr = new Thread(() -> {
            while (!model.isGameOver()) {
                try {
                    int delay = model.getActiveUpgrades()[0] ? 100 : 300; // Speed boost
                    Thread.sleep(delay);
                    synchronized (model) {
                        view.repaintTable();
                    }
                } catch (InterruptedException e) {
                    JOptionPane.showMessageDialog(null, "Pacman thread error: " + e.getMessage());
                }
            }
        });

        ghostThr = new Thread(() -> {
            while (!model.isGameOver()) {
                try {
                    Thread.sleep(450); // Ghosts move slower
                    synchronized (model) {
                        ghostMove();
                        touchWithGhost();
                        view.updateStats();
                        isLevelFinished();
                    }
                } catch (InterruptedException e) {
                    JOptionPane.showMessageDialog(null, "Ghost thread error: " + e.getMessage());
                }
            }
        });

        upgrd = new Thread(() -> {
            while (!model.isGameOver()) {
                try {
                    Thread.sleep(5000); // Every 5 seconds in a path upgrades will be available for pacman
                    synchronized (model) {
                        if (random.nextInt(4) == 0) { // 25% chance
                            randomSpawnForUpgrades();
                        }
                        for (int i = 0; i < model.getUpgradeDurations().length; i++) {
                            if (model.getUpgradeDurations()[i] > 0) {
                                model.getUpgradeDurations()[i]--;
                                if (model.getUpgradeDurations()[i] == 0) {
                                    model.getActiveUpgrades()[i] = false;
                                }
                            }
                        }
                        view.updateStats();
                    }
                } catch (InterruptedException e) {
                    JOptionPane.showMessageDialog(null, "Upgrade error: " + e.getMessage());
                }
            }
        });

        animationThr = new Thread(() -> {
            while (!model.isGameOver()) {
                try {
                    Thread.sleep(200); // Animation window every 200ms
                    synchronized (model) {
                        model.nextpacmanFps();
                        view.repaintTable();
                    }
                } catch (InterruptedException e) {
                    JOptionPane.showMessageDialog(null, "Animation error: " + e.getMessage());
                }
            }
        });

        pacmanThr.start();
        ghostThr.start();
        upgrd.start();
        animationThr.start();
    }

    private void ghostMove() {
        if (model.getActiveUpgrades()[1]) return; // Ghosts frozen
        for (int i = 0; i < model.getghostPos().length; i++) {
            int gx = model.getghostPos()[i][0];
            int gy = model.getghostPos()[i][1];
            if (model.getMaze()[gy][gx] == 3) {
                model.getMaze()[gy][gx] = 0;
            }
            int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            arrayShuffling(dirs);
            double minimumDisctance = Double.MAX_VALUE;
            int xX = gx, yY = gy;
            for (int[] d : dirs) {
                int nx = gx + d[0], ny = gy + d[1];
                if (nx >= 0 && ny >= 0 && nx < model.getMaze()[0].length && ny < model.getMaze().length && model.getMaze()[ny][nx] != 1) {
                    double distance = Math.pow(model.getPacmanX() - nx, 2) + Math.pow(model.getPacmanY() - ny, 2);
                    if (distance < minimumDisctance) {
                        minimumDisctance = distance;
                        xX = nx;
                        yY = ny;
                    }
                }
            }
            model.setGhostPosition(i, xX, yY);
            if (model.getMaze()[yY][xX] == 0) {
                model.getMaze()[yY][xX] = 3;
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

    private void randomSpawnForUpgrades() {
        for (int[] upgrade : model.getUpgrades()) {
            if (model.getMaze()[upgrade[2]][upgrade[1]] == 5) {
                model.getMaze()[upgrade[2]][upgrade[1]] = 0;
            }
        }
        model.getUpgrades().clear();
        int x, y;
        do {
            x = random.nextInt(model.getMaze()[0].length);
            y = random.nextInt(model.getMaze().length);
        } while (model.getMaze()[y][x] != 0);
        int type = random.nextInt(5); // 5 upgrade types
        model.getUpgrades().add(new int[]{type, x, y});
        model.getMaze()[y][x] = 5;
    }

    public void pacmanMovements(int dx, int dy) {
        synchronized (model) {
            int nx = model.getPacmanX() + dx;
            int ny = model.getPacmanY() + dy;
            if (nx >= 0 && ny >= 0 && nx < model.getMaze()[0].length && ny < model.getMaze().length && model.getMaze()[ny][nx] != 1) {
                if (model.getMaze()[ny][nx] == 5) {
                    upgrades(nx, ny);
                }
                model.getMaze()[model.getPacmanY()][model.getPacmanX()] = 0;
                if (model.getMaze()[ny][nx] == 4) {
                    model.addScore(model.getActiveUpgrades()[2] ? 20 : 10); // Double points
                    model.collect();
                }
                model.setPacmanPosition(nx, ny);
                model.getMaze()[ny][nx] = 2;
                view.repaintTable();
                view.updateStats();
            }
        }
    }

    private void upgrades(int x, int y) {
        for (int[] upgrade : model.getUpgrades()) {
            if (upgrade[1] == x && upgrade[2] == y) {
                int type = upgrade[0];
                model.getActiveUpgrades()[type] = true;
                model.getUpgradeDurations()[type] = 15; // 15 seconds
                model.getMaze()[y][x] = 0;
                String[] names = {"Speed Boost", "Ghost Freeze", "Double Points", "Invincibility", "Extra Life"};
                JOptionPane.showMessageDialog(view, "Upgrade Collected: " + names[type]);
                if (type == 4) model.loseLife(); // Extra life (reverses loseLife to gain one)
                view.updateStats();
                break;
            }
        }
    }

    private void touchWithGhost() {
        if (model.getActiveUpgrades()[3]) return; // Invincibility
        for (int[] ghost : model.getghostPos()) {
            if (ghost[0] == model.getPacmanX() && ghost[1] == model.getPacmanY()) {
                model.loseLife();
                if (model.isGameOver()) {
                    endGame();
                }
            }
        }
    }

    private void isLevelFinished() {
        if (model.getCollectedCollectables() >= model.getTotalCollectables()) {
            model.setGameOver(true);
            endGame();
        }
    }

    private void endGame() {
        pacmanThr.interrupt();
        ghostThr.interrupt();
        upgrd.interrupt();
        animationThr.interrupt();
        String name = JOptionPane.showInputDialog(view, "Game Over! Nickname please: ");
        if (name != null && !name.trim().isEmpty()) {
            highScores.add(new HighScore(name, model.getScore()));
            Collections.sort(highScores, (a, b) -> b.getScore() - a.getScore());
            saveHighScores();
        }
        view.returnToMainMenu();
    }

    public List<HighScore> getHighScores() {
        return highScores; }

    private void saveHighScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("highscores.dat"))) {
            oos.writeObject(highScores);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving high scores: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private List<HighScore> loadHighScores() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("highscores.dat"))) {
            return (List<HighScore>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public GameModel getModel() {
        return model; }
    public GameView getView() {
        return view; }
}