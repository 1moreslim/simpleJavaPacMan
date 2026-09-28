import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PacManGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {JFrame window = new JFrame("Pac-Man");
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                Container contentPane = window.getContentPane();
                contentPane.setLayout(new CardLayout());
                contentPane.add(new MainMenuScreen(contentPane), "MainMenu");
                window.setSize(800, 600);
                window.setLocationRelativeTo(null);
                // ctrl+shift+q to main menu from any of screen
                KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

                    @Override
                    public boolean dispatchKeyEvent(KeyEvent e) {
                        if (e.getID() == KeyEvent.KEY_PRESSED &&
                                e.getKeyCode() == KeyEvent.VK_Q &&
                                e.isControlDown() &&
                                e.isShiftDown()) {
                            if (GamePhys.getInstance().getModel() != null) {
                                GamePhys.getInstance().getModel().setGameOver(true);
                            }
                            CardLayout cl = (CardLayout) contentPane.getLayout();
                            cl.show(contentPane, "MainMenu");
                            Component mainMenu = contentPane.getComponent(0);
                            if (mainMenu != null) {
                                mainMenu.requestFocusInWindow();
                            }
                            return true;
                        }
                        return false;
                    }
                });

                window.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error:  " + e.getMessage()); //starting error
            }
        });
    }
}