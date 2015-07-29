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

import control4j.Signal;
import cz.lidinsky.tools.reflect.Setter;
import cz.lidinsky.tools.reflect.Getter;
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
   *  Index of the signal attached to this changer. It is the
   *  index into the buffer of all signals.
   */
  private int signalIndex;

  /**
   *  Name of the signal that will influence the gui behaviour.
   *  May be null if no signal is assigned.
   */
  private String signalName;

  /**
   *  Name of the parent property which will be changed according
   *  to the state of the signal.
   */
  private String property;
  private Method propertyMethod;

  private VisualObject parent;

  public void setParent(VisualObject parent) {
    this.parent = parent;
  }

  public VisualObject getParent() {
    return parent;
  }

  /**
   *  This method is called regulary by the runtime machine.
   *  As a parameter it gets an array of all the signals.
   *  This method gets only the signal which name correspond
   *  with content of Signal property and calls update.
   *
   *  @see #update(Signal)
   */
  public final void update(Signal[] input)
  {
    if (signalIndex >= 0 && signalIndex < input.length)
    {
      update(input[signalIndex]);
    }
  }

  /**
   *  This method should be overridden and should implement
   *  the functionality of the changer.
   */
  protected abstract void update(Signal input);

  /**
   *
   */
  public abstract Class getPropertyClass();

  /**
   *  @return the name of the signal that will influence the behaviour
   *             of parent gui element. May return null if the signal
   *             was not specified.
   */
  @Getter("Signal")
  public String getSignalName()
  {
    return signalName;
  }

  /**
   *
   */
  @Setter("Signal")
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
  @Getter("Property")
  public String getProperty()
  {
    return property;
  }

  /**
   *
   */
  @Setter("Property")
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

  public boolean isAssignable(GuiObject object) {
    return false;
  }

}
