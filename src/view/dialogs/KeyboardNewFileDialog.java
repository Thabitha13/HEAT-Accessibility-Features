package view.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.SwingUtilities;
import java.io.*;

/**
 * Keyboard-driven dialog for creating a new Haskell file.
 * Eliminates mouse use: Tab navigates fields, Enter confirms, Esc cancels.
 * Creates the target directory automatically if it does not exist.
 */
public class KeyboardNewFileDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private JTextField filenameField;
    private JTextField directoryField;
    private File result = null;

    public KeyboardNewFileDialog(Frame owner) {
        super(owner, "Create new Haskell file", true);
        buildUI();
        setSize(500, 220);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        getRootPane().getAccessibleContext().setAccessibleName(
            "Create new Haskell file dialog. Enter file name, then directory. Press Enter to create, Escape to cancel.");
    }

    private void buildUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        panel.add(new JLabel("File name:"), c);
        filenameField = new JTextField(28);
        filenameField.getAccessibleContext().setAccessibleName(
            "File name. Type the name of your new Haskell file. A .hs extension is added automatically.");
        c.gridx = 1; c.weightx = 1;
        panel.add(filenameField, c);

        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        panel.add(new JLabel("Directory:"), c);
        directoryField = new JTextField(System.getProperty("user.home"), 28);
        directoryField.getAccessibleContext().setAccessibleName(
            "Save directory. Enter the folder path. Type tilde slash for home. The folder is created if it does not exist.");
        directoryField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                SwingUtilities.invokeLater(() ->
                    directoryField.setCaretPosition(directoryField.getText().length()));
            }
        });
        c.gridx = 1; c.weightx = 1;
        panel.add(directoryField, c);

        c.gridx = 1; c.gridy = 2;
        JLabel hint = new JLabel("Tip: use ~/Documents/MyProject  —  directory is created if missing");
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 10f));
        hint.setForeground(Color.GRAY);
        panel.add(hint, c);

        JButton createBtn = new JButton("Create");
        JButton cancelBtn = new JButton("Cancel");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.add(createBtn);
        buttons.add(cancelBtn);
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2; c.fill = GridBagConstraints.NONE; c.anchor = GridBagConstraints.EAST;
        panel.add(buttons, c);

        createBtn.addActionListener(e -> attemptCreate());
        cancelBtn.addActionListener(e -> dispose());
        filenameField.addActionListener(e -> directoryField.requestFocus());
        directoryField.addActionListener(e -> attemptCreate());

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
        getRootPane().getActionMap().put("cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { dispose(); }
        });
        getRootPane().setDefaultButton(createBtn);
        setContentPane(panel);
    }

    private void attemptCreate() {
        String name = filenameField.getText().trim();
        String dir  = directoryField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a file name.", "Missing name", JOptionPane.WARNING_MESSAGE);
            filenameField.requestFocus();
            return;
        }
        if (!name.endsWith(".hs") && !name.endsWith(".lhs") &&
            !name.endsWith(".HS") && !name.endsWith(".LHS")) {
            name += ".hs";
        }
        if (dir.startsWith("~")) {
            dir = System.getProperty("user.home") + dir.substring(1);
        }

        File directory = new File(dir);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                JOptionPane.showMessageDialog(this,
                    "Could not create directory: " + directory.getAbsolutePath(),
                    "Directory error", JOptionPane.ERROR_MESSAGE);
                directoryField.requestFocus();
                return;
            }
        }

        File target = new File(directory, name);
        if (target.exists()) {
            int choice = JOptionPane.showConfirmDialog(this,
                "'" + name + "' already exists in that directory. Overwrite?",
                "File exists", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                filenameField.requestFocus();
                return;
            }
        }

        result = target;
        dispose();
    }

    public File getResult() { return result; }

    public static File show(Frame owner) {
        KeyboardNewFileDialog dlg = new KeyboardNewFileDialog(owner);
        dlg.setVisible(true);
        return dlg.getResult();
    }
}
