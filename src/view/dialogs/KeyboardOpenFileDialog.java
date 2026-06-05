package view.dialogs;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Spotlight-style keyboard-driven dialog for opening an existing Haskell file.
 * User types a filename (partial match is fine); optional directory narrows the search.
 * Matching files appear in a scrollable list — arrow keys + Enter to select.
 * No mouse required: Tab navigates, Enter searches or opens, Esc cancels.
 */
public class KeyboardOpenFileDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final int MAX_RESULTS = 50;
    private static final int MAX_DEPTH   = 6;

    private JTextField filenameField;
    private JTextField directoryField;
    private DefaultListModel<File> listModel;
    private JList<File> resultList;
    private JLabel statusLabel;
    private File result = null;

    public KeyboardOpenFileDialog(Frame owner) {
        super(owner, "Open Haskell file", true);
        buildUI();
        setSize(540, 400);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        getRootPane().getAccessibleContext().setAccessibleName(
            "Open Haskell file dialog. Type a file name and press Enter to search. " +
            "Use arrow keys to select from results. Press Enter to open the selected file.");
    }

    private void buildUI() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 8, 14));

        // ── Input rows ──────────────────────────────────────────────────────
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: label | filename field | Search button
        c.gridx = 0; c.gridy = 0; c.weightx = 0; c.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(new JLabel("File name:"), c);
        filenameField = new JTextField(18);
        filenameField.getAccessibleContext().setAccessibleName(
            "File name to search for. Type part of the file name, then press Enter or Search.");
        c.gridx = 1; c.weightx = 1;
        inputPanel.add(filenameField, c);
        JButton searchBtn = new JButton("Search");
        searchBtn.getAccessibleContext().setAccessibleName("Search. Press to find files matching the name you typed.");
        c.gridx = 2; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        inputPanel.add(searchBtn, c);

        // Row 1: label | directory field (spans cols 1-2)
        c.gridx = 0; c.gridy = 1; c.weightx = 0; c.fill = GridBagConstraints.HORIZONTAL; c.gridwidth = 1;
        inputPanel.add(new JLabel("Directory (optional):"), c);
        directoryField = new JTextField("", 22);
        directoryField.getAccessibleContext().setAccessibleName(
            "Search directory. Leave blank to search your home folder. Type tilde slash for home.");
        c.gridx = 1; c.weightx = 1; c.gridwidth = 2;
        inputPanel.add(directoryField, c);

        // Row 2: hint (spans cols 1-2)
        c.gridx = 1; c.gridy = 2; c.gridwidth = 2; c.fill = GridBagConstraints.NONE; c.anchor = GridBagConstraints.WEST;
        JLabel hint = new JLabel("Leave directory blank to search ~/   ·   Press Enter or Search");
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 10f));
        hint.setForeground(Color.GRAY);
        inputPanel.add(hint, c);

        panel.add(inputPanel, BorderLayout.NORTH);

        // ── Results list ────────────────────────────────────────────────────
        listModel  = new DefaultListModel<>();
        resultList = new JList<>(listModel);
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultList.setCellRenderer(new FileCellRenderer());
        resultList.getAccessibleContext().setAccessibleName(
            "Search results. Use up and down arrow keys, then press Enter to open.");

        JScrollPane scroll = new JScrollPane(resultList);
        scroll.setBorder(new TitledBorder("Results  (up to " + MAX_RESULTS + ")"));
        panel.add(scroll, BorderLayout.CENTER);

        // ── Status + buttons ────────────────────────────────────────────────
        statusLabel = new JLabel(" ");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 10f));
        statusLabel.setForeground(Color.GRAY);

        JButton openBtn   = new JButton("Open");
        JButton cancelBtn = new JButton("Cancel");
        JPanel south = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.add(openBtn);
        buttons.add(cancelBtn);
        south.add(statusLabel, BorderLayout.WEST);
        south.add(buttons, BorderLayout.EAST);
        panel.add(south, BorderLayout.SOUTH);

        // ── Wiring ──────────────────────────────────────────────────────────
        ActionListener doSearch = e -> performSearch();
        filenameField.addActionListener(doSearch);
        directoryField.addActionListener(doSearch);
        searchBtn.addActionListener(doSearch);

        openBtn.addActionListener(e -> openSelected());
        cancelBtn.addActionListener(e -> dispose());

        resultList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) openSelected();
            }
        });
        resultList.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "open");
        resultList.getActionMap().put("open", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { openSelected(); }
        });

        // Tab from directory field focuses list when results are present
        directoryField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB && !listModel.isEmpty()) {
                    e.consume();
                    resultList.requestFocus();
                    if (resultList.getSelectedIndex() < 0) resultList.setSelectedIndex(0);
                }
            }
        });

        // Cmd/Ctrl+[ from results list → focus back to filename field
        int menu = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, menu), "backToSearch");
        getRootPane().getActionMap().put("backToSearch", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { filenameField.requestFocus(); }
        });

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
        getRootPane().getActionMap().put("cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { dispose(); }
        });
        getRootPane().setDefaultButton(openBtn);
        setContentPane(panel);
    }

    private void performSearch() {
        String query = filenameField.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please type a file name to search for.", "Missing name", JOptionPane.WARNING_MESSAGE);
            filenameField.requestFocus();
            return;
        }

        String dirStr = directoryField.getText().trim();
        if (dirStr.isEmpty()) {
            dirStr = System.getProperty("user.home");
        } else if (dirStr.startsWith("~")) {
            dirStr = System.getProperty("user.home") + dirStr.substring(1);
        }

        File root = new File(dirStr);
        if (!root.exists() || !root.isDirectory()) {
            JOptionPane.showMessageDialog(this,
                "Directory not found: " + dirStr, "Directory error", JOptionPane.WARNING_MESSAGE);
            directoryField.requestFocus();
            return;
        }

        statusLabel.setText("Searching...");
        List<File> found = new ArrayList<>();
        searchRecursive(root, query.toLowerCase(), found);

        listModel.clear();
        if (found.isEmpty()) {
            statusLabel.setText("No results for \"" + query + "\"");
            JOptionPane.showMessageDialog(this,
                "No Haskell files found matching \"" + query + "\".",
                "No results", JOptionPane.INFORMATION_MESSAGE);
            filenameField.requestFocus();
        } else {
            for (File f : found) listModel.addElement(f);
            boolean truncated = found.size() >= MAX_RESULTS;
            statusLabel.setText(found.size() + (truncated ? "+" : "") + " result(s)");
            resultList.requestFocus();
            resultList.setSelectedIndex(0);
        }
    }

    private void searchRecursive(File dir, String query, List<File> results) {
        searchRecursive(dir, query, results, 0);
    }

    private void searchRecursive(File dir, String query, List<File> results, int depth) {
        if (depth > MAX_DEPTH || results.size() >= MAX_RESULTS) return;
        File[] entries = dir.listFiles();
        if (entries == null) return;
        Arrays.sort(entries);
        for (File f : entries) {
            if (results.size() >= MAX_RESULTS) return;
            if (f.isFile()) {
                String n = f.getName().toLowerCase();
                if ((n.endsWith(".hs") || n.endsWith(".lhs")) && n.contains(query)) {
                    results.add(f);
                }
            } else if (f.isDirectory() && !f.isHidden()) {
                searchRecursive(f, query, results, depth + 1);
            }
        }
    }

    private void openSelected() {
        // If the list is empty but the user has typed something, search first
        if (listModel.isEmpty() && !filenameField.getText().trim().isEmpty()) {
            performSearch();
            // After search: auto-open if exactly one result, otherwise let user pick
            if (listModel.getSize() == 1) {
                result = listModel.getElementAt(0);
                dispose();
            } else if (listModel.getSize() > 1) {
                resultList.requestFocus();
                resultList.setSelectedIndex(0);
            }
            return;
        }
        File selected = resultList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                "Please type a file name above and press Enter to search first.",
                "No results yet", JOptionPane.INFORMATION_MESSAGE);
            filenameField.requestFocus();
            return;
        }
        result = selected;
        dispose();
    }

    public File getResult() { return result; }

    public static File show(Frame owner) {
        KeyboardOpenFileDialog dlg = new KeyboardOpenFileDialog(owner);
        dlg.setVisible(true);
        return dlg.getResult();
    }

    private static class FileCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof File) {
                File f = (File) value;
                String home = System.getProperty("user.home");
                String path = f.getAbsolutePath();
                if (path.startsWith(home)) path = "~" + path.substring(home.length());
                setText(path);
                setToolTipText(f.getAbsolutePath());
            }
            return this;
        }
    }
}
