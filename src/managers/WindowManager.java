package managers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import java.awt.Dimension;

import utils.HeatTheme;
import utils.Resources;
import view.toolbars.MainMenu;
import view.toolbars.Toolbar;
import view.windows.AboutWindow;
import view.windows.ConsoleWindow;
import view.windows.EditorWindow;
import view.windows.HelpWindow;
import view.windows.OptionsWindow;
import view.windows.PrintWindow;
import view.windows.SearchDialog;
import view.windows.SplashWindow;
import view.windows.TreeWindow;
import view.windows.WizardWindow;

/**
 * The manager class responsible for all GUI components
 */
public class WindowManager {
  private static Logger log = Logger.getLogger("heat");
  private static WindowManager instance = null;

  private JFrame mainScreenFrame;
  private JSplitPane jSplitMain;
  private JSplitPane jSplitTree;

  // Goutham: splash screen
  private SplashWindow splashWindow;

  // Thabitha: font-size/accessibility panel
  private view.panels.AccessibilityPanel accessibilityPanel;

  private EditorWindow displayWindow;
  private OptionsWindow optionsWindow;
  private AboutWindow aboutWindow;
  private WizardWindow wizardWindow;
  private HelpWindow helpWindow;
  private ConsoleWindow consoleWindow;
  private PrintWindow printwindow;
  private SearchDialog searchWindow;
  private TreeWindow treeWindow;
  private MainMenu mainMenu;
  private Toolbar toolbar;

  private final int COMPILEDERROR  = 0;
  private final int COMPILEDCORRECT = 1;
  private final int UNCOMPILED     = 2;
  private final int EVALUATING     = 3;
  private final int NOPROGRAM      = 4;
  private int savedStatus  = -1;
  private int currentStatus = -1;
  private boolean compileEnabled   = false;
  private boolean testEnabled      = false;
  private boolean interruptEnabled = false;

  public boolean isCompiledCorrect() { return currentStatus == COMPILEDCORRECT; }
  public boolean isEvaluating()      { return currentStatus == EVALUATING; }
  public void safeStatus()           { savedStatus = currentStatus; }
  public void restoreStatus()        { setStatus(savedStatus); }

  private void setStatus(int status) {
    currentStatus = status;
    switch (currentStatus) {
      case COMPILEDERROR:
        log.info("[WindowManager]: setStatusCompiledError");
        toolbar.setCompileStatus(0);
        accessibility.TTSManager.getInstance().speak(
            "Compilation failed. Check the error in the console.");
        consoleWindow.setEnabled(false);
        displayWindow.setEnabled(true);
        setCompileEnabled(false); setTestEnabled(false); setInterruptEnabled(false);
        break;
      case COMPILEDCORRECT:
        log.info("[WindowManager]: setStatusCompiledCorrect");
        toolbar.setCompileStatus(1);
        if (savedStatus != COMPILEDCORRECT)
          accessibility.TTSManager.getInstance().speak("Compilation successful. Ready to evaluate.");
        consoleWindow.setEnabled(true);
        displayWindow.setEnabled(true);
        setCompileEnabled(false);
        setTestEnabled(ParserManager.getParser().hasUncheckedTests());
        setInterruptEnabled(false);
        dismissSplash();
        break;
      case UNCOMPILED:
        log.info("[WindowManager]: setStatusUncompiled");
        toolbar.setCompileStatus(2);
        accessibility.TTSManager.getInstance().speak("Program not compiled. Press F5 to compile.");
        consoleWindow.setEnabled(false);
        displayWindow.setEnabled(true);
        setCompileEnabled(true); setTestEnabled(false); setInterruptEnabled(false);
        break;
      case EVALUATING:
        log.info("[WindowManager]: setStatusEvaluating");
        toolbar.setCompileStatus(3);
        accessibility.TTSManager.getInstance().speak("Evaluating.");
        consoleWindow.setEnabled(true);
        displayWindow.setEnabled(false);
        setCompileEnabled(false); setTestEnabled(false); setInterruptEnabled(true);
        break;
      case NOPROGRAM:
        log.info("[WindowManager]: setStatusNoProgram");
        toolbar.setCompileStatus(1);
        accessibility.TTSManager.getInstance().speak(
            "No program loaded. Open a Haskell file to begin.");
        consoleWindow.setEnabled(true);
        displayWindow.setEnabled(false);
        setCompileEnabled(false); setTestEnabled(false); setInterruptEnabled(false);
        break;
      default:
        log.warning("[WindowManager]: set to unknown status");
    }
  }

