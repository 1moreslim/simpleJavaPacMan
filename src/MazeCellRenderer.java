import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

class MazeCellRenderer extends DefaultTableCellRenderer {
    private ImageIcon[] pacmanPngs = new ImageIcon[3];
    private ImageIcon[] ghostPngs = new ImageIcon[3];
    private ImageIcon collectablesIcon;
    private ImageIcon upgradeIcon;
    private int cellSize = 20;

    public MazeCellRenderer() {
        try {
            pacmanPngs[0] = new ImageIcon("C:\\Users\\selimvural\\IdeaProjects\\pcm\\PNGS\\Assets\\pacman1.png");
            pacmanPngs[1] = new ImageIcon("C:\\Users\\selimvural\\IdeaProjects\\pcm\\PNGS\\Assets\\pacman2.png");
            pacmanPngs[2] = new ImageIcon("C:\\Users\\selimvural\\IdeaProjects\\pcm\\PNGS\\Assets\\pacman3.png");
            ghostPngs[0] = new ImageIcon("C:\\Users\\selimvural\\IdeaProjects\\pcm\\PNGS\\Assets\\ghost1.png");
            ghostPngs[1] = new ImageIcon("C:\\Users\\selimvural\\IdeaProjects\\pcm\\PNGS\\Assets\\ghost2.png");
            ghostPngs[2] = new ImageIcon("C:\\Users\\selimvural\\IdeaProjects\\pcm\\PNGS\\Assets\\ghost3.png");
            collectablesIcon = new ImageIcon("C:\\Users\\selimvural\\IdeaProjects\\pcm\\PNGS\\Assets\\collectables.png");
            upgradeIcon = new ImageIcon("C:\\Users\\selimvural\\IdeaProjects\\pcm\\PNGS\\Assets\\upgrade.png");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Images cannot found in this path please be sure: " + e.getMessage());
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        int cellValue = (int) value;
        label.setIcon(null);
        label.setText("");
        label.setHorizontalAlignment(CENTER);

        switch (cellValue) {
            case 1: // Walls
                label.setBackground(Color.DARK_GRAY);
                break;
            case 2: // Pacman
                label.setBackground(Color.BLACK);
                if (pacmanPngs[GamePhys.getInstance().getModel().getpacmanFps()] != null) {
                    label.setIcon(new ImageIcon(pacmanPngs[GamePhys.getInstance().getModel().getpacmanFps()].getImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
                } else {
                    label.setBackground(Color.ORANGE);
                }
                break;
            case 3: // Ghosts
                label.setBackground(Color.BLACK);
                if (ghostPngs[0] != null) {
                    label.setIcon(new ImageIcon(ghostPngs[row % 3].getImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
                } else {
                    label.setBackground(Color.BLUE);
                }
                break;
            case 4: // Collectables
                label.setBackground(Color.BLACK);
                if (collectablesIcon != null) {
                    label.setIcon(new ImageIcon(collectablesIcon.getImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
                } else {
                    label.setBackground(Color.YELLOW);
                }
                break;
            case 5: // Upgrades
                label.setBackground(Color.BLACK);
                if (upgradeIcon != null) {
                    label.setIcon(new ImageIcon(upgradeIcon.getImage().getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
                } else {
                    label.setBackground(Color.GREEN);
                }
                break;
            default: // Path and connections with other paths
                label.setBackground(Color.BLACK);
        }
        setBorder(BorderFactory.createEmptyBorder());
        return label;
    }
}