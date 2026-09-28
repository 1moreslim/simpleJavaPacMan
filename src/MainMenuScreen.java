import javax.swing.*;
import java.awt.*;

class MainMenuScreen extends JPanel {
    private Container parent;

    public MainMenuScreen(Container parent) {
        this.parent = parent;
        setBackground(Color.BLACK);
        setLayout(new GridBagLayout());
        GridBagConstraints grid = new GridBagConstraints();
        grid.gridwidth = GridBagConstraints.REMAINDER;
        grid.insets = new Insets(20, 20, 20, 20);
        grid.anchor = GridBagConstraints.CENTER;


        JLabel menuPacman = new JLabel(new ImageIcon("C:\\Users\\selimvural\\IdeaProjects\\pcm\\PNGS\\MainMenu_sign.png"), SwingConstants.CENTER);
        menuPacman.setOpaque(true);
        add(menuPacman, grid);



        JButton newGame = new JButton(new ImageIcon("C:\\Users\\selimvural\\IdeaProjects\\pcm\\PNGS\\NewGame.png"));
        newGame.setPreferredSize(new Dimension(100, 50));
        JButton highScores = new JButton(new ImageIcon("C:\\Users\\selimvural\\IdeaProjects\\pcm\\PNGS\\HighScore.png"));
        highScores.setPreferredSize(new Dimension(100, 50));
        JButton quit = new JButton(new ImageIcon("C:\\Users\\selimvural\\IdeaProjects\\pcm\\PNGS\\Exit.png"));
        quit.setPreferredSize(new Dimension(100, 50));

        add(newGame, grid);
        add(highScores, grid);
        add(quit, grid);

        newGame.addActionListener(e -> {
            JPanel sizePanel = new JPanel(new GridLayout(2, 2));
            sizePanel.add(new JLabel("Rows (10-100):"));
            JSpinner rowsSpinner = new JSpinner(new SpinnerNumberModel(20, 10, 100, 1));
            sizePanel.add(rowsSpinner);
            sizePanel.add(new JLabel("Columns (10-100):"));
            JSpinner colsSpinner = new JSpinner(new SpinnerNumberModel(20, 10, 100, 1));
            sizePanel.add(colsSpinner);

            int result = JOptionPane.showConfirmDialog(this, sizePanel, "Select Board Size",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                int rows = (int) rowsSpinner.getValue();
                int cols = (int) colsSpinner.getValue();
                GamePhys.getInstance().startingGame(rows, cols);
                CardLayout cl = (CardLayout) parent.getLayout();
                GameView gameView = GamePhys.getInstance().getView();
                parent.add(gameView, "Game");
                cl.show(parent, "Game");
                gameView.requestFocusInWindow();
            }
        });

        highScores.addActionListener(e -> {
            JList<HighScore> scoresList = new JList<>(new DefaultListModel<>());
            DefaultListModel<HighScore> listModel = (DefaultListModel<HighScore>) scoresList.getModel();
            for (HighScore entry : GamePhys.getInstance().getHighScores()) {
                listModel.addElement(entry);
            }
            JScrollPane scrollPane = new JScrollPane(scoresList);
            scrollPane.setPreferredSize(new Dimension(300, 400));
            JOptionPane.showMessageDialog(this, scrollPane, "High Scores", JOptionPane.PLAIN_MESSAGE);
        });

        quit.addActionListener(e -> System.exit(0));
    }
}