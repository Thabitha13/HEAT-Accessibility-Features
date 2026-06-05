package view.toolbars;

import managers.ActionManager;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * The menus used within HEAT
 */
public class MainMenu {
  private JMenuBar jMenuBar = new JMenuBar();

  // Program menu items
  private JMenu jMenuFile = new JMenu();
  private JMenuItem jMenuItemNew = new JMenuItem();          // Goutham
  private JMenuItem jMenuItemOpen = new JMenuItem();
  private JMenuItem jMenuItemFileExplorer = new JMenuItem(); // Goutham
  private JMenuItem jMenuItemCloseFile = new JMenuItem();
  private JMenuItem jMenuItemPrint = new JMenuItem();
  private JMenuItem jMenuItemOptions = new JMenuItem();
  private JMenuItem jMenuItemExit = new JMenuItem();

  // Edit menu items
  private JMenu jMenuEdit = new JMenu();
  private JMenuItem jMenuItemCopy = new JMenuItem();
  private JMenuItem jMenuItemCut = new JMenuItem();
  private JMenuItem jMenuItemPaste = new JMenuItem();
  private JMenuItem jMenuItemSearch = new JMenuItem();

  // Run menu items
  private JMenu jMenuRun = new JMenu();
  private JMenuItem jMenuItemCompile = new JMenuItem();
  private JMenuItem jMenuItemInterrupt = new JMenuItem();
  private JMenuItem jMenuItemTest = new JMenuItem();

  // Help menu items
  private JMenu jMenuHelp = new JMenu();
  private JMenuItem jMenuItemContents = new JMenuItem();
  private JMenuItem jMenuItemAbout = new JMenuItem();
  private JMenuItem jMenuItemKeyboardGuide = new JMenuItem(); // YOUR

  private ActionManager.UndoAction undoAction = ActionManager.getInstance().getUndoAction();
  private ActionManager.RedoAction redoAction = ActionManager.getInstance().getRedoAction();

  public MainMenu() {
    try { jbInit(); } catch (Exception e) { e.printStackTrace(); }
  }

  public void jbInit() throws Exception {
    ActionManager am = ActionManager.getInstance();

    // Program Menu
    jMenuFile.setText("Program");
    jMenuFile.setMnemonic('p');

    // Goutham: New file item
    jMenuItemNew.setAction(am.getNewFileAction());
    jMenuItemNew.setText("New..");

    jMenuItemOpen.setAction(am.getOpenFileAction());

    // Goutham: file explorer item
    jMenuItemFileExplorer.setAction(am.getFileExplorerAction());
    jMenuItemFileExplorer.setText("Navigate Files..");

    jMenuItemCloseFile.setAction(am.getCloseFileAction());
    jMenuItemCloseFile.setEnabled(false);
    jMenuItemOptions.setAction(am.getShowOptionsAction());
    jMenuItemOptions.setMnemonic('o');
    jMenuItemExit.setText("Quit");
    jMenuItemExit.setMnemonic('Q');
    jMenuItemExit.setAction(am.getExitProgramAction());

    jMenuFile.add(jMenuItemNew);
    jMenuFile.add(jMenuItemOpen);
    jMenuFile.add(jMenuItemFileExplorer);
    jMenuFile.add(jMenuItemCloseFile);
    jMenuFile.addSeparator();
    jMenuFile.add(jMenuItemOptions);
    jMenuFile.addSeparator();
    jMenuFile.add(jMenuItemExit);

    // Edit Menu
    jMenuEdit.setText("Edit");
    jMenuEdit.setMnemonic('e');
    jMenuEdit.add(undoAction);
    jMenuEdit.add(redoAction);
    jMenuEdit.addSeparator();
    jMenuItemCut.setAction(am.getEditCutAction());
    jMenuItemCut.setMnemonic('t');
    jMenuItemCopy.setAction(am.getEditCopyAction());
    jMenuItemCopy.setMnemonic('c');
    jMenuItemPaste.setAction(am.getEditPasteAction());
    jMenuItemPaste.setMnemonic('p');
    jMenuItemSearch.setAction(am.getSearchAction());
    jMenuEdit.add(jMenuItemCut);
    jMenuEdit.add(jMenuItemCopy);
    jMenuEdit.add(jMenuItemPaste);
    jMenuEdit.add(jMenuItemSearch);

    // Run Menu — YOUR: F5/F6/F7 accelerators
    jMenuRun.setText("Run");
    jMenuRun.setMnemonic('r');
    jMenuItemCompile.setAction(am.getCompileAction());
    jMenuItemCompile.setText("Load & Compile");
    jMenuItemCompile.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
    jMenuItemCompile.setMnemonic('c');
    jMenuItemInterrupt.setAction(am.getInterruptAction());
    jMenuItemInterrupt.setText("Interrupt");
    jMenuItemInterrupt.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, 0));
    jMenuItemInterrupt.setMnemonic('i');
    jMenuItemTest.setAction(am.getTestAction());
    jMenuItemTest.setText("Test");
    jMenuItemTest.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, 0));
    jMenuItemTest.setMnemonic('t');
    jMenuRun.add(jMenuItemCompile);
    jMenuRun.add(jMenuItemInterrupt);
    jMenuRun.add(jMenuItemTest);
    jMenuRun.addSeparator();
    // Thabitha: font size shortcuts in Run menu
    jMenuRun.add(ActionManager.getInstance().getIncreaseFontSizeAction());
    jMenuRun.add(ActionManager.getInstance().getDecreaseFontSizeAction());
    jMenuRun.addSeparator();
    // Thabitha: colour blind mode toggle
    jMenuRun.add(ActionManager.getInstance().getToggleColourBlindAction());

    // Help Menu — YOUR: F1 keyboard guide
    jMenuHelp.setText("Help");
    jMenuHelp.setMnemonic('H');
    jMenuItemContents.setAction(am.getShowHelpAction());
    jMenuItemAbout.setAction(am.getShowAboutAction());
    jMenuItemAbout.setMnemonic('a');
    jMenuItemKeyboardGuide.setAction(am.getKeyboardGuideAction());
    jMenuItemKeyboardGuide.setText("Keyboard Shortcuts");
    jMenuItemKeyboardGuide.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
    jMenuHelp.add(jMenuItemContents);
    jMenuHelp.addSeparator();
    jMenuHelp.add(jMenuItemKeyboardGuide);
    jMenuHelp.addSeparator();
    jMenuHelp.add(jMenuItemAbout);

    jMenuBar.add(jMenuFile);
    jMenuBar.add(jMenuEdit);
    jMenuBar.add(jMenuRun);
    jMenuBar.add(jMenuHelp);
  }

  public JMenuBar getToolBar() { return jMenuBar; }

  public void updateUndoRedo() {
    undoAction.updateUndoState();
    redoAction.updateRedoState();
    ActionManager am = ActionManager.getInstance();
    am.getToolbarUndoAction().updateUndoState();
    am.getToolbarRedoAction().updateRedoState();
  }

  public void setCloseEnabled(boolean enabled) { jMenuItemCloseFile.setEnabled(enabled); }
  public void setInterruptEnabled(boolean enabled) { jMenuItemInterrupt.setEnabled(enabled); }
  public void setTestEnabled(boolean enabled) { jMenuItemTest.setEnabled(enabled); }
  public void setCompileEnabled(boolean enabled) { jMenuItemCompile.setEnabled(enabled); }
}
