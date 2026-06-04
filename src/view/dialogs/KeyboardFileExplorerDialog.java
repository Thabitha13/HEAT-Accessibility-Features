package view.dialogs;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;

/**
 * Keyboard-driven "Navigate File Explorer" dialog (Idea_02).
 *
 * Flow:
 *   1. Search panel   — type filename, Enter/Search, Up/Down to pick a result, Enter to continue
 *   2. Action panel   — "Open File" or "Move / Copy File"
 *   3. Move source    — shows current location (read-only)
 *   4. Move target    — type destination directory; created automatically if absent
 *
 * Cmd/Ctrl+[ always returns to the previous panel.
 * Esc closes from any panel.
 */
public class KeyboardFileExplorerDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final int MAX_RESULTS = 50;
    private static final int MAX_DEPTH   = 8;

    private static final String SEARCH   = "SEARCH";
    private static final String ACTION   = "ACTION";
    private static final String MOVE_SRC = "MOVE_SRC";
    private static final String MOVE_TGT = "MOVE_TGT";

    private CardLayout cards;
    private JPanel deck;

    // Search panel
    private JTextField searchField;
    private DefaultListModel<File> listModel;
    private JList<File> resultList;
    private JLabel searchStatusLabel;

    // Action panel
    private JLabel actionFileLabel;
    private JButton actionOpenBtn;

    // Move source panel
    private JLabel moveSourceLabel;

    // Move target panel
    private JTextField targetDirField;
    private JLabel moveTargetStatusLabel;

    // State
    private File selectedFile;
    private final Deque<String> panelHistory = new ArrayDeque<>();

    public KeyboardFileExplorerDialog(Frame owner) {
        super(owner, "Navigate File Explorer", true);
        buildUI();
        setSize(600, 460);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        getRootPane().getAccessibleContext().setAccessibleName(
            "Navigate File Explorer. Type a file name and press Enter to search. " +
            "Use arrow keys to navigate results. Press Cmd or Ctrl left bracket to go back.");
    }

    // ── UI construction ───────────────────────────────────────────────────────

    private void buildUI() {
        cards = new CardLayout();
        deck  = new JPanel(cards);
        deck.add(buildSearchPanel(),    SEARCH);
        deck.add(buildActionPanel(),    ACTION);
        deck.add(buildMoveSourcePanel(), MOVE_SRC);
        deck.add(buildMoveTargetPanel(), MOVE_TGT);
        setContentPane(deck);

        int menu = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "esc");
        getRootPane().getActionMap().put("esc", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { dispose(); }
        });

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, menu), "back");
        getRootPane().getActionMap().put("back", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { goBack(); }
        });

        showPanel(SEARCH);
    }

    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 8, 14));

        // Input row: label | text field | Search button
        JPanel inputRow = new JPanel(new BorderLayout(6, 0));
        inputRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        JLabel lbl = new JLabel("File name:");
        lbl.setPreferredSize(new Dimension(82, 24));
        searchField = new JTextField();
        searchField.getAccessibleContext().setAccessibleName(
            "File name. Type part of the Haskell file name, then press Enter or Search.");
        JButton searchBtn = new JButton("Search");
        inputRow.add(lbl, BorderLayout.WEST);
        inputRow.add(searchField, BorderLayout.CENTER);
        inputRow.add(searchBtn, BorderLayout.EAST);
        panel.add(inputRow, BorderLayout.NORTH);

        // Results list
        listModel  = new DefaultListModel<>();
        resultList = new JList<>(listModel);
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultList.setCellRenderer(new FileCellRenderer());
        resultList.getAccessibleContext().setAccessibleName(
            "Search results. Up and down arrow to select. Enter to continue.");
        JScrollPane scroll = new JScrollPane(resultList);
        scroll.setBorder(new TitledBorder("Results  (up to " + MAX_RESULTS + ")"));
        panel.add(scroll, BorderLayout.CENTER);

        // Status bar
        searchStatusLabel = new JLabel(" ");
        searchStatusLabel.setFont(searchStatusLabel.getFont().deriveFont(Font.PLAIN, 10f));
        searchStatusLabel.setForeground(Color.GRAY);
        JLabel hint = new JLabel("Esc closes  ·  Cmd/Ctrl+[ goes back");
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 10f));
        hint.setForeground(Color.GRAY);
        JPanel south = new JPanel(new BorderLayout());
        south.add(searchStatusLabel, BorderLayout.WEST);
        south.add(hint, BorderLayout.EAST);
        panel.add(south, BorderLayout.SOUTH);

        // Wiring
        searchField.addActionListener(e -> performSearch());
        searchBtn.addActionListener(e -> performSearch());

        // Down arrow from text field → move focus to list
        searchField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN && !listModel.isEmpty()) {
                    resultList.requestFocus();
                    if (resultList.getSelectedIndex() < 0) resultList.setSelectedIndex(0);
                }
            }
        });

        // Enter on list item → advance to action panel
        resultList.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "select");
        resultList.getActionMap().put("select", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { advanceToAction(); }
        });
        resultList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) advanceToAction();
            }
        });

        // Up arrow from top of list → return focus to search field
        resultList.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP && resultList.getSelectedIndex() == 0) {
                    searchField.requestFocus();
                }
            }
        });

        return panel;
    }

    private JPanel buildActionPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));

        actionFileLabel = new JLabel(" ", JLabel.CENTER);
        actionFileLabel.setFont(actionFileLabel.getFont().deriveFont(Font.PLAIN, 12f));
        actionFileLabel.setBorder(new TitledBorder("Selected file"));
        panel.add(actionFileLabel, BorderLayout.NORTH);

        actionOpenBtn = new JButton("Open File");
        actionOpenBtn.setPreferredSize(new Dimension(150, 52));
        actionOpenBtn.getAccessibleContext().setAccessibleName(
            "Open File. Opens the selected file in the code editor.");

        JButton moveBtn = new JButton("Move / Copy File");
        moveBtn.setPreferredSize(new Dimension(170, 52));
        moveBtn.getAccessibleContext().setAccessibleName(
            "Move or copy file. Choose a destination directory.");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 8));
        buttons.add(actionOpenBtn);
        buttons.add(moveBtn);
        panel.add(buttons, BorderLayout.CENTER);

        JLabel hint = new JLabel("Cmd/Ctrl+[  ←  back to results  ·  Esc  closes", JLabel.CENTER);
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 10f));
        hint.setForeground(Color.GRAY);
        panel.add(hint, BorderLayout.SOUTH);

        actionOpenBtn.addActionListener(e -> openSelectedFile());
        moveBtn.addActionListener(e -> {
            updateMoveSourceDisplay();
            showPanel(MOVE_SRC);
        });

        return panel;
    }

    private JPanel buildMoveSourcePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        moveSourceLabel = new JLabel(" ", JLabel.CENTER);
        moveSourceLabel.setBorder(new TitledBorder("Current location"));
        moveSourceLabel.setFont(moveSourceLabel.getFont().deriveFont(Font.PLAIN, 12f));
        panel.add(moveSourceLabel, BorderLayout.CENTER);

        JButton nextBtn = new JButton("Choose destination  →");
        nextBtn.addActionListener(e -> showPanel(MOVE_TGT));

        JLabel hint = new JLabel("Cmd/Ctrl+[  ←  back  ·  Esc  closes");
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 10f));
        hint.setForeground(Color.GRAY);

        JPanel south = new JPanel(new BorderLayout());
        south.add(hint, BorderLayout.WEST);
        south.add(nextBtn, BorderLayout.EAST);
        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildMoveTargetPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 4, 6, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        inputPanel.add(new JLabel("Destination directory:"), c);
        targetDirField = new JTextField(System.getProperty("user.home"), 28);
        targetDirField.getAccessibleContext().setAccessibleName(
            "Destination directory. Type the path. Created automatically if it does not exist.");
        c.gridx = 1; c.weightx = 1;
        inputPanel.add(targetDirField, c);

        c.gridx = 1; c.gridy = 1;
        JLabel dirHint = new JLabel("Tip: ~/Documents/MyProject  —  directory is created if missing");
        dirHint.setFont(dirHint.getFont().deriveFont(Font.PLAIN, 10f));
        dirHint.setForeground(Color.GRAY);
        inputPanel.add(dirHint, c);

        panel.add(inputPanel, BorderLayout.CENTER);

        moveTargetStatusLabel = new JLabel(" ");
        moveTargetStatusLabel.setFont(moveTargetStatusLabel.getFont().deriveFont(Font.PLAIN, 10f));

        JButton moveBtn = new JButton("Move File");
        JButton copyBtn = new JButton("Copy File");
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.add(moveBtn);
        btns.add(copyBtn);

        JLabel hint = new JLabel("Cmd/Ctrl+[  ←  back  ·  Esc  closes");
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 10f));
        hint.setForeground(Color.GRAY);

        JPanel south = new JPanel(new BorderLayout(4, 4));
        south.add(moveTargetStatusLabel, BorderLayout.NORTH);
        JPanel southBottom = new JPanel(new BorderLayout());
        southBottom.add(hint, BorderLayout.WEST);
        southBottom.add(btns, BorderLayout.EAST);
        south.add(southBottom, BorderLayout.SOUTH);
        panel.add(south, BorderLayout.SOUTH);

        moveBtn.addActionListener(e -> performMove(false));
        copyBtn.addActionListener(e -> performMove(true));
        targetDirField.addActionListener(e -> performMove(false));

        return panel;
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    private void showPanel(String name) {
        if (panelHistory.isEmpty() || !panelHistory.peek().equals(name)) {
            panelHistory.push(name);
        }
        cards.show(deck, name);
        focusDefault(name);
    }

    private void goBack() {
        if (panelHistory.size() <= 1) {
            dispose();
            return;
        }
        panelHistory.pop();
        String prev = panelHistory.peek();
        cards.show(deck, prev);
        focusDefault(prev);
    }

    private void focusDefault(String panel) {
        SwingUtilities.invokeLater(() -> {
            switch (panel) {
                case SEARCH:   searchField.requestFocus();    break;
                case ACTION:   actionOpenBtn.requestFocus();  break;
                case MOVE_SRC: moveSourceLabel.requestFocus(); break;
                case MOVE_TGT: targetDirField.requestFocus(); break;
            }
        });
    }

    // ── Logic ─────────────────────────────────────────────────────────────────

    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please type a file name to search for.", "Missing name", JOptionPane.WARNING_MESSAGE);
            searchField.requestFocus();
            return;
        }
        searchStatusLabel.setText("Searching…");
        listModel.clear();

        List<File> found = new ArrayList<>();
        searchRecursive(new File(System.getProperty("user.home")), query.toLowerCase(), found, 0);

        if (found.isEmpty()) {
            // Suggest fuzzy matches: drop last char until we find something
            List<File> fuzzy = new ArrayList<>();
            String shortened = query;
            while (shortened.length() > 1 && fuzzy.isEmpty()) {
                shortened = shortened.substring(0, shortened.length() - 1);
                searchRecursive(new File(System.getProperty("user.home")),
                    shortened.toLowerCase(), fuzzy, 0);
            }
            String msg = "No Haskell files found matching \"" + query + "\".";
            if (!fuzzy.isEmpty()) {
                msg += "\n\nDid you mean:";
                for (int i = 0; i < Math.min(3, fuzzy.size()); i++) {
                    msg += "\n  " + fuzzy.get(i).getName();
                }
            }
            searchStatusLabel.setText("No results for \"" + query + "\"");
            JOptionPane.showMessageDialog(this, msg, "File not found", JOptionPane.INFORMATION_MESSAGE);
            searchField.requestFocus();
        } else {
            for (File f : found) listModel.addElement(f);
            boolean truncated = found.size() >= MAX_RESULTS;
            searchStatusLabel.setText(found.size() + (truncated ? "+" : "") +
                " result(s)  ·  ↑↓ to select, Enter to continue");
            resultList.requestFocus();
            resultList.setSelectedIndex(0);
        }
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

    private void advanceToAction() {
        File sel = resultList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a file from the results list first.",
                "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        selectedFile = sel;
        String home = System.getProperty("user.home");
        String path = sel.getAbsolutePath();
        if (path.startsWith(home)) path = "~" + path.substring(home.length());
        actionFileLabel.setText("<html><center>" + escapeHtml(path) + "</center></html>");
        showPanel(ACTION);
    }

    private void openSelectedFile() {
        if (selectedFile == null) return;
        managers.WindowManager.getInstance().openFile(selectedFile);
        dispose();
    }

    private void updateMoveSourceDisplay() {
        if (selectedFile == null) return;
        String home = System.getProperty("user.home");
        String parent = selectedFile.getParent();
        if (parent == null) parent = "";
        if (parent.startsWith(home)) parent = "~" + parent.substring(home.length());
        moveSourceLabel.setText("<html><center>" + escapeHtml(parent) +
            "<br><b>" + escapeHtml(selectedFile.getName()) + "</b></center></html>");
    }

    private void performMove(boolean copyOnly) {
        if (selectedFile == null) return;
        moveTargetStatusLabel.setForeground(Color.RED);

        String dirStr = targetDirField.getText().trim();
        if (dirStr.startsWith("~")) {
            dirStr = System.getProperty("user.home") + dirStr.substring(1);
        }
        File targetDir = new File(dirStr);
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            moveTargetStatusLabel.setText("Could not create directory: " + dirStr);
            targetDirField.requestFocus();
            return;
        }
        if (!targetDir.isDirectory()) {
            moveTargetStatusLabel.setText("Not a directory: " + dirStr);
            targetDirField.requestFocus();
            return;
        }

        File dest = new File(targetDir, selectedFile.getName());
        if (dest.exists()) {
            int choice = JOptionPane.showConfirmDialog(this,
                "'" + dest.getName() + "' already exists at the destination. Overwrite?",
                "File exists", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) return;
        }

        try {
            Files.copy(selectedFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            if (!copyOnly) {
                selectedFile.delete();
            }
        } catch (IOException ex) {
            moveTargetStatusLabel.setText((copyOnly ? "Copy" : "Move") + " failed: " + ex.getMessage());
            return;
        }

        String verb = copyOnly ? "Copied" : "Moved";
        JOptionPane.showMessageDialog(this,
            verb + " to " + dest.getAbsolutePath(),
            verb + " successfully", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public static void show(Frame owner) {
        new KeyboardFileExplorerDialog(owner).setVisible(true);
    }

    // ── Renderer ─────────────────────────────────────────────────────────────

    private static class FileCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
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