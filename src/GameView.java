import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class GameView extends JPanel {
    private GameModel model;
    private JTable mazeTable;
    private MazeModel tableModel;
    private JLabel scoreLabel, timeLabel, livesLabel, upgradesLabel;
    private Container parent;

    public GameView(GameModel model) {
        this.model = model;
        this.parent = SwingUtilities.getAncestorOfClass(Container.class, this);
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        tableModel = new MazeModel(model.getMaze());
        mazeTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (int i = 0; i < mazeTable.getColumnModel().getColumnCount(); i++) {
            mazeTable.getColumnModel().getColumn(i).setHeaderValue((char) ('A' + i));
            mazeTable.getColumnModel().getColumn(i).setPreferredWidth(20);
            mazeTable.getColumnModel().getColumn(i).setMinWidth(20);
            mazeTable.getColumnModel().getColumn(i).setMaxWidth(20);
        }
        mazeTable.getTableHeader().setReorderingAllowed(false);
        mazeTable.getTableHeader().setResizingAllowed(false);
        mazeTable.setRowHeight(20);
        mazeTable.setShowGrid(false);
        mazeTable.setIntercellSpacing(new Dimension(0, 0));
        mazeTable.setDefaultRenderer(Object.class, new MazeCellRenderer());
        JScrollPane scrollPane = new JScrollPane(mazeTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel statsPanel = new JPanel();
        statsPanel.setBackground(Color.BLACK);
        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        scoreLabel.setForeground(Color.YELLOW);
        timeLabel = new JLabel("Time: 0s", SwingConstants.CENTER);
        timeLabel.setForeground(Color.YELLOW);
        livesLabel = new JLabel("Lives: 3", SwingConstants.CENTER);
        livesLabel.setForeground(Color.YELLOW);
        upgradesLabel = new JLabel("Upgrades: None", SwingConstants.CENTER);
        upgradesLabel.setForeground(Color.YELLOW);
        statsPanel.add(scoreLabel);
        statsPanel.add(timeLabel);
        statsPanel.add(livesLabel);
        statsPanel.add(upgradesLabel);
        add(statsPanel, BorderLayout.NORTH);

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_Q && e.isControlDown() && e.isShiftDown()) {
                    GamePhys.getInstance().getModel().setGameOver(true);
                    returnToMainMenu();
                } else {
                    int dx = 0, dy = 0;
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP: dy = -1; break;
                        case KeyEvent.VK_DOWN: dy = 1; break;
                        case KeyEvent.VK_LEFT: dx = -1; break;
                        case KeyEvent.VK_RIGHT: dx = 1; break;
                    }
                    GamePhys.getInstance().pacmanMovements(dx, dy);
                }
            }
        });
    }

    public void updateStats() {
        scoreLabel.setText("Score: " + model.getScore());
        timeLabel.setText("Time: " + model.getTime() + "s");
        livesLabel.setText("Lives: " + model.getLives());
        StringBuilder sb = new StringBuilder("Upgrades: ");
        if (model.getActiveUpgrades()[0]) sb.append("Speed(").append(model.getUpgradeDurations()[0]).append(") ");
        if (model.getActiveUpgrades()[1]) sb.append("Freeze(").append(model.getUpgradeDurations()[1]).append(") ");
        if (model.getActiveUpgrades()[2]) sb.append("Double(").append(model.getUpgradeDurations()[2]).append(") ");
        if (model.getActiveUpgrades()[3]) sb.append("Invinc(").append(model.getUpgradeDurations()[3]).append(") ");
        if (model.getActiveUpgrades()[4]) sb.append("Life(").append(model.getUpgradeDurations()[4]).append(") ");
        upgradesLabel.setText(sb.toString().equals("Upgrades: ") ? "Upgrades: None" : sb.toString());
    }

    public void repaintTable() {
        tableModel.fireTableDataChanged();
    }

    public void returnToMainMenu() {
        CardLayout cl = (CardLayout) parent.getLayout();
        cl.show(parent, "MainMenu");
    }
}
