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

import java.awt.Container;
import java.util.ArrayList;
import control4j.gui.changers.Changer;
import control4j.gui.changers.IChangeable;

/**
 *  Iterates over all of the changers.
 */
public class ChangerIterator implements java.util.Iterator<Changer>
{

  /**
   *
   */
  private ComponentIterator components;

  /**
   *  A component which contains at least one changer. If there is not
   *  any other such component under the root component it contains null.
   */
  private IChangeable component = null;

  /**
   *  Index of the changer which will be returned by the next method.
   */
  private int index = 0;

  /**
   *  Initialize the iterator.
   *
   *  @param root
   *             a container which will be scanned for changers.
   */
  public ChangerIterator(Container root)
  {
    this.components = new ComponentIterator(root);
    findNextChangeableComponent();
  }

  /**
   *  Finds next component which contains some changer and stores
   *  such component into component field. Index field is set to
   *  zero. If this method doesn't find any such component it 
   *  does nothing.
   */
  private void findNextChangeableComponent()
  {
    while (components.hasNext())
    {
      Container container = components.next();
      if (container instanceof IChangeable 
          && ((IChangeable)container).getChangerCount() > 0)
      {
        component = (IChangeable)container;
	index = 0;
	break;
      }
    }
  }
  
  /**
   *
   */
  public boolean hasNext()
  {
    if (component == null) 
      return false;
    else
      return true;
  }

  /**
   *
   */
  public Changer next()
  {
    if (!hasNext())
      throw new java.util.NoSuchElementException();
    Changer result = component.getChanger(index);
    index++;
    if (index >= component.getChangerCount())
    {
      component = null;
      findNextChangeableComponent();
    }
    return result;
  }

  /**
   *  This method is not implemented.
   */
  public void remove()
  {
    throw new UnsupportedOperationException();
  }

}
