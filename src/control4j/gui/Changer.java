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

import control4j.Signal;
import control4j.scanner.Setter;
import control4j.scanner.Getter;
import control4j.scanner.Scanner;
import java.lang.reflect.Method;

/**
 *
 *  Change some property of the parent gui element according to state
 *  or value of some signal. Each changer has attached one signal and
 *  one property of one gui element to modify.
 *
 */
public abstract class Changer<T> extends GuiObject
{

  /**
   *
   */
  private int signalIndex;

  /** 
   *  Name of the signal that will influence the gui behaviour.
   *  May be null if no signal is assigned.
   */
  private String signalName;

  /**
   *
   */
  private String property;
  private Method propertyMethod;

  /**
   *  This method is called regulary by the runtime machine.
   *  As a parameter it gets all of the signals. Than this
   *  method calls method update with the only chosen signal.
   */
  public void update(Signal[] input)
  {
    if (signalIndex >= 0 && signalIndex < input.length)
    {
      update(input[signalIndex]);
    }
  }

  /**
   *
   */
  protected abstract void update(Signal input);

  /**
   *
   */
  public abstract Class getPropertyClass();

  /**
   *  @return the name of the signal that will influence the behaviour
   *         of parent gui element. May return null if the signal was
   *         not specified.
   */
  @Getter(key="Signal")
  public String getSignalName()
  {
    return signalName;
  }

  /**
   *
   */
  @Setter(key="Signal")
  public void setSignalName(String signal)
  {
    this.signalName = signal;
  }

  /**
   *
   */
  public void setSignalIndex(int index)
  {
    this.signalIndex = index;
  }

  /**
   *
   */
  protected int getSignalIndex()
  {
    return signalIndex;
  }

  /**
   *
   */
  @Getter(key="Property")
  public String getProperty()
  {
    return property;
  }

  /**
   *
   */
  @Setter(key="Property")
  public void setProperty(String property)
  {
    this.property = property;
  }

  /**
   *
   */
  protected Method getPropertyMethod()
  {
    if (propertyMethod == null)
      propertyMethod = Scanner.getSetter(getParent(), property);
    return propertyMethod;
  }

  /**
   *  Sets the property value of the parent.
   */
  protected void setPropertyValue(T value)
  {
    Method propertyMethod = getPropertyMethod();
    if (propertyMethod != null)
      try
      {
        propertyMethod.invoke(getParent(), value);
      }
      catch (IllegalAccessException e)
      {
      }
      catch (java.lang.reflect.InvocationTargetException e)
      {
      }
  }

  /**
   *  Changers doesn't have any children, this method always returns
   *  false.
   */
  @Override
  public boolean hasChildren()
  {
    return false;
  }

  /**
   *  Returns false.
   */
  @Override
  public boolean isVisual()
  {
    return false;
  }

  @Override
  public boolean isVisualContainer()
  {
    return false;
  }

  @Override
  public GuiObject getChild(int index)
  {
    throw new IndexOutOfBoundsException();
  }

  @Override
  public int size()
  {
    return 0;
  }

  @Override
  public GuiObject removeChild(int index)
  {
    throw new java.util.NoSuchElementException();
  }

  @Override
  public int getIndexOfChild(GuiObject child)
  {
    throw new java.util.NoSuchElementException();
  }

}
