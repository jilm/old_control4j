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

import java.util.ArrayList;
import java.util.NoSuchElementException;
import javax.swing.JComponent;

/**
 *
 *  Serves as a facade for objects which are instances of
 *  swing components. But it is not only a facade.
 *
 *  <p>Each instance may contain exactly one swing component
 *  which is responsible for painting. But instance of descendant
 *  of this class may live even without such component. In this
 *  case it serves as a container for visual component settings
 *  or properties. Appropriate swing component is created as
 *  soon as this object is connected as a child to object which
 *  contains swing component. Similarly, the swing component is
 *  released as soon as this component or some of the parents of
 *  this component is disconnected.
 *
 *  <p>Each instance may contain zero or more changer objects.
 *
 */
public abstract class VisualObject extends GuiObject
{

  /**
   *  A list of all children objects. It contains null until 
   *  first child is added. The list is ordered. At first there
   *  are objects which are derived from VisualObject
   *  class and then there are objects derived from  Changer.
   */
  protected ArrayList<GuiObject> children;

  /**
   *  Appends given changer at the end of the list of all changers.
   *  Moreover it sets a parent of the given changer to this object.
   *
   *  @param child
   *             a changer to be appended to the list of changers
   *
   *  @throws NullPointerException
   *             if the parameter is null
   */
  public void add(Changer child)
  {
    addChild(child);
  }

  /**
   *
   */
  protected void addChild(GuiObject child)
  {
    if (child == null)
      throw new NullPointerException();
    if (children == null)
      children = new ArrayList<GuiObject>();
    children.add(child);
    child.setParent(this);
  }

  /**
   *  Inserts the given changer at the given position in the list
   *  of changers. Shifts the element currently at that position
   *  and subsequent elements to the right. Sets the parent of the
   *  inserted object to this object.
   *
   *  @param child
   *             a changer object to be inserted
   *
   *  @param index
   *             an index at which the given object is to be inserted
   *
   *  @throws NullPointerException
   *             if the child object is null
   *
   *  @throws IndexOutOfBoundsException
   *             if index is negative number or greater than number
   *             of all the changers.
   */
  public void insert(Changer child, int index)
  {
    insertChild(child, index);
  }

  /**
   *
   */
  protected void insertChild(GuiObject child, int index)
  {
    if (child == null)
      throw new NullPointerException();
    if (children == null && index == 0)
      children = new ArrayList<GuiObject>();
    else
      throw new IndexOutOfBoundsException();
    if (index < 0 || index > children.size())
      throw new IndexOutOfBoundsException();
    children.add(index, child);
    child.setParent(this);
  }

  /**
   *
   */
  public Changer removeChanger(int index)
  {
    return (Changer)removeChild(index);
  }

  /**
   *
   */
  @Override
  public GuiObject removeChild(int index)
  {
    if (children == null)
      throw new IndexOutOfBoundsException();
    return children.remove(index);
  }

  /**
   *
   */
  public Changer getChanger(int index)
  {
    return (Changer)getChild(index);
  }

  /**
   *
   */
  @Override
  public GuiObject getChild(int index)
  {
    if (children == null)
      throw new IndexOutOfBoundsException();
    else
      return children.get(index);
  }

  /**
   *
   */
  @Override
  public int getIndexOfChild(GuiObject child)
  {
    if (child == null)
      throw new IllegalArgumentException();
    if (children == null)
      throw new NoSuchElementException();
    else
      for (int i=0; i<children.size(); i++)
	if (child == children.get(i))
	  return i;
    throw new NoSuchElementException();
  }

  /**
   *  Returns number of all changer children of this object.
   *
   *  @return number of changer childeren
   */
  public int getChangerCount()
  {
    return size();
  }

  /**
   *  Returns number of all the children. It means number of changers
   *  plus number of other children.
   *
   *  @return number of all childeren of this object
   */
  public int size()
  {
    if (children == null)
      return 0;
    else
      return children.size();
  }

  /**
   *
   */
  public abstract JComponent getVisualComponent();

  /**
   *  This method should create instance of appropriate swing
   *  component which will be responsible for painting. Such
   *  swing component must be configured in accordance with
   *  content of properties of this object.
   */
  protected abstract JComponent createVisualComponent();

  /**
   *
   */
  protected abstract void configureVisualComponent();

  /**
   *
   */
  protected abstract void releaseVisualComponent();

  /**
   *  Sets the parent object. This method is called by methods
   *  which adds or removes children.
   *
   *  @param parent
   *             a parent object. It may be null, in such a case,
   *             this object becomes a root of isolated subtree.
   */
  @Override
  protected void setParent(GuiObject parent)
  {
    if (parent == null)
      releaseVisualComponent();
    super.setParent(parent);
  }

  /**
   *  Makes and returns a clone of this object. It creates new 
   *  instance of the same class and than performs copy of all
   *  the properties which are annotated by setter and getter
   *  annotations. Moreover it provides copy of all the children
   *  of this object.
   *
   *  <p>Returned object, is not connected anywhere which means, 
   *  that getParent returns null. The whole subtree doesn't
   *  contain swing components which are responsible for painting.
   *
   *  @param full
   *             if true, it copy even the name property, otherwise
   *             the name will be set to default value
   *
   *  @return a cloned object
   */
  @Override
  public GuiObject clone(boolean full)
  {
    // create new class instance and copy properties
    VisualObject clone = (VisualObject)super.clone(full);
    // create clones of all the children
    if (size() > 0)
    {
      clone.children = new ArrayList<GuiObject>(size());
      for (GuiObject child : children)
	clone.addChild(child.clone(full));
    }
    else
      clone.children = null;
    // return result
    return clone;
  }

  /**
   *  Returns true if and only if it has at least one child.
   */
  @Override
  public boolean hasChildren()
  {
    return children != null && children.size() > 0;
  }

  /**
   *  Returns true.
   */
  @Override
  public boolean isVisual()
  {
    return true;
  }

  @Override
  public boolean isVisualContainer()
  {
    return false;
  }

}
