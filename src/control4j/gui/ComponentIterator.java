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

/**
 *
 *  Iterates over all of the components.
 *
 */
public class ComponentIterator implements java.util.Iterator<GuiObject>
{

  /**
   *  A buffer that contains nodes to be expanded. At the begining it
   *  contains only the root element. At the end, this buffer is empty.
   */
  private LinkedList<GuiObject> buffer 
    = new LinkedList<GuiObject>();

  private Class<? extends GuiObject> filter = GuiObject.class;

  private GuiObject next;

  /**
   *
   */
  public ComponentIterator(GuiObject root)
  {
    buffer.add(root);
  }

  /**
   *
   */
  public ComponentIterator(GuiObject root, Class<? extends GuiObject> filter)
  {
    buffer.add(root);
    this.filter = filter;
  }

  /**
   *
   */
  public boolean hasNext()
  {
    return next != null;
  }

  /**
   *
   */
  public GuiObject next()
  {
    if (!hasNext()) new java.util.NoSuchElementException();
    GuiObject result = next;
    next = filteredNext();
    return result;
  }

  /**
   *
   */
  private GuiObject filteredNext()
  {
    GuiObject node;
    while (buffer.size() > 0)
    {
      node = findNext();
      if (node.getClass().isAssignableFrom(filter)) return node;
    }
    return null;
  }

  /**
   *
   */
  private GuiObject findNext()
  {
    // get first element from the buffer
    GuiObject node = buffer.removeFirst();
    // insert all of its children at the end of the buffer
    if (node.hasChildren())
    {
      VisualObject container = (VisualObject)node;
      for (int i=0; i<container.size(); i++)
        buffer.add(container.getChild(i));
    }
    // return the node
    return node;
  }

  /**
   *  Not implemented.
   */
  public void remove()
  {
  }

}
