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
  private LinkedList<GuiObject> buffer = new LinkedList<GuiObject>();

  /**
   *
   */
  private Class<? extends GuiObject> filter = GuiObject.class;

  /**
   *
   */
  private GuiObject next;

  /**
   *
   */
  public ComponentIterator(GuiObject root)
  {
    buffer.add(root);
    filter = GuiObject.class;
    next = filteredNext();
  }

  /**
   *
   */
  public ComponentIterator(GuiObject root, Class<? extends GuiObject> filter)
  {
    buffer.add(root);
    this.filter = filter;
    next = filteredNext();
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
    if (!hasNext()) 
      throw new java.util.NoSuchElementException();
    GuiObject result = next;
    next = filteredNext();
    return result;
  }

  /**
   *  Return first object from the buffer which is instance of the
   *  appropriate class. If the buffer is empty, it returns null.
   */
  private GuiObject filteredNext()
  {
    while (buffer.size() > 0)
    {
      GuiObject node = findNext();
      if (filter.isAssignableFrom(node.getClass())) return node;
    }
    return null;
  }

  /**
   *  Returns next GuiObject node.
   *
   *  @throws NoSuchElementException
   *             if there is no another node in the buffer
   */
  private GuiObject findNext()
  {
    // get first element from the buffer
    GuiObject node = buffer.removeFirst();
    // insert all of its children at the end of the buffer
    if (node.hasChildren())
      for (int i=0; i<node.size(); i++)
        buffer.add(node.getChild(i));
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
