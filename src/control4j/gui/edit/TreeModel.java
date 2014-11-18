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

import java.util.ArrayList;
import java.util.LinkedList;
import java.awt.Container;
import java.awt.Component;
import javax.swing.tree.TreePath;
import javax.swing.JComponent;
import javax.swing.JPanel;
//import javax.swing.tree.TreeModel;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import control4j.gui.Changer;
import control4j.gui.GuiObject;
import control4j.gui.VisualObject;
import control4j.gui.VisualContainer;
import control4j.gui.Screens;
import control4j.gui.components.Screen;

/**
 *
 */
public class TreeModel implements javax.swing.tree.TreeModel, FileListener
{
  private LinkedList<TreeModelListener> listeners 
    = new LinkedList<TreeModelListener>();

  /** 
   *  Root element which is screens object.
   */
  private Screens root;

  /**
   *  Creates an empty model.
   */
  public TreeModel()
  {
    root = new Screens();
  }

  /**
   *
   */
  public void addTreeModelListener(TreeModelListener l)
  {
    if (l != null)
      listeners.add(l);
  }

  /**
   *
   */
  public void removeTreeModelListener(TreeModelListener l)
  {
    if (l != null)
      listeners.remove(l);
  }

  /**
   *
   */
  public Object getChild(Object parent, int index)
  {
    if (parent instanceof VisualObject)
    {
      VisualObject container = (VisualObject)parent;
      return container.getChild(index);
    }
    return null;
  }

  /**
   *  Number of children of the given object.
   */
  public int getChildCount(Object parent)
  {
    return ((GuiObject)parent).size();
  }

  /**
   *
   */
  public int getIndexOfChild(Object parent, Object child)
  {
    try
    {
      return ((GuiObject)parent).getIndexOfChild((GuiObject)child);
    }
    catch (Exception e)
    {
      return -1;
    }
  }

  /**
   *  Returns Screens object.
   */
  public Object getRoot()
  {
    return root;
  }

  /**
   *
   */
  public boolean isLeaf(Object node)
  {
    return ! ((GuiObject)node).hasChildren();
  }

  /**
   *
   */
  public void valueForPathChanged(TreePath path, Object newValue)
  {
  }

  public void setRoot(Screens screens)
  {
    root = screens;
    fireTreeStructureChanged(root);
  }

  /**
   *  Notify all of the registered listeners, that a node was added.
   *
   *  @param node
   *             an object which was added
   */
  void fireTreeNodeInserted(Object node)
  {
    Object parent = getParent(node);
    Object[] path = getPath(parent);
    int[] indexes = new int[] {getIndexOfChild(parent, node)};
    Object[] children = new Object[] {node};
    TreeModelEvent e = new TreeModelEvent(this, path, indexes, children);
    for (TreeModelListener listener : listeners)
      listener.treeNodesInserted(e);
  }

  void fireTreeNodeChanged(Object node)
  {
    Object[] path = getPath(getParent(node));
    int[] indexes = new int[] {getIndexOfChild(getParent(node), node)};
    Object[] children = new Object[] {node};
    TreeModelEvent e = new TreeModelEvent(this, path, indexes, children);
    for (TreeModelListener listener : listeners)
      listener.treeNodesChanged(e);
  }

  /**
   *  Called when the node has been removed
   *
   *  @param node
   *             a node that has been deleted
   */
  void fireTreeNodeRemoved(Object parent, Object node, int index)
  {
    Object[] parentPath = getPath(parent);
    int[] indexes = new int[] { index };
    Object[] children = new Object[] { node };
    TreeModelEvent e = new TreeModelEvent(this, parentPath, indexes, children);
    for (TreeModelListener listener : listeners)
      listener.treeNodesRemoved(e);
  }

  /**
   *  Called when the structure drasticaly changed in some way. 
   *
   *  @param node
   *             object indentifying modified subtree
   */
  void fireTreeStructureChanged(Object node)
  {
    TreeModelEvent e = new TreeModelEvent(this, getPath(node));
    for (TreeModelListener listener : listeners)
      listener.treeStructureChanged(e);
  }

  /**
   *  Returns a path for a given node.
   */
  protected Object[] getPath(Object node)
  {
    ArrayList<Object> path = new ArrayList<Object>();
    while (node != root)
    {
      path.add(0, node);
      node = getParent(node);
    }
    path.add(0, node);
    return path.toArray();
  }

  /**
   *  Returns parent of the given node.
   */
  public static Object getParent(Object node)
  {
    return ((GuiObject)node).getParent();
  }

  public void fileChanged(FileEvent e)
  {
    setRoot(e.getScreens());
  }

}
