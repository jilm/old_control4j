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
//import javax.swing.tree.TreeModel;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import control4j.gui.changers.IChangeable;
import control4j.gui.changers.Changer;
import control4j.gui.Screens;
import control4j.gui.components.Screen;

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
    Container parentContainer = (Container)parent;
    int size = parentContainer.getComponentCount();
    Object result;
    if (index < size)
      result = parentContainer.getComponent(index);
    else if (parent instanceof IChangeable)
      result = ((IChangeable)parent).getChanger(index - size);
    else
      result = null;
    return result;
  }

  /**
   *  Number of children of the given object.
   */
  public int getChildCount(Object parent)
  {
    if (parent instanceof Changer) return 0;
    int count = ((Container)parent).getComponentCount();
    if (parent instanceof IChangeable)
      count += ((IChangeable)parent).getChangerCount();
    return count;
  }

  /**
   *
   */
  public int getIndexOfChild(Object parent, Object child)
  {
    if (child instanceof Changer)
    {
      int index = getIndexOfChild((IChangeable)parent, (Changer)child);
      return index + ((Container)parent).getComponentCount();
    }
    else
      return getIndexOfChild((Container)parent, child);
  }

  /**
   *
   */
  private int getIndexOfChild(IChangeable parent, Changer child)
  {
    for (int i=0; i<parent.getChangerCount(); i++)
      if (child == parent.getChanger(i))
        return i;
    return -1;
  }

  private int getIndexOfChild(Container parent, Object child)
  {
    for (int i=0; i<parent.getComponentCount(); i++)
      if (child == parent.getComponent(i))
        return i;
    return -1;
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
    return getChildCount(node) == 0;
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
   *  Adds a new screen at the end of all the screens.
   */
  public void addScreen()
  {
    fireTreeNodeInserted(root.addScreen());
  }

  public void addChild(JComponent parent, JComponent child)
  {
    fireTreeNodeInserted(parent.add(child));
  }

  public void addChild(IChangeable parent, Changer child)
  {
    parent.addChanger(child);
    fireTreeNodeInserted(child);
  }

  /**
   *  Removes one object which path is given as an parameter.
   */
  public void remove(TreePath nodePath)
  {
    Object node = nodePath.getLastPathComponent();
    if (node instanceof Component)
    {
      Component component = (Component)node;
      Container parent = component.getParent();
      int index = getIndexOfChild(parent, node);
      parent.remove(component);
      fireTreeNodeRemoved(nodePath.getParentPath(), node, index);
    }
    else if (node instanceof Changer)
    {
      Changer changer = (Changer)node;
      IChangeable parent = changer.getParent();
      int index = getIndexOfChild(parent, node);
      parent.removeChanger(changer);
      fireTreeNodeRemoved(nodePath.getParentPath(), node, index);
    }
    else
    {
      assert false;
    }
  }

  /**
   *  Notify all of the registered listeners, that a node was added.
   */
  protected void fireTreeNodeInserted(Object node)
  {
    Object parent = getParent(node);
    Object[] path = getPath(parent);
    int[] indexes = new int[] {getIndexOfChild(parent, node)};
    Object[] children = new Object[] {node};
    TreeModelEvent e = new TreeModelEvent(this, path, indexes, children);
    for (TreeModelListener listener : listeners)
      listener.treeNodesInserted(e);
  }

  protected void fireTreeNodeChanged(Object node)
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
  protected void fireTreeNodeRemoved(TreePath parentPath, Object node, int index)
  {
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
  protected void fireTreeStructureChanged(Object node)
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
   *  Returns parent of the given node. Node may be a componenet
   *  or a changer.
   */
  public static Object getParent(Object node)
  {
    if (node instanceof Changer)
      return ((Changer)node).getParent();
    else
      return ((Container)node).getParent();
  }

  public void fileChanged(FileEvent e)
  {
    setRoot(e.getScreens());
  }

}
