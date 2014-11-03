package control4j.gui;

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

import java.util.LinkedList;
import java.awt.Container;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeModel;
import javax.swing.event.TreeModelListener;

public class GuiStructureTreeModel implements TreeModel
{
  private LinkedList<TreeModelListener> listeners 
    = new LinkedList<TreeModelListener>();

  private Container root;

  public GuiStructureTreeModel(Container root)
  {
    this.root = root;
  }

  public void addTreeModelListener(TreeModelListener l)
  {
    if (l != null)
      listeners.add(l);
  }

  public Object getChild(Object parent, int index)
  {
    return ((Container)parent).getComponent(index);
  }

  public int getChildCount(Object parent)
  {
    return ((Container)parent).getComponentCount();
  }

  public int getIndexOfChild(Object parent, Object child)
  {
    Container parentContainer = (Container)parent;
    for (int i=0; i<parentContainer.getComponentCount(); i++)
    {
      if (child == parentContainer.getComponent(i))
        return i;
    }
    return -1;
  }

  public Object getRoot()
  {
    return root;
  }

  public boolean isLeaf(Object node)
  {
    return ((Container)node).getComponentCount() == 0;
  }

  public void removeTreeModelListener(TreeModelListener l)
  {
    if (l != null)
      listeners.remove(l);
  }

  public void valueForPathChanged(TreePath path, Object newValue)
  {
  }
}
