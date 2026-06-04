package view.windows;

import utils.Resources;
import managers.ActionManager;

import java.awt.*;
import java.awt.event.*;
import java.awt.KeyboardFocusManager;
import java.awt.KeyEventDispatcher;
import javax.swing.*;
import javax.swing.JComponent;

/**
 * Startup splash screen. Stays visible until the user acts:
 *   N or O  — dismiss and open the file chooser
 *   Escape  — dismiss and continue to the main window as-is
 */
public class SplashWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private JLabel statusLabel;
    private KeyEventDispatcher keyDispatcher;

    public SplashWindow() {
        setUndecorated(true);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createLineBorder(new Color(80, 200, 80), 2));

        // Logo — scaled to leave room for option labels
        ImageIcon splashIcon = Resources.getIcon("splash");
        JLabel logoLabel;
        if (splashIcon != null) {
            Image scaled = splashIcon.getImage()
                    .getScaledInstance(280, 140, Image.SCALE_SMOOTH);
            logoLabel = new JLabel(new ImageIcon(scaled), JLabel.CENTER);
        } else {
            logoLabel = new JLabel("HEAT", JLabel.CENTER);
            logoLabel.setForeground(Color.WHITE);
            logoLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
        }
        logoLabel.setBorder(BorderFactory.createEmptyBorder(14, 20, 6, 20));
        panel.add(logoLabel, BorderLayout.NORTH);

        // Keyboard option labels
        JPanel optionsPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        optionsPanel.setBackground(new Color(30, 30, 30));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        String k = System.getProperty("os.name", "").toLowerCase().contains("mac") ? "Cmd" : "Ctrl";
        optionsPanel.add(buildOptionLabel(k + "+N   —   New Haskell file"));
        optionsPanel.add(buildOptionLabel(k + "+O   —   Open existing file"));
        panel.add(optionsPanel, BorderLayout.CENTER);

        // Hint + status at the bottom
        JPanel southPanel = new JPanel(new GridLayout(2, 1));
        southPanel.setBackground(new Color(30, 30, 30));

        JLabel escHint = new JLabel("Press Esc to skip", JLabel.CENTER);
        escHint.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 10));
        escHint.setForeground(new Color(100, 100, 100));
        southPanel.add(escHint);

        statusLabel = new JLabel("GHCi starting...", JLabel.CENTER);
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        statusLabel.setForeground(new Color(160, 160, 160));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        southPanel.add(statusLabel);

        panel.add(southPanel, BorderLayout.SOUTH);

        setContentPane(panel);
        setSize(420, 320);
        setLocationRelativeTo(null);
        bindKeys();

        String k2 = System.getProperty("os.name", "").toLowerCase().contains("mac") ? "Cmd" : "Ctrl";
        getRootPane().getAccessibleContext().setAccessibleName(
            "Welcome to HEAT. Press " + k2 + " N for a new file. Press " + k2 + " O to open an existing file. Press Escape to continue.");
    }

    private JLabel buildOptionLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        label.setOpaque(true);
        label.setBackground(new Color(50, 50, 50));
        label.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        return label;
    }

    private void bindKeys() {
        int menu = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        keyDispatcher = new KeyEventDispatcher() {
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() != KeyEvent.KEY_PRESSED) return false;
                int code = e.getKeyCode();
                int mods = e.getModifiersEx();
                if (code == KeyEvent.VK_N && (mods & menu) != 0) {
                    dismiss();
                    ActionManager.getInstance().triggerNewFile();
                    return true;
                }
                if (code == KeyEvent.VK_O && (mods & menu) != 0) {
                    dismiss();
                    ActionManager.getInstance().triggerOpenFile();
                    return true;
                }
                if (code == KeyEvent.VK_ESCAPE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(keyDispatcher);
    }

    public void setStatus(String message) {
        statusLabel.setText(message);
    }

    public void dismiss() {
        if (keyDispatcher != null) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager()
                    .removeKeyEventDispatcher(keyDispatcher);
            keyDispatcher = null;
        }
        setVisible(false);
        dispose();
    }
}
