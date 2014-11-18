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

import java.util.Map;
import java.util.LinkedList;
import control4j.scanner.Setter;
import control4j.scanner.Getter;
import control4j.scanner.Item2;
import control4j.scanner.Scanner;

/**
 *
 *  Common predecessor of gui components, panels and changers
 *
 *  <p>Each object has a name property which serves mainly
 *  for reference and navigation purposes. This name should
 *  be unique but it is not neccessary for function.
 *
 *  <p>Objects are organized in the tree structure. It means,
 *  that each object has one parent; excluding the root of
 *  course; and that objects may have zero or more children.
 *  This class provides methods to navigate through the tree.
 *  
 *  <p>This object also provides mechanism to inform subscribers
 *  about a change of some property. This object declares property 
 *  called Name. Descendants may declare another. 
 *
 */
public abstract class GuiObject
{

  /**
   *  Name of the component. May contain null value.
   */
  private String name;

  /**
   *  Parent of the component, may contain null value.
   */
  private GuiObject parent;

  /**
   *  Number of all instances that were created during the application
   *  run. This number is used for default name creation.
   */
  private static int counter;

  /**
   *  Serial number of object instance. It is used for default name
   *  creation.
   */
  private int number = ++counter;

  /**
   *  Listeners that will be notified about a change of property value.
   */
  private LinkedList<IChangeListener> changeListeners;

  /**
   *  Returns a name property of this object. If this property
   *  has not been set, or has been set to null value or empty
   *  string, it returns default name which consists of class
   *  name and an order number of this object.
   *
   *  @return a name of this object. It never returns a null value.
   *
   *  @see #setName
   */
  @Getter(key="Name")
  public String getName()
  {
    if (name != null && name.length() > 0)
      return name;
    else
      return getClass().getSimpleName() + Integer.toString(number);
  }

  /**
   *  Sets the name property of this object. The parameter is trimed
   *  before it is stored into internal structures of object.
   *
   *  @param name
   *             required name of the object. May be null.
   *
   *  @see #getName
   */
  @Setter(key="Name")
  public void setName(String name)
  {
    if (name != null)
      this.name = name.trim();
    else
      this.name = name;
    //
    fireChangeEvent(new ChangeEvent(this, "Name", getName()));
  }

  /**
   *  Returns a parent object. If this object is the root of the
   *  tree or temporary subtree, it returns null value.
   *
   *  @return a parent object, may return a null value
   *
   *  @see #setParent
   */
  public GuiObject getParent()
  {
    return parent;
  }

  /**
   *  Sets a parent of this object. This method shoud be called
   *  by insert or add method of this object descendats.
   *
   *  @param parent
   *             a new parent of this object. May be null.
   *
   *  @see #getParent
   */
  protected void setParent(GuiObject parent)
  {
    this.parent = parent;
  }

  /**
   *  Creates a new object of the same class and makes a copy of all
   *  the properties which are annotated by Setter and Getter
   *  annotations. This method doesn't copy parent which is set to
   *  null value. Moreover you can specify not to copy name and
   *  number properties.
   *  
   *  @param full
   *             if false, it doesn't copy name and number property.
   *
   *  @return a cloned object
   *
   *  @throws ExceptionInInitializerError
   *
   *  @throws SecurityException
   *
   *  @see java.lang.Class#newInstance
   *  @see control4j.scanner.Getter
   *  @see control4j.scanner.Setter
   */
  public GuiObject clone(boolean full)
  {
    try
    {
      // create a new object of the same class
      Class<? extends GuiObject> _class = getClass();
      GuiObject clone = _class.newInstance();
      // copy all of the getters
      Map<String, Item2> properties = Scanner.scanClass(_class);
      for (Item2 property : properties.values())
	if (property.isReadable() && property.isWritable())
	  property.setValue(clone, property.getValue(this));
      clone.parent = null;
      // if full copy is not required
      if (full)
	clone.number = this.number;
      else
	clone.name = null;
      // return result
      return clone;
    }
    catch (IllegalAccessException e)
    {
    }
    catch (InstantiationException e)
    {
    }
    catch (java.lang.reflect.InvocationTargetException e)
    {
    }
    return null;
    
  }

  /**
   *  Adds a listener which will be notified about change of some property
   *  of this object.
   *
   *  @param listener
   *             object that will be notified about property change
   *
   *  @throws NullPointerException
   *             if listener contains a null value
   */
  public void addChangeListener(IChangeListener listener)
  {
    if (listener == null)
      throw new NullPointerException();
    if (changeListeners == null)
      changeListeners = new LinkedList<IChangeListener>();
    changeListeners.add(listener);
  }

  /**
   *
   */
  public void removeChangeListener(IChangeListener listener)
  {
    if (listener == null)
      throw new NullPointerException();
    if (changeListeners == null)
      return;
    changeListeners.remove(listener);
  }

  /**
   *  Should be called by property set methods whenever value
   *  of a property has changed. This method notifies all of
   *  the listeners.
   *
   *  @param e
   *            more info about the change
   */
  protected void fireChangeEvent(ChangeEvent e)
  {
    if (changeListeners == null) 
      return;
    for (IChangeListener listener : changeListeners)
      listener.propertyChanged(e);
  }

  /**
   *  Returns true if and only if the object has at least one child.
   *  Otherwise it returns false.
   */
  public abstract boolean hasChildren();

  /**
   *  Returns true if and only if the object has visual representation.
   *  In other words it returns true, if the object is descendant
   *  of VisualObject.
   */
  public abstract boolean isVisual();

  /**
   *  Returns true if this object is descendant of VisualContainer
   *  object, otherwise, it returns false.
   */
  public abstract boolean isVisualContainer();

  /**
   *  Returns a child with given index.
   *
   *  @param index
   *
   *  @return a child object at given index
   *
   *  @throws IndexOutOfBoundsException
   */
  public abstract GuiObject getChild(int index);

  /**
   *  Returns number of all children.
   */
  public abstract int size();

  /**
   *  Removes a child object at given index.
   *
   *  @param index
   *
   *  @return removed child
   *
   *  @throws IndexOutOfBoundsException
   */
  public abstract GuiObject removeChild(int index);

  /**
   *  Returns index of given child object.
   *
   *  @param child
   *             a child object 
   *
   *  @return index of the given child object
   *
   *  @throws NoSuchElementException
   */
  public abstract int getIndexOfChild(GuiObject child);

}
