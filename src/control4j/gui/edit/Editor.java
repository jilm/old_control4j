package control4j.gui.edit;

/*
 *  Copyright 2013, 2014 Jiri Lidinsky
 *
 *  This file is part of control4j.
 *
 *  control4j is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 3.
 *
 *  control4j is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with control4j.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.JTable;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Container;
import java.awt.Component;

import control4j.gui.Screens;
import control4j.gui.components.Screen;
import control4j.gui.components.Circle;
import control4j.gui.components.*;
import control4j.gui.Writer;
import control4j.gui.changers.*;
import control4j.gui.ComponentIterator;
import control4j.gui.GuiObject;
import control4j.gui.VisualObject;
import control4j.gui.VisualContainer;
import control4j.gui.Changer;

import control4j.scanner.Scanner;
import control4j.scanner.KeyValueTableModel;

/**
 *
 *  GUI visual editor.
 *
 */
public class Editor implements ActionListener, TreeSelectionListener, 
TreeModelListener, FileListener
{

  private JFrame frame;
  private Screens screens;
  private Tree guiStructureTree;
  private TreeModel treeModel;
  private JTable propertyTable;
  private KeyValueTableModel propertyTableModel;
  private ComponentToTreeLink componentToTreeLink;
  private JSplitPane split;

  private FileHandling file;
  private EditHandling edit;
  private LayoutHandling layout;

  /** filename that was given from the command line */
  private String filename;

  /** this object is singleton */
  private static Editor instance;

  /**
   *
   */
  public static void main(String[] args)
  {
    // create an instance of the editor
    instance = new Editor();
    // create and show gui
    instance.createMainFrame();
    // load file from the command line
    if (args.length > 0) instance.filename = args[0];
    javax.swing.SwingUtilities.invokeLater(
      new Runnable()
      {
        public void run()
        {
          if (instance.filename != null)
          {
            try
            {
              instance.file.load(instance.filename);
            }
            catch (java.io.IOException e) { }
          }
          else
	  {
            instance.file.fileNew();
	  }
        }
      }
    );
    // show the window
    instance.show();
  }

  /**
   *
   */
  private Editor()
  {
    super();
  }

  /**
   *
   */
  public static Editor getInstance()
  {
    return instance;
  }

  /**
   *  Create main frame of the editor
   */
  protected void createMainFrame()
  {
    // Create and set up the window
    frame = new JFrame("GUI Editor");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // split the main window
    split = new JSplitPane();
    frame.add(split);

    // split the right side
    JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    split.setBottomComponent(rightSplit);

    // Add component tree navigator
    treeModel = new TreeModel();
    guiStructureTree = new Tree(treeModel);
    guiStructureTree.getSelectionModel().setSelectionMode(
      TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    guiStructureTree.setDragEnabled(true);
    guiStructureTree.setTransferHandler(new TreeTransferHandler());
    JScrollPane treeScroll = new JScrollPane(guiStructureTree);
    rightSplit.setLeftComponent(treeScroll);
    treeModel.addTreeModelListener(this);

    // Create file handling class
    file = new FileHandling(frame);
    file.addFileEventListener(treeModel);
    file.addFileEventListener(this);

    // Create edit handling class
    edit = new EditHandling(guiStructureTree);

    // Create layout handling class
    layout = new LayoutHandling(guiStructureTree);

    // Add property editor table
    propertyTableModel = new KeyValueTableModel();
    propertyTable = new JTable(propertyTableModel);
    propertyTable.setDefaultRenderer(Object.class 
            , new ObjectPropertiesTableRenderer());
    TableCellEditor tableCellEditor = new TableCellEditor();
    propertyTable.setDefaultEditor(Object.class, tableCellEditor);
    guiStructureTree.addTreeSelectionListener(tableCellEditor);
    JScrollPane tableScroll = new JScrollPane(propertyTable);
    rightSplit.setBottomComponent(tableScroll);
    guiStructureTree.addTreeSelectionListener(this);
    propertyTableModel.addTableModelListener(guiStructureTree);

    // Add a link component -> tree
    componentToTreeLink = new ComponentToTreeLink(guiStructureTree);

    // Add a menu
    Menu menuBar = new Menu();
    frame.setJMenuBar(menuBar);
    // File menu
    file.addMenu(menuBar);
    // Edit menu
    edit.addMenu(menuBar);
    // Layout menu
    layout.addMenu(menuBar);
    // Structure manipulation menu
    menuBar.add(new JMenu("Structure")); 
    menuBar.addItem("Add Screen", "STRUCTURE_ADD_SCREEN", this)
	   .addItem("Add Component", "STRUCTURE_ADD_COMPONENT", this)
	   .addItem("Add Changer", "STRUCTURE_ADD_CHANGER", this)
	   .addSeparator()
           .addItem("Delete", "STRUCTURE_DELETE", this)
	   .addSeparator()
	   .addItem("Move Up", "STRUCTURE_MOVE_UP", this)
	   .addItem("Move Down", "STRUCTURE_MOVE_DOWN", this);
  }

  /**
   *  Show the main window.
   */
  protected void show()
  {
    javax.swing.SwingUtilities.invokeLater(
      new Runnable()
      {
        public void run()
        {
          frame.pack();
	  frame.setVisible(true);
        }
      }
    );
  }

  /**
   *  If selection in the tree window is changed, show approprite
   *  values in the property table.
   *
   *  <p>Moreover show appropriate selected screen.
   *
   *  <p>This method is listening tree selection event.
   */
  public void valueChanged(TreeSelectionEvent e)
  {
    TreePath selectedPath = e.getNewLeadSelectionPath();
    if (selectedPath != null)
    {
      Object selected = selectedPath.getLastPathComponent();
      propertyTableModel.setData(Scanner.scanObject(selected));
      // select appropriate selected screen
      if (selectedPath.getPathCount() > 1)
      {
        Screen screen = (Screen)selectedPath.getPathComponent(1);
        ((Screens)treeModel.getRoot()).showScreen(screen);
      }
    }
    else
      propertyTableModel.setData(null);
    // cancel table editing
    if (propertyTable.isEditing())
    {
      propertyTable.getCellEditor().cancelCellEditing();
    }
  }

  /**
   *  Listener method for all the menu items.
   */
  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equals("STRUCTURE_ADD_SCREEN")) 
    { 
      Screen newScreen = new Screen();
      screens.add(newScreen);
      treeModel.fireTreeNodeInserted(newScreen);
    }
    else if (e.getActionCommand().equals("STRUCTURE_DELETE"))
    {
      delete();
    }
    else if (e.getActionCommand().equals("STRUCTURE_ADD_COMPONENT"))
    {
      addComponent();
    }
    else if (e.getActionCommand().equals("STRUCTURE_ADD_CHANGER"))
    {
      addChanger();
    }
  }

  /**
   *  Deletes selected object.
   */
  protected void delete()
  {
    if (!guiStructureTree.isSelectionEmpty())
    {
      TreePath[] selectionPaths = guiStructureTree.getSelectionPaths();
      for (TreePath path : selectionPaths)
      {
	GuiObject node = (GuiObject)path.getLastPathComponent();
        VisualObject parent = (VisualObject)node.getParent();
	int index = parent.getIndexOfChild(node);
	parent.removeChild(index);
        treeModel.fireTreeNodeRemoved(parent, node, index);
      }
    }
  }

  /**
   *  Main method which should be called to add new component.
   */
  private void addComponent()
  {
    // Is the editor in the appropriate mode ?
    TreePath selectPath = guiStructureTree.getLeadSelectionPath();
    if (selectPath == null) return;
    if (selectPath.getPathCount() <= 1) return;
    GuiObject selected 
      = (GuiObject)selectPath.getLastPathComponent();
    if (! selected.isVisualContainer()) return;
    VisualContainer container = (VisualContainer)selected;
    // ask a user which component wants to add
    String componentName = letSelectComponent();
    if (componentName != null)
    {
      // create an instance of selected component
      VisualObject component 
        = (VisualObject)ComponentFactory.getInstance()
	.createInstance(componentName);
      // add the component to the appropriate place
      container.add(component);
      treeModel.fireTreeNodeInserted(component);
      // end.
      //component.setSize(component.getPreferredSize());
      //component.revalidate();
      //component.repaint();
    }
  }

  /**
   *
   */
  private void addChanger()
  {
    try
    {
      // Is the editor in the appropriate mode ?
      TreePath selectPath = guiStructureTree.getLeadSelectionPath();
      if (selectPath == null) return;
      VisualObject selected 
	= (VisualObject)selectPath.getLastPathComponent();
      // ask a user which component wants to add
      String name = letSelectChanger();
      if (name != null)
      {
        // create an instance of selected component
        Changer changer 
          = ChangerFactory.getInstance().createInstance(name);
        // add the component to the appropriate place
	selected.add(changer);
        treeModel.fireTreeNodeInserted(changer);
      }
    }
    catch (ClassCastException e)
    {
      return;
    }
  }

  /**
   *
   */
  protected String letSelectComponent()
  {
    // get array of component names
    String[] componentNames = ComponentFactory.getInstance().getComponentList();
    // show input dialog
    String selected = (String)JOptionPane.showInputDialog(
      frame, "Select component", "Components", JOptionPane.QUESTION_MESSAGE,
      null, componentNames, componentNames[0]);
    return selected;
  }

  /**
   *
   */
  protected String letSelectChanger()
  {
    // get array of changer names
    String[] changerNames = ChangerFactory.getInstance().getList();
    // show input dialog
    String selected = (String)JOptionPane.showInputDialog(
      frame, "Select changer", "Changers", JOptionPane.QUESTION_MESSAGE,
      null, changerNames, changerNames[0]);
    return selected;
  }

  public void treeNodesChanged(TreeModelEvent e)
  {

  }

  /**
   *  Add a link component -> tree to each new node.
   *  And select last inserted node.
   */
  public void treeNodesInserted(TreeModelEvent e)
  {
    // add a link component -> tree
    Object[] parentPath = e.getPath();
    Container parent = (Container)parentPath[parentPath.length-1];
    int[] indexes = e.getChildIndices();
    for (int i : indexes)
    {
      Object object = treeModel.getChild(parent, i); 
      if (object instanceof Component)
        ((Component)object).addMouseListener(componentToTreeLink);
    }
    // select last inserted node
    Object object = treeModel.getChild(parent, indexes[indexes.length-1]);
    guiStructureTree
      .setSelectionPath((new TreePath(parentPath)).pathByAddingChild(object));
  }

  public void treeNodesRemoved(TreeModelEvent e)
  {
  }

  /**
   *  If there is new Screens object, add it to the window
   */
  public void treeStructureChanged(TreeModelEvent e)
  {
  }

  /**
   *
   */
  public void fileChanged(FileEvent e)
  {
    // add a link component -> tree structure to each of the components
    ComponentIterator components = new ComponentIterator(e.getScreens());
    while (components.hasNext())
    {
      ((VisualObject)components.next()).getVisualComponent()
	.addMouseListener(componentToTreeLink);
    }
  }

  public static TreeModel getTreeModel()
  {
    return instance.treeModel;
  }
  
  /**
   *  Place new screens visual component into the left split window.
   */
  public static void setScreens(Screens screens)
  {
    instance.screens = screens;
    JComponent visualComponent = screens.createVisualComponent();
    instance.split.setLeftComponent(visualComponent);
      visualComponent.addMouseListener(instance.componentToTreeLink);
    screens.configureVisualComponent();
  }

}
