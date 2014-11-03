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

/**
 *  Iterates over all of the components.
 */
public class ComponentIterator implements java.util.Iterator<Container>
{
  private Container root;
  private boolean end = false;
  private java.util.ArrayList<Integer> indexes 
      = new java.util.ArrayList<Integer>();

  public ComponentIterator(Container root)
  {
    this.root = root;
  }

  public boolean hasNext()
  {
    return !end;
  }

  public Container next()
  {
    if (end) new java.util.NoSuchElementException();
    // find the element to return
    Container result = root;
    for (Integer i : indexes)
      result = (Container)result.getComponent(i);
    // find next element
    if (result.getComponentCount() > 0)
      indexes.add(0);
    else
    {
      Container parent = (Container)result.getParent();
      while(true)
      {
        int lastIndex = indexes.size()-1;
        if (lastIndex < 0)
        {
          end = true;
          break;
        }
        int index = indexes.get(lastIndex);
        if (parent.getComponentCount() > index+1)
        {
          indexes.set(lastIndex, index+1);
          break;
        }
        else
        {
          indexes.remove(lastIndex);
          parent = (Container)parent.getParent();
        }
      }
    }
    return result;
  }

  public void remove()
  {
  }

}
