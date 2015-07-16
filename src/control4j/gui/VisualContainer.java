package control4j.gui;

/*
 *  Copyright 2013, 2014, 2015 Jiri Lidinsky
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
   *  Adds a given child at the end of visual children list. If this
   *  object has a visual component, visual component is created even
   *  for the child. Created component is added at the end of this
   *  visual component children list. And finally configureVisualComponent
   *  is called for the child.
   *
   *  @param child
   *             object to be added as a child
   */
  public void add(VisualObject child)
  {
    insertChild(child, firstChangerIndex);
    firstChangerIndex++;
    if (component != null)
    {
      JComponent childComponent = child.createVisualComponent();
      component.add(childComponent);
      child.configureVisualComponent();
      component.revalidate();
      component.repaint();
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
    if (component != null)
    {
      JComponent childComponent = child.createVisualComponent();
      component.add(childComponent, index);
      child.configureVisualComponent();
      component.revalidate();
      component.repaint();
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

  /**
   *
   */
  @Override
  public GuiObject removeChild(int index)
  {
    GuiObject child = getChild(index);
    if (child.isVisual())
    {
      firstChangerIndex--;
      if (component != null)
      {
        component.remove(index);
        ((VisualObject)child).releaseVisualComponent();
        component.revalidate();
        component.repaint();
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
    return getChildren().size() - firstChangerIndex;
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
   *  Creates a visual component for each VisualObject child.
   *  Child visual component is than added to this visual component
   *  and finally, configureVisualComponent is called for each
   *  VisualObject child.
   */
  @Override
  protected void configureVisualComponent()
  {
    for (int i=0; i<getVisualObjectCount(); i++)
    {
      JComponent childComponent = getVisualObject(i).createVisualComponent();
      component.add(childComponent);
      getVisualObject(i).configureVisualComponent();
    }
  }

  /**
   *  Calls a releseVisualComponent for all of the visual child objects.
   *  Moreover, it removes child visual components from this visual
   *  component.
   */
  @Override
  protected void releaseVisualComponent()
  {
    if (component != null)
    {
      for (int i=0; i<getVisualObjectCount(); i++)
        getVisualObject(i).releaseVisualComponent();
      component.removeAll();
    }
    super.releaseVisualComponent();
  }

  @Override
  public boolean isVisualContainer()
  {
    return true;
  }

  /**
   *
   */
  @Override
  public GuiObject clone(boolean full)
  {
    VisualContainer clone = (VisualContainer)super.clone(full);
    clone.firstChangerIndex = this.firstChangerIndex;
    return clone;
  }

}