  public void setStatusNotCompiled() {
    if (FileManager.getInstance().getCurrentFile() != null) setStatusUncompiled();
    else setStatusNoProgram();
  }
  public void setStatusUncompiled() { if (currentStatus != UNCOMPILED) setStatus(UNCOMPILED); }
  public void setStatusCompiledError()   { setStatus(COMPILEDERROR); }
  public void setStatusCorrect() {
    if (FileManager.getInstance().getCurrentFile() != null) setStatusCompiledCorrect();
    else setStatusNoProgram();
  }
  public void setStatusNoProgram()      { setStatus(NOPROGRAM); }
  public void setStatusCompiledCorrect(){ setStatus(COMPILEDCORRECT); }
  public void setStatusEvaluating()     { safeStatus(); setStatus(EVALUATING); }

  public void setTestEnabled(boolean on) {
    testEnabled = on; mainMenu.setTestEnabled(on); toolbar.setTestEnabled(on);
  }
  public boolean isTestEnabled()  { return testEnabled; }
  public void setCompileEnabled(boolean on) {
    compileEnabled = on; mainMenu.setCompileEnabled(on); toolbar.setCompileEnabled(on);
  }
  public boolean isCompileEnabled()  { return compileEnabled; }
  public void setInterruptEnabled(boolean on) {
    interruptEnabled = on; mainMenu.setInterruptEnabled(on); toolbar.setInterruptEnabled(on);
  }
  public boolean isInterruptEnabled() { return interruptEnabled; }

  protected WindowManager() { /* Exists to prevent instantiation */ }

  public static WindowManager getInstance() {
    if (instance == null) instance = new WindowManager();
    return instance;
  }

  public EditorWindow getEditorWindow()   { return displayWindow; }
  public ConsoleWindow getConsoleWindow() { return consoleWindow; }
  public OptionsWindow getOptionsWindow() { return optionsWindow; }
  public WizardWindow getWizardWindow()   { return wizardWindow; }
  public HelpWindow getHelpWindow()       { return helpWindow; }
  public AboutWindow getAboutWindow()     { return aboutWindow; }
  public PrintWindow getPrintWindow()     { return printwindow; }
  public MainMenu getMainMenu()           { return mainMenu; }
  public SearchDialog getSearchWindow()   { return searchWindow; }
  public TreeWindow getTreeWindow()       { return treeWindow; }

  /** Thabitha: returns the accessibility font-size panel */
  public view.panels.AccessibilityPanel getAccessibilityPanel() { return accessibilityPanel; }

