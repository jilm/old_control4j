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

import java.util.Map;
import java.util.Collection;
import java.lang.reflect.Constructor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JTree;
import javax.swing.JMenu;
import javax.swing.JComponent;
import javax.swing.tree.TreePath;
import control4j.gui.components.AbstractComponent;
import control4j.gui.components.AbstractPanel;
import control4j.gui.components.Screen;
import control4j.gui.changers.Changer;
import control4j.gui.changers.IChangeable;
import control4j.scanner.Scanner;
import control4j.scanner.Item;

/**
 *  Provides operations like copy, cut, paste, delete.
 */
class EditHandling
implements ActionListener
{

  private JTree tree;
  private TreeModel treeModel;

  private TreePath[] container;

  private boolean copy;

  public EditHandling(JTree tree)
  {
    this.tree = tree;
    this.treeModel = (TreeModel)tree.getModel();
  }

  /**
   *  Adds Edit menu at the end of the given menu bar.
   */
  void addMenu(Menu menu)
  {
    JMenu editMenu = new JMenu("Edit");
    editMenu.setMnemonic('E');
    menu.add(editMenu);
    menu.addItem("Copy", "EDIT_COPY", this, 'C');
    menu.addItem("Cut", "EDIT_CUT", this, 'u');
    menu.addItem("Paste", "EDIT_PASTE", this, 'P');
    menu.addSeparator();
    menu.addItem("Delete", "EDIT_DELETE", this, 'D');
  }

  /**
   *  Response method for Edit menu items.
   */
  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equals("EDIT_COPY"))
      doCopy();
    else if (e.getActionCommand().equals("EDIT_CUT"))
      doCut();
    else if (e.getActionCommand().equals("EDIT_PASTE"))
      doPaste();
    else if (e.getActionCommand().equals("EDIT_DELETE"))
      doDelete();
  }

  /**
   *  Response to copy request from the user. It doesn't perform any
   *  copy, it only remembers all of the selected items into the
   *  container.
   */
  protected void doCopy()
  {
    container = tree.getSelectionPaths();
    copy = true;
  }

  protected void doCut()
  {
    container = tree.getSelectionPaths();
    copy = false;
  }

  protected void doPaste()
  {
    if (copy)
    {
      for (TreePath childPath : container)
      {
        Object child = childPath.getLastPathComponent();
	Object newParent = tree.getLeadSelectionPath().getLastPathComponent();
	if (child instanceof Changer)
	{
	  doPaste((IChangeable)newParent, (Changer)child);
	}
	else if (child instanceof JComponent)
	{
	  doPaste((JComponent)newParent, (JComponent)child);
	}
	else
	  assert false;
      }
    }
  }

  /**
   *  Makes a deep copy of the child and place it as a new child of
   *  the given parent. It copy the whole subtree, it means that if
   *  the given child hase a children it copy even the children. 
   *  This method uses recursion to copy the whole tree.
   */
  private void doPaste(IChangeable parent, Changer child)
  {
    try
    {
      Changer clone = (Changer)child.clone();
      treeModel.addChild(parent, clone);
    }
    catch (CloneNotSupportedException e)
    { 
      assert false; // should not happen becouse Changer is cloneable
    }
  }

  /**
   *
   */
  private void doPaste(JComponent parent, JComponent child)
  {
    // make a copy instance of the child
    JComponent copy = (JComponent)createCopyInstance(child);
    // add it to the new parent
    treeModel.addChild(parent, copy);
    // copy properties
    copy(child, copy);
    // make copy of all the children of the child
    for (int i=0; i<child.getComponentCount(); i++)
    {
      JComponent grandchild = (JComponent)child.getComponent(i);
      doPaste(copy, grandchild);
    }
    // make copy of all the changeables !
    if (child instanceof IChangeable)
    {
      IChangeable changeableParent = (IChangeable)child;
      for (int i=0; i<changeableParent.getChangerCount(); i++)
        doPaste((IChangeable)copy, changeableParent.getChanger(i));
    }
  }

  protected void doDelete()
  {
  }

  /**
   *
   */
  static void copy(Object model, Object copy)
  {
    try
    {
      // make a copy of all the methods annotated as setters and getters
      Scanner scanner = new Scanner();
      Map<String, Item> modelScan = scanner.scanObject(model);
      Map<String, Item> copyScan = scanner.scanObject(copy);
      Collection<Item> copyItems = copyScan.values();
      for (Item copyItem : copyItems)
        if (copyItem.isWritable())
	{
	  Object value = modelScan.get(copyItem.getKey()).getValue();
	  System.out.println(copyItem.getKey() + " : " + value);
	  System.out.println(value.getClass().getName());
	  copyItem.setValue(value);
        }
    }
    catch (java.lang.reflect.InvocationTargetException e)
    {
      System.err.println(e.getClass().toString() + " : " + e.getMessage());
      java.lang.Throwable cause = e.getCause();
      System.err.println(cause.getClass().toString() + " : " + cause.getMessage());
    }
  }


  /**
   *
   */
  Object createCopyInstance(Object model)
  {
    try
    {
      // create an object of the same class
      Class _class = model.getClass();
      System.out.println(_class.getName());
      Constructor constructor = _class.getConstructor();
      Object copy = constructor.newInstance();
      return copy;
    }
    catch (NoSuchMethodException e)
    {
      System.err.println(e.getClass().toString() + " : " + e.getMessage());
    }
    catch (InstantiationException e)
    {
      // this should not happen
      System.err.println(e.getClass().toString() + " : " + e.getMessage());
      assert false;
    }
    catch (IllegalAccessException e)
    {
      System.err.println(e.getClass().toString() + " : " + e.getMessage());
    }
    catch (java.lang.reflect.InvocationTargetException e)
    {
      System.err.println(e.getClass().toString() + " : " + e.getMessage());
      java.lang.Throwable cause = e.getCause();
      System.err.println(cause.getClass().toString() + " : " + cause.getMessage());
    }
    return null;
  }

}
