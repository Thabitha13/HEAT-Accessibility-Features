package managers;

import java.util.logging.Logger;

import utils.HaskellFilter;
import utils.Resources;
import utils.Settings;
import utils.InterpreterParser;

import view.dialogs.KeyboardNewFileDialog;
import view.dialogs.KeyboardOpenFileDialog;
import view.windows.*;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * The manager Class responsible for all GUI action commands
 */
public class ActionManager {
  private static Logger log = Logger.getLogger("heat");
  private static ActionManager instance = null;
  private File selectedFile = null;

  // file/program actions
  private ExitProgramAction exitProgramAction = new ExitProgramAction("Quit",
      Resources.getIcon("exit16"), "Quit HEAT", new Integer(KeyEvent.VK_Q),
      KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private ExitProgramAction toolbarExitProgramAction = new ExitProgramAction(null,
      Resources.getIcon("exit22"), "Quit HEAT", new Integer(KeyEvent.VK_Q),
      KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));

  // Goutham: New file action using keyboard dialog
  private NewFileAction newFileAction = new NewFileAction("New..",
      Resources.getIcon("windownew22"), "Create a new Haskell file",
      new Integer(KeyEvent.VK_N),
      KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));

  // Goutham: File explorer action
  private FileExplorerAction fileExplorerAction = new FileExplorerAction("Navigate Files..",
      Resources.getIcon("filefind16"), "Search and navigate Haskell files",
      new Integer(KeyEvent.VK_F),
      KeyStroke.getKeyStroke(KeyEvent.VK_F,
          Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | InputEvent.SHIFT_DOWN_MASK, false));

  private OpenFileAction openFileAction = new OpenFileAction("Open..",
      Resources.getIcon("fileopen16"), "Open an existing or new file in the editor",
      new Integer(KeyEvent.VK_O),
      KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private OpenFileAction toolbarOpenFileAction = new OpenFileAction(null,
      Resources.getIcon("fileopen22"), "Open an existing or new file in the editor",
      new Integer(KeyEvent.VK_O),
      KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private CloseFileAction closeFileAction = new CloseFileAction("Close",
      Resources.getIcon("fileclose16"), "Save file and close editor", null, null);
  private CloseFileAction toolbarCloseFileAction = new CloseFileAction(null,
      Resources.getIcon("fileclose22"), "Save file and close editor", null, null);
  private PrintFileAction printFileAction = new PrintFileAction("Print",
      Resources.getIcon("fileprint16"), "Print editor content or interpreter console",
      new Integer(KeyEvent.VK_P),
      KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private ShowOptionsAction showOptionsAction = new ShowOptionsAction("Options",
      Resources.getIcon("list16"), "Change HEAT Options",
      new Integer(KeyEvent.VK_D),
      KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));

  // editing actions
  private UndoAction undoAction = new UndoAction("Undo", Resources.getIcon("undo16"),
      "Undo last change", new Integer(KeyEvent.VK_Z),
      KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private UndoAction toolbarUndoAction = new UndoAction(null, Resources.getIcon("undo22"),
      "Undo last change", new Integer(KeyEvent.VK_Z),
      KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private RedoAction redoAction = new RedoAction("Redo", Resources.getIcon("redo16"),
      "Redo last change", new Integer(KeyEvent.VK_Y),
      KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private RedoAction toolbarRedoAction = new RedoAction(null, Resources.getIcon("redo22"),
      "Redo last change", new Integer(KeyEvent.VK_Y),
      KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private ShowSearchAction showSearchAction = new ShowSearchAction("Find",
      Resources.getIcon("filefind16"), "Find text in the program",
      new Integer(KeyEvent.VK_F),
      KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private ShowSearchAction toolbarSearchAction = new ShowSearchAction(null,
      Resources.getIcon("filefind22"), "Find text in the program",
      new Integer(KeyEvent.VK_F),
      KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));

  // YOUR: select all action
  private EditSelectAllAction editSelectAllAction = new EditSelectAllAction("Select All",
      Resources.getIcon("editcopy16"), "Select all text", new Integer(KeyEvent.VK_A),
      KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));

  private EditCutAction editCutAction = new EditCutAction("Cut",
      Resources.getIcon("editcut16"), "Cut selected text", new Integer(KeyEvent.VK_X),
      KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private EditCutAction toolbarEditCutAction = new EditCutAction(null,
      Resources.getIcon("editcut22"), "Cut selected text", new Integer(KeyEvent.VK_X),
      KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private EditCopyAction editCopyAction = new EditCopyAction("Copy",
      Resources.getIcon("editcopy16"), "Copy selected text", new Integer(KeyEvent.VK_C),
      KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private EditCopyAction toolbarEditCopyAction = new EditCopyAction(null,
      Resources.getIcon("editcopy22"), "Copy selected text", new Integer(KeyEvent.VK_C),
      KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private EditPasteAction editPasteAction = new EditPasteAction("Paste",
      Resources.getIcon("editpaste16"), "Paste selected text", new Integer(KeyEvent.VK_V),
      KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private EditPasteAction toolbarEditPasteAction = new EditPasteAction(null,
      Resources.getIcon("editpaste22"), "Paste selected text", new Integer(KeyEvent.VK_V),
      KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));

  // run actions
  private CompileAction compileAction = new CompileAction(null,
      Resources.getIcon("reload16"), "Load & compile program", new Integer(KeyEvent.VK_L),
      KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private CompileAction toolbarCompileAction = new CompileAction(null,
      Resources.getIcon("reload22"), "Load program into interpreter and compile it", new Integer(KeyEvent.VK_L),
      KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private InterruptAction interruptAction = new InterruptAction(null,
      Resources.getIcon("stop16"), "Interrupt interpreter", new Integer(KeyEvent.VK_I),
      KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private InterruptAction toolbarInterruptAction = new InterruptAction(null,
      Resources.getIcon("stop22"), "Interrupt interpreter", new Integer(KeyEvent.VK_I),
      KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private TestAction testAction = new TestAction(null,
      Resources.getIcon("debug16"), "Check properties", new Integer(KeyEvent.VK_T),
      KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private TestAction toolbarTestAction = new TestAction(null,
      Resources.getIcon("debug22"), "Check properties", new Integer(KeyEvent.VK_T),
      KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));

  // help actions
  private ShowHelpAction showHelpAction = new ShowHelpAction("Help",
      Resources.getIcon("help16"), "Display help", new Integer(KeyEvent.VK_L),
      KeyStroke.getKeyStroke(KeyEvent.VK_H, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private ShowAboutAction showAboutAction = new ShowAboutAction("About",
      Resources.getIcon("info16"), "Display about information", null, null);

  private RefreshTreeAction refreshTreeAction = new RefreshTreeAction("", Resources.getIcon("reload16"), "Refresh overview");
  private ExpandTreeAction expandTreeAction = new ExpandTreeAction("", Resources.getIcon("expandTreeWindow16"), "Expand all overview elements");
  private CollapseTreeAction collapseTreeAction = new CollapseTreeAction("", Resources.getIcon("collapseTreeWindow16"), "Collapse all overview elements");
  private ToggleTreeAction toggleTreeAction = new ToggleTreeAction(null, Resources.getIcon("tree_window_22"), "Show/hide overview");
  private ToggleConsoleAction toggleOutputAction = new ToggleConsoleAction(null, Resources.getIcon("output_window_22"), "Show/hide interpreter console");

  private SendEvaluationAction sendEvaluationAction = new SendEvaluationAction("Send",
      Resources.getIcon("effect16"), "Sends Evaluation to Interpreter",
      new Integer(KeyEvent.VK_E),
      KeyStroke.getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private GoToPastConsoleHistory goToPastConsoleHistory = new GoToPastConsoleHistory();
  private GoToRecentConsoleHistory goToRecentConsoleHistory = new GoToRecentConsoleHistory();

  // YOUR: keyboard guide action (F1)
  private KeyboardGuideAction keyboardGuideAction = new KeyboardGuideAction();

  // Thabitha: font size keyboard shortcuts (Cmd+= / Cmd+-)
  private IncreaseFontSizeAction increaseFontSizeAction = new IncreaseFontSizeAction();
  private DecreaseFontSizeAction decreaseFontSizeAction = new DecreaseFontSizeAction();

  // Thabitha: toggle colour-blind mode shortcut (Cmd+Shift+C)
  private ToggleColourBlindAction toggleColourBlindAction = new ToggleColourBlindAction();

  private SaveOptionsAction saveOptionsAction = new SaveOptionsAction("Apply",
      Resources.getIcon(""), "Apply options",
      new Integer(KeyEvent.VK_S),
      KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
  private SaveWizardAction saveWizardAction = new SaveWizardAction("Continue",
      Resources.getIcon(""), "Save path and continue", new Integer(KeyEvent.VK_S),
      KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));

  protected ActionManager() { /* Prevent instantiation */ }

  public File getSelectedFile() { return selectedFile; }

  public static ActionManager getInstance() {
    if (instance == null) instance = new ActionManager();
    return instance;
  }

  // Getters
  public ActionManager.NewFileAction getNewFileAction() { return newFileAction; }
  public void triggerNewFile() { newFileAction.actionPerformed(null); }
  public ActionManager.OpenFileAction getOpenFileAction() { return openFileAction; }
  public void triggerOpenFile() { openFileAction.actionPerformed(null); }
  public ActionManager.FileExplorerAction getFileExplorerAction() { return fileExplorerAction; }
  public ActionManager.CloseFileAction getCloseFileAction() { return closeFileAction; }
  public ActionManager.CloseFileAction getToolbarCloseFileAction() { return toolbarCloseFileAction; }
  public ActionManager.EditSelectAllAction getEditSelectAllAction() { return editSelectAllAction; }
  public ActionManager.EditCopyAction getEditCopyAction() { return editCopyAction; }
  public ActionManager.EditCopyAction getToolbarEditCopyAction() { return toolbarEditCopyAction; }
  public ActionManager.EditCutAction getEditCutAction() { return editCutAction; }
  public ActionManager.EditCutAction getToolbarEditCutAction() { return toolbarEditCutAction; }
  public ActionManager.EditPasteAction getEditPasteAction() { return editPasteAction; }
  public ActionManager.EditPasteAction getToolbarEditPasteAction() { return toolbarEditPasteAction; }
  public ActionManager.ExitProgramAction getExitProgramAction() { return exitProgramAction; }
  public ActionManager.PrintFileAction getPrintFileAction() { return printFileAction; }
  public ActionManager.CompileAction getCompileAction() { return compileAction; }
  public ActionManager.CompileAction getToolbarCompileAction() { return toolbarCompileAction; }
  public ActionManager.SaveOptionsAction getSaveOptionsAction() { return saveOptionsAction; }
  public ActionManager.SaveWizardAction getSaveWizardAction() { return saveWizardAction; }
  public ActionManager.SendEvaluationAction getSendEvaluationAction() { return sendEvaluationAction; }
  public ActionManager.ShowAboutAction getShowAboutAction() { return showAboutAction; }
  public ActionManager.ShowHelpAction getShowHelpAction() { return showHelpAction; }
  public ActionManager.ShowOptionsAction getShowOptionsAction() { return showOptionsAction; }
  public ActionManager.ExitProgramAction getToolbarExitProgramAction() { return toolbarExitProgramAction; }
  public ActionManager.OpenFileAction getToolbarOpenFileAction() { return toolbarOpenFileAction; }
  public ActionManager.UndoAction getUndoAction() { return undoAction; }
  public ActionManager.UndoAction getToolbarUndoAction() { return toolbarUndoAction; }
  public ActionManager.RedoAction getRedoAction() { return redoAction; }
  public ActionManager.RedoAction getToolbarRedoAction() { return toolbarRedoAction; }
  public ActionManager.ShowSearchAction getSearchAction() { return showSearchAction; }
  public ActionManager.ShowSearchAction getToolbarSearchAction() { return toolbarSearchAction; }
  public ActionManager.RefreshTreeAction getRefreshTreeAction() { return refreshTreeAction; }
  public ActionManager.ExpandTreeAction getExpandTreeAction() { return expandTreeAction; }
  public ActionManager.CollapseTreeAction getCollapseTreeAction() { return collapseTreeAction; }
  public ActionManager.ToggleTreeAction getToggleTreeAction() { return toggleTreeAction; }
  public ActionManager.ToggleConsoleAction getToggleOutputAction() { return toggleOutputAction; }
  public ActionManager.TestAction getTestAction() { return testAction; }
  public ActionManager.TestAction getToolbarTestAction() { return toolbarTestAction; }
  public ActionManager.InterruptAction getInterruptAction() { return interruptAction; }
  public ActionManager.InterruptAction getToolbarInterruptAction() { return toolbarInterruptAction; }
  public ActionManager.GoToPastConsoleHistory getGoToPastConsoleHistory() { return goToPastConsoleHistory; }
  public ActionManager.GoToRecentConsoleHistory getGoToRecentConsoleHistory() { return goToRecentConsoleHistory; }
  public ActionManager.KeyboardGuideAction getKeyboardGuideAction() { return keyboardGuideAction; }

  // Thabitha: font size action getters
  public ActionManager.IncreaseFontSizeAction getIncreaseFontSizeAction() { return increaseFontSizeAction; }
  public ActionManager.DecreaseFontSizeAction getDecreaseFontSizeAction() { return decreaseFontSizeAction; }
  public ActionManager.ToggleColourBlindAction getToggleColourBlindAction() { return toggleColourBlindAction; }

  // ---- Inner action classes ----

  protected class TestAction extends AbstractAction {
    public TestAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent arg0) {
      WindowManager wm = WindowManager.getInstance();
      if (!wm.isTestEnabled()) { Toolkit.getDefaultToolkit().beep(); return; }
      InterpreterManager im = InterpreterManager.getInstance();
      ParserManager pm = ParserManager.getInstance();
      if (pm.getParser().getTests().size() > 0) {
        wm.getTreeWindow().runTests();
      }
    }
  }

  protected class ExitProgramAction extends AbstractAction {
    public ExitProgramAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent e) {
      FileManager.getInstance().ensureSaved();
      System.exit(0);
    }
  }

  // Goutham: new file via keyboard dialog
  protected class NewFileAction extends AbstractAction {
    public NewFileAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      FileManager fm = FileManager.getInstance();
      fm.ensureSaved();
      File newFile = KeyboardNewFileDialog.show(wm.getMainScreenFrame());
      if (newFile == null) return;
      try {
        newFile.getParentFile().mkdirs();
        if (!newFile.exists()) newFile.createNewFile();
      } catch (java.io.IOException ex) {
        JOptionPane.showMessageDialog(wm.getMainScreenFrame(),
            "Could not create file: " + ex.getMessage(), "File creation error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      wm.openFile(newFile);
    }
  }

  // Goutham: open via keyboard dialog
  protected class OpenFileAction extends AbstractAction {
    public OpenFileAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      FileManager fm = FileManager.getInstance();
      fm.ensureSaved();
      File selected = KeyboardOpenFileDialog.show(wm.getMainScreenFrame());
      if (selected == null) return;
      fm.setOpenDirectory(selected.getParentFile());
      wm.openFile(selected);
    }
  }

  protected class SendEvaluationAction extends AbstractAction {
    public SendEvaluationAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      if (wm.getConsoleWindow().isEnabled()) {
        String command = wm.getConsoleWindow().getCommand();
        log.info("Send Command: " + command);
        if (!wm.isEvaluating()) wm.setStatusEvaluating();
        wm.getConsoleWindow().outputInput('\n', true);
        wm.getConsoleWindow().evalCommand(command);
      } else {
        java.awt.Toolkit.getDefaultToolkit().beep();
      }
    }
  }

  protected class ShowOptionsAction extends AbstractAction {
    public ShowOptionsAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent e) { WindowManager.getInstance().showOptionsWindow(); }
  }

  protected class ShowSearchAction extends AbstractAction {
    public ShowSearchAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent e) { WindowManager.getInstance().showSearchWindow(); }
  }

  protected class ShowAboutAction extends AbstractAction {
    public ShowAboutAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent e) { WindowManager.getInstance().showAboutWindow(); }
  }

  protected class SaveOptionsAction extends AbstractAction {
    public SaveOptionsAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      boolean essentialChange = false;
      String interpreterPath = wm.getOptionsWindow().getInterpreterPath();
      String interpreterOpts = wm.getOptionsWindow().getInterpreterOpts();
      String libraryPath = wm.getOptionsWindow().getLibraryPath();
      String outputFontSize = wm.getOptionsWindow().getOuputFontSize();
      String codeFontSize = wm.getOptionsWindow().getCodeFontSize();
      SettingsManager sm = SettingsManager.getInstance();
      if (!(sm.getSetting(Settings.INTERPRETER_PATH).equals(interpreterPath)
            && sm.getSetting(Settings.INTERPRETER_OPTS).equals(interpreterOpts)
            && sm.getSetting(Settings.LIBRARY_PATH).equals(libraryPath))) {
        sm.setSetting(Settings.INTERPRETER_PATH, interpreterPath);
        sm.setSetting(Settings.INTERPRETER_OPTS, interpreterOpts);
        sm.setSetting(Settings.LIBRARY_PATH, libraryPath);
        essentialChange = true;
      }
      sm.setSetting(Settings.TEST_FUNCTION, wm.getOptionsWindow().getTestFunction().trim());
      sm.setSetting(Settings.TEST_POSITIVE, wm.getOptionsWindow().getTestPositive().trim());
      try {
        int sz = Integer.parseInt(outputFontSize);
        wm.getConsoleWindow().setFontSize(sz);
        sm.setSetting(Settings.OUTPUT_FONT_SIZE, outputFontSize);
      } catch (NumberFormatException nfe) {}
      try {
        int sz = Integer.parseInt(codeFontSize);
        wm.getEditorWindow().setFontSize(sz);
        sm.setSetting(Settings.CODE_FONT_SIZE, codeFontSize);
      } catch (NumberFormatException nfe) {}
      // Goutham: save high-contrast setting and apply theme
      sm.setSetting(Settings.HIGH_CONTRAST_MODE,
          String.valueOf(wm.getOptionsWindow().isHighContrastSelected()));
      wm.applyTheme();

      // Thabitha: save deuteranopia setting and refresh console colours
      boolean deuteranopiaEnabled = wm.getOptionsWindow().isDeuteranopiaEnabled();
      accessibility.ColorThemeManager.setDeuteranopiaMode(deuteranopiaEnabled);
      sm.setSetting(Settings.DEUTERANOPIA_MODE, String.valueOf(deuteranopiaEnabled));
      wm.getConsoleWindow().refreshStyles();
      wm.getOptionsWindow().close();
      sm.saveSettings();
      if (essentialChange) wm.getConsoleWindow().restart();
      else wm.repaintAll();
    }
  }

  protected class SaveWizardAction extends AbstractAction {
    public SaveWizardAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      String interpreterPath = wm.getWizardWindow().getInterpreterPath();
      SettingsManager sm = SettingsManager.getInstance();
      wm.getWizardWindow().close();
      sm.setSetting(Settings.INTERPRETER_PATH, interpreterPath);
      sm.saveSettings();
      sm.loadSettings();
      InterpreterManager.getInstance().startProcess(false);
    }
  }

  protected class CloseFileAction extends AbstractAction {
    public CloseFileAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent e) {
      FileManager fm = FileManager.getInstance();
      WindowManager wm = WindowManager.getInstance();
      fm.closeCurrentFile();
      wm.setCloseEnabled(false);
      wm.setStatusNoProgram();
      wm.setStatusEvaluating();
      wm.setTitleFileName(null);
      wm.getEditorWindow().setEnabled(false);
      wm.getEditorWindow().setEditorContent("Use menu to open an existing or create a new program in the editor.");
      managers.UndoManager.getInstance().reset();
      ParserManager.getInstance().refresh();
      wm.getTreeWindow().refreshTree();
      wm.onlyConsole();
      wm.getConsoleWindow().unload();
    }
  }

  protected class PrintFileAction extends AbstractAction {
    public PrintFileAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent e) { WindowManager.getInstance().showPrintWindow(); }
  }

  protected class ShowHelpAction extends AbstractAction {
    public ShowHelpAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent e) { WindowManager.getInstance().showHelpWindow(); }
  }

  protected class EditCopyAction extends AbstractAction {
    public EditCopyAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent e) { WindowManager.getInstance().getEditorWindow().copy(); }
  }

  protected class EditCutAction extends AbstractAction {
    public EditCutAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent e) { WindowManager.getInstance().getEditorWindow().cut(); }
  }

  // YOUR: select all action
  protected class EditSelectAllAction extends AbstractAction {
    public EditSelectAllAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent e) { WindowManager.getInstance().getEditorWindow().selectAll(); }
  }

  protected class EditPasteAction extends AbstractAction {
    public EditPasteAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent e) { WindowManager.getInstance().getEditorWindow().paste(); }
  }

  protected class CompileAction extends AbstractAction {
    public CompileAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      if (!wm.isCompileEnabled()) { Toolkit.getDefaultToolkit().beep(); return; }
      wm.getEditorWindow().clearLineMark();
      FileManager.getInstance().ensureSaved();
      wm.setStatusEvaluating();
      wm.getConsoleWindow().compile();
      ParserManager.getInstance().refresh();
      wm.getTreeWindow().refreshTree();
    }
  }

  public class UndoAction extends AbstractAction {
    public UndoAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      setEnabled(false);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent e) {
      managers.UndoManager um = managers.UndoManager.getInstance();
      if (!um.canUndo()) { Toolkit.getDefaultToolkit().beep(); return; }
      try { um.undo(); } catch (CannotUndoException ex) { log.warning("Unable to undo " + ex); }
      WindowManager.getInstance().getMainMenu().updateUndoRedo();
    }
    public void updateUndoState() {
      setEnabled(managers.UndoManager.getInstance().canUndo());
    }
  }

  public class RedoAction extends AbstractAction {
    public RedoAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      setEnabled(false);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent e) {
      managers.UndoManager um = managers.UndoManager.getInstance();
      if (!um.canRedo()) { Toolkit.getDefaultToolkit().beep(); return; }
      try { um.redo(); } catch (CannotRedoException ex) { log.warning("Unable to redo " + ex); }
      WindowManager.getInstance().getMainMenu().updateUndoRedo();
    }
    public void updateRedoState() {
      setEnabled(managers.UndoManager.getInstance().canRedo());
    }
  }

  protected class RefreshTreeAction extends AbstractAction {
    public RefreshTreeAction(String text, ImageIcon icon, String desc) {
      super(text, icon); setEnabled(true); putValue(SHORT_DESCRIPTION, desc);
    }
    public void actionPerformed(ActionEvent e) {
      ParserManager.getInstance().refresh();
      WindowManager.getInstance().getTreeWindow().refreshTree();
    }
  }

  protected class ExpandTreeAction extends AbstractAction {
    public ExpandTreeAction(String text, ImageIcon icon, String desc) {
      super(text, icon); putValue(SHORT_DESCRIPTION, desc);
    }
    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      wm.getTreeWindow().refreshTree();
      wm.getTreeWindow().expandTree();
    }
  }

  protected class CollapseTreeAction extends AbstractAction {
    public CollapseTreeAction(String text, ImageIcon icon, String desc) {
      super(text, icon); putValue(SHORT_DESCRIPTION, desc);
    }
    public void actionPerformed(ActionEvent e) {
      WindowManager wm = WindowManager.getInstance();
      wm.getTreeWindow().refreshTree();
      wm.getTreeWindow().collapseTree();
    }
  }

  protected class ToggleTreeAction extends AbstractAction {
    public ToggleTreeAction(String text, ImageIcon icon, String desc) {
      super(text, icon); putValue(SHORT_DESCRIPTION, desc);
    }
    public void actionPerformed(ActionEvent e) { WindowManager.getInstance().toggleTree(); }
  }

  protected class ToggleConsoleAction extends AbstractAction {
    public ToggleConsoleAction(String text, ImageIcon icon, String desc) {
      super(text, icon); putValue(SHORT_DESCRIPTION, desc);
    }
    public void actionPerformed(ActionEvent e) { WindowManager.getInstance().toggleConsole(); }
  }

  protected class GoToPastConsoleHistory extends AbstractAction {
    public void actionPerformed(ActionEvent arg0) {
      ConsoleWindow console = WindowManager.getInstance().getConsoleWindow();
      if (console.isEnabled()) console.commandHistoryBackwards();
      else java.awt.Toolkit.getDefaultToolkit().beep();
    }
  }

  protected class GoToRecentConsoleHistory extends AbstractAction {
    public void actionPerformed(ActionEvent arg0) {
      ConsoleWindow console = WindowManager.getInstance().getConsoleWindow();
      if (console.isEnabled()) console.commandHistoryForwards();
      else java.awt.Toolkit.getDefaultToolkit().beep();
    }
  }

  protected class InterruptAction extends AbstractAction {
    public InterruptAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent arg0) {
      WindowManager wm = WindowManager.getInstance();
      if (!wm.isInterruptEnabled()) { Toolkit.getDefaultToolkit().beep(); return; }
      wm.getConsoleWindow().interrupt();
    }
  }

  // Goutham: file explorer via keyboard dialog
  protected class FileExplorerAction extends AbstractAction {
    public FileExplorerAction(String text, ImageIcon icon, String desc, Integer mnemonic, KeyStroke accelerator) {
      super(text, icon);
      putValue(SHORT_DESCRIPTION, desc);
      putValue(MNEMONIC_KEY, mnemonic);
      putValue(ACCELERATOR_KEY, accelerator);
    }
    public void actionPerformed(ActionEvent e) { WindowManager.getInstance().showFileExplorer(); }
  }

  // YOUR: keyboard guide action reads all shortcuts aloud via TTS
  protected class KeyboardGuideAction extends AbstractAction {
    public KeyboardGuideAction() {
      super("Keyboard Shortcuts");
      putValue(SHORT_DESCRIPTION, "Read keyboard shortcuts aloud");
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
    }
    public void actionPerformed(ActionEvent e) { accessibility.KeyboardGuide.readShortcuts(); }
  }

  // Thabitha: increase font size via Cmd+=
  protected class IncreaseFontSizeAction extends AbstractAction {
    public IncreaseFontSizeAction() {
      super("Increase Font Size");
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS,
          Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
    }
    public void actionPerformed(ActionEvent e) {
      if (accessibility.FontSizeManager.canIncrease()) {
        accessibility.FontSizeManager.increaseFontSize();
        WindowManager wm = WindowManager.getInstance();
        if (wm != null && wm.getAccessibilityPanel() != null)
          wm.getAccessibilityPanel().updateDisplay();
      }
    }
  }

  // Thabitha: decrease font size via Cmd+-
  protected class DecreaseFontSizeAction extends AbstractAction {
    public DecreaseFontSizeAction() {
      super("Decrease Font Size");
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS,
          Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(), false));
    }
    public void actionPerformed(ActionEvent e) {
      if (accessibility.FontSizeManager.canDecrease()) {
        accessibility.FontSizeManager.decreaseFontSize();
        WindowManager wm = WindowManager.getInstance();
        if (wm != null && wm.getAccessibilityPanel() != null)
          wm.getAccessibilityPanel().updateDisplay();
      }
    }
  }

  // Thabitha: toggle deuteranopia colour-blind mode on/off (Cmd+Shift+C)
  protected class ToggleColourBlindAction extends AbstractAction {
    public ToggleColourBlindAction() {
      super("Toggle Colour Blind Mode");
      putValue(SHORT_DESCRIPTION, "Toggle deuteranopia colour blind mode on or off");
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C,
          Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()
          | java.awt.event.InputEvent.SHIFT_DOWN_MASK, false));
    }
    public void actionPerformed(ActionEvent e) {
      boolean nowEnabled = !accessibility.ColorThemeManager.isDeuteranopiaEnabled();
      accessibility.ColorThemeManager.setDeuteranopiaMode(nowEnabled);
      SettingsManager.getInstance().setSetting(
          utils.Settings.DEUTERANOPIA_MODE, String.valueOf(nowEnabled));
      WindowManager.getInstance().getConsoleWindow().refreshStyles();
      String msg = nowEnabled
          ? "Colour blind mode enabled. Console colours adjusted for deuteranopia."
          : "Colour blind mode disabled. Normal colours restored.";
      accessibility.TTSManager.getInstance().speak(msg);
    }
  }

} /* end ActionManager */