  public void createGUI() {
    if (mainScreenFrame != null) mainScreenFrame.setVisible(false);
    mainScreenFrame = new JFrame();
    mainScreenFrame.setTitle("HEAT - Haskell Educational Advancement Tool");
    Image icon = Resources.getIcon("logo").getImage();
    mainScreenFrame.setIconImage(icon);

    // Thabitha: create accessibility panel before building UI
    accessibilityPanel = new view.panels.AccessibilityPanel();

    displayWindow = new EditorWindow();
    consoleWindow = new ConsoleWindow();
    optionsWindow = new OptionsWindow();
    helpWindow    = new HelpWindow();
    aboutWindow   = new AboutWindow();
    wizardWindow  = new WizardWindow();
    searchWindow  = new SearchDialog();
    printwindow   = new PrintWindow();
    treeWindow    = new TreeWindow();
    mainMenu      = new MainMenu();
    toolbar       = new Toolbar();

    jSplitMain = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        displayWindow.getJTextPane(), consoleWindow.getWindowPanel());
    jSplitTree = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        treeWindow.getWindowPanel(), jSplitMain);

    displayWindow.getJTextPane().setMinimumSize(new Dimension(200, 0));
    jSplitMain.setResizeWeight(0.6);
    jSplitMain.setOneTouchExpandable(true);
    jSplitTree.setOneTouchExpandable(true);

    try {
      mainScreenFrame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          JButton jb = new JButton();
          jb.setAction(ActionManager.getInstance().getExitProgramAction());
          jb.doClick();
        }
      });
      mainScreenFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

      mainScreenFrame.setJMenuBar(mainMenu.getToolBar());

      // Thabitha: stack toolbar + accessibility panel at the top
      javax.swing.JPanel topPanel = new javax.swing.JPanel();
      topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.Y_AXIS));
      topPanel.add(toolbar.getToolBar());
      topPanel.add(accessibilityPanel);
      mainScreenFrame.getContentPane().add(topPanel, BorderLayout.NORTH);

      mainScreenFrame.getContentPane().add(jSplitTree, BorderLayout.CENTER);

      mainScreenFrame.setMinimumSize(new Dimension(550, 300));
      mainScreenFrame.setSize(620, 400);
      mainScreenFrame.pack();
      mainScreenFrame.setLocationRelativeTo(null);

      // YOUR: F10 moves focus to toolbar
      mainScreenFrame.getRootPane().getInputMap(
          javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(
          javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F10, 0), "focusToolbar");
      mainScreenFrame.getRootPane().getActionMap().put("focusToolbar",
          new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              toolbar.getToolBar().getComponent(0).requestFocusInWindow();
              accessibility.TTSManager.getInstance().speak(
                  "Toolbar focused. Press Tab to move between buttons.");
            }
          });

      setStatusNotCompiled();

      // Goutham: high-contrast theme
      applyTheme();

      // Goutham: accessible names for screen readers (null-guarded)
      if (jSplitMain.getAccessibleContext() != null)
        jSplitMain.getAccessibleContext().setAccessibleName("Editor and console split panel");
      if (jSplitTree.getAccessibleContext() != null)
        jSplitTree.getAccessibleContext().setAccessibleName("Overview tree and main content split panel");
      if (consoleWindow.getTextArea() != null &&
          consoleWindow.getTextArea().getAccessibleContext() != null)
        consoleWindow.getTextArea().getAccessibleContext()
            .setAccessibleName("GHCi interpreter console output");
      if (displayWindow.getJTextPane() != null &&
          displayWindow.getJTextPane().getAccessibleContext() != null)
        displayWindow.getJTextPane().getAccessibleContext()
            .setAccessibleName("Haskell source code editor");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setVisible() { mainScreenFrame.setVisible(true); }

  public void onlyConsole() {
    hideTree();
    jSplitMain.setDividerLocation(jSplitMain.getInsets().top);
  }
  public void showAll()  { showTree(); showOutput(); }
  public void hideTree() { jSplitTree.setDividerLocation(jSplitTree.getInsets().left); }
  public void showTree() {
    jSplitTree.setDividerLocation(jSplitTree.getInsets().left +
        jSplitTree.getLeftComponent().getPreferredSize().width);
  }
  public void toggleTree() {
    if (jSplitTree.getDividerLocation() > jSplitTree.getMinimumDividerLocation()) hideTree();
    else showTree();
  }
  public void hideOutput() {
    log.info("hideOutput");
    jSplitMain.setDividerLocation(jSplitMain.getSize().height -
        jSplitMain.getInsets().bottom - jSplitMain.getDividerSize());
  }
  public void showOutput() { log.info("showOutput"); jSplitMain.setDividerLocation(0.6); }
  public void toggleConsole() {
    int max = jSplitMain.getSize().height - jSplitMain.getInsets().bottom -
        jSplitMain.getDividerSize();
    if (jSplitMain.getDividerLocation() < max) hideOutput(); else showOutput();
  }

  public void repaintAll() { mainScreenFrame.repaint(); }

  // Goutham: high-contrast colour theme
  public void applyTheme() {
    boolean hc   = SettingsManager.getInstance().isHighContrastEnabled();
    Color bg     = hc ? HeatTheme.HIGH_CONTRAST_BACKGROUND : null;
    Color fg     = hc ? HeatTheme.HIGH_CONTRAST_FOREGROUND  : null;
    Color edit   = hc ? HeatTheme.HIGH_CONTRAST_EDITOR_BG   : null;
    if (bg != null) {
      mainScreenFrame.getContentPane().setBackground(bg);
      consoleWindow.getTextArea().setBackground(bg);
      consoleWindow.getTextArea().setForeground(fg);
      consoleWindow.getTextArea().setCaretColor(fg);
      displayWindow.getJTextPane().getPainter().setBackground(edit);
      displayWindow.getJTextPane().getPainter().setForeground(fg);
      displayWindow.getJTextPane().getPainter().setCaretColor(fg);
    } else {
      consoleWindow.getTextArea().setBackground(Color.WHITE);
      consoleWindow.getTextArea().setForeground(Color.BLACK);
      displayWindow.getJTextPane().getPainter().setBackground(Color.WHITE);
      displayWindow.getJTextPane().getPainter().setForeground(Color.BLACK);
    }
    repaintAll();
  }

  // Goutham: splash window
  public void setSplashWindow(SplashWindow sw) { splashWindow = sw; }
  public void dismissSplash() {
    if (splashWindow != null) { splashWindow.dismiss(); splashWindow = null; }
  }
  public void notifyInterpreterReady() {
    if (splashWindow != null)
      splashWindow.setStatus("GHCi ready — press N to create, O to open, Esc to skip");
  }

  // Goutham: keyboard-accessible file explorer
  public void showFileExplorer() {
    view.dialogs.KeyboardFileExplorerDialog.show(mainScreenFrame);
  }

  public JFrame getMainScreenFrame() { return mainScreenFrame; }

  public void showOptionsWindow()  { getOptionsWindow().show(); }
  public void showSearchWindow()   { getSearchWindow().show(); }
  public void showWizardWindow()   { getWizardWindow().show(); }
  public void showHelpWindow()     { getHelpWindow().show(); }
  public void showAboutWindow()    { getAboutWindow().show(); }
  public void showPrintWindow()    { getPrintWindow().show(); }
  public void setDefaultFontSize(int ptSize) { getConsoleWindow().setFontSize(ptSize); }

  public void setCloseEnabled(boolean enabled) {
    mainMenu.setCloseEnabled(enabled); toolbar.setCloseEnabled(enabled);
  }

  public void copySelected() {
    String d = displayWindow.getSelectedText();
    String o = consoleWindow.getSelectedText();
    if (d != null && !d.equals("")) displayWindow.copy();
    else if (o != null && !o.equals("")) consoleWindow.copy();
  }

  public void setLNF(String lnfString) {
    try {
      UIManager.setLookAndFeel(lnfString);
      SwingUtilities.updateComponentTreeUI(getMainScreenFrame());
      optionsWindow = new OptionsWindow();
      helpWindow    = new HelpWindow();
      aboutWindow   = new AboutWindow();
      wizardWindow  = new WizardWindow();
      printwindow   = new PrintWindow();
      searchWindow  = new SearchDialog();
    } catch (Exception ex) {
      log.severe("[OptionsWindow] Error setting lnf:" + lnfString);
    }
  }

  public void setTitleFileName(String fileName) {
    if (fileName == null || fileName.trim().equals(""))
      getMainScreenFrame().setTitle("HEAT - Haskell Educational Advancement Tool");
    else
      getMainScreenFrame().setTitle("HEAT - " + fileName);
  }

  public void openFile(java.io.File file) {
    FileManager fm = FileManager.getInstance();
    if (!fm.openFile(file)) {
      JOptionPane.showMessageDialog(mainScreenFrame,
          "Error creating new file " + file.getAbsolutePath(),
          "File Creation Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    String contents = fm.readFile();
    getEditorWindow().clearLineMark();
    getEditorWindow().setEditorContent(contents);
    getEditorWindow().setEnabled(true);
    setCloseEnabled(true);
    setTitleFileName(fm.getFilePath());
    setStatusUncompiled();
    ParserManager.getInstance().refresh();
    getTreeWindow().refreshTree();
    showAll();
    UndoManager.getInstance().reset();
  }

  public static void setLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      Logger.getLogger("heat").warning("[WindowManager] Unable to set look and feel");
    }
  }

} // end WindowManager
