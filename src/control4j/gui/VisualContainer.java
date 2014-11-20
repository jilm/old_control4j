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

import javax.swing.JComponent;

/**
 *
 *
 *
 */
public abstract class VisualContainer extends VisualObject
{

  /**
   *  Index of the first changer object in the list of children.
   */
  private int firstChangerIndex = 0;

  /**
   *
   */
  public void add(VisualObject child)
  {
    insertChild(child, firstChangerIndex);
    firstChangerIndex++;
    if (getVisualComponent() != null)
    {
      JComponent childComponent = child.createVisualComponent();
      getVisualComponent().add(childComponent);
      child.configureVisualComponent();
      getVisualComponent().validate();
    }
  }

  /**
   *
   */
  @Override
  public void insert(Changer child, int index)
  {
    insertChild(child, index + firstChangerIndex);
  }

  /**
   *
   */
  public void insert(VisualObject child, int index)
  {
    insertChild(child, index);
    firstChangerIndex++;
    if (getVisualComponent() != null)
    {
      JComponent childComponent = child.createVisualComponent();
      getVisualComponent().add(childComponent);
      child.configureVisualComponent();
      getVisualComponent().validate();
    }
  }

  /**
   *
   */
  @Override
  public Changer removeChanger(int index)
  {
    if (index < 0)
      throw new IndexOutOfBoundsException();
    return (Changer)removeChild(index + firstChangerIndex);
  }

  /**
   *
   */
  public VisualObject removeVisualObject(int index)
  {
    if (index >= firstChangerIndex)
      throw new IndexOutOfBoundsException();
    return (VisualObject)removeChild(index);
  }

  @Override 
  public GuiObject removeChild(int index)
  {
    GuiObject child = getChild(index);
    if (child.isVisual()) 
    {
      firstChangerIndex--;
      JComponent visualComponent = getVisualComponent();
      if (visualComponent != null)
      {
	visualComponent.remove(index);
	visualComponent.validate();
	((VisualObject)child).releaseVisualComponent();
      }
    }
    return super.removeChild(index);
  }

  /**
   *
   */
  @Override
  public int getChangerCount()
  {
    return size() - firstChangerIndex;
  }

  /**
   *
   */
  public int getVisualObjectCount()
  {
    return firstChangerIndex;
  }

  /**
   *
   */
  @Override
  public Changer getChanger(int index)
  {
    if (index < 0)
      throw new IndexOutOfBoundsException();
    return (Changer)getChild(index + firstChangerIndex);
  }

  /**
   *
   */
  public VisualObject getVisualObject(int index)
  {
    if (index >= firstChangerIndex)
      throw new IndexOutOfBoundsException();
    return (VisualObject)getChild(index);
  }

  /**
   *  Calls a releseVisualComponent for all of the visual child objects.
   *  Moreover, it removes child visual components from this visual 
   *  component. The descendant of this class shoud first call super, 
   *  and than release its own visual object.
   */
  @Override
  protected void releaseVisualComponent()
  {
    JComponent component = getVisualComponent();
    if (component != null)
    {
      for (int i=0; i<getVisualObjectCount(); i++)
        getVisualObject(i).releaseVisualComponent();
      component.removeAll();
    }
  }

  @Override
  public boolean isVisualContainer()
  {
    return true;
  }

}
