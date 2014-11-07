package control4j.gui.edit;

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

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Collection;
import javax.swing.JPanel;
import javax.swing.JComponent;
import control4j.scanner.Scanner;
import control4j.scanner.Item;
import control4j.gui.Screens;
import control4j.gui.components.Screen;
import control4j.gui.changers.IChangeable;
import control4j.gui.changers.Changer;

/**
 *
 *  Provides a copy of the given component.
 *
 */
public class Copy
{

  /**
   *
   */
  public static Object copy(Object model, Object parent, int index)
  {
    if (model instanceof Changer && parent instanceof IChangeable)
      return copy((Changer)model, (IChangeable)parent);
    else if (model instanceof JComponent && parent instanceof JPanel)
      return copy((JComponent)model, (JPanel)parent, index);
    else if (model instanceof Screen && parent instanceof Screens)
      return copy((Screen)model, (Screens)parent, index);
    else
      assert false;
    return null;
  }

  /**
   *
   */
  private static Changer copy(Changer model, IChangeable parent)
  {
    try
    {
      Changer clone = (Changer)model.clone();
      Editor.getTreeModel().addChild(parent, clone);
      return clone;
    }
    catch (CloneNotSupportedException e)
    {
      assert false; // should not happen becouse Changer is cloneable
    }
    return null;
  }

  /**
   *
   */
  private static JComponent copy(JComponent model, JPanel parent, int index)
  {
    // make a copy instance of the child
    JComponent copy = (JComponent)createCopyInstance(model);
    // add it to the new parent
    if (index < 0)
      Editor.getTreeModel().addChild(parent, copy);
    else
      Editor.getTreeModel().addChild(parent, copy, index);
    // copy properties
    copyProperties(model, copy);
    // make copy of all the children of the child
    for (int i=0; i<model.getComponentCount(); i++)
    {
      JComponent grandchild = (JComponent)model.getComponent(i);
      copy(grandchild, copy, -1);
    }
    // make copy of all the changeables !
    if (model instanceof IChangeable)
    {
      IChangeable changeableParent = (IChangeable)model;
      for (int i=0; i<changeableParent.getChangerCount(); i++)
	copy(changeableParent.getChanger(i), (IChangeable)copy);
    }
    return copy;
  }

  /**
   *
   */
  private static JComponent copy(Screen model, Screens parent, int index)
  {
    // make a copy instance of the child
    Screen copy = (Screen)createCopyInstance(model);
    // add it to the new parent
    if (index < 0)
      Editor.getTreeModel().addScreen(copy, model.getTitle());
    else
      Editor.getTreeModel().addScreen(copy, model.getTitle(), index);
    // copy properties
    copyProperties(model, copy);
    // make copy of all the children of the child
    for (int i=0; i<model.getComponentCount(); i++)
    {
      JComponent grandchild = (JComponent)model.getComponent(i);
      copy(grandchild, copy, -1);
    }
    // make copy of all the changeables !
    if (model instanceof IChangeable)
    {
      IChangeable changeableParent = (IChangeable)model;
      for (int i=0; i<changeableParent.getChangerCount(); i++)
	copy(changeableParent.getChanger(i), (IChangeable)copy);
    }
    return copy;
  }

  /**
   *
   */
  private static void copyProperties(Object model, Object copy)
  {
    try
    {
      // make a copy of all the methods annotated as setters and getters
      Scanner scanner = new Scanner();
      Map<String, Item> modelScan = scanner.scanObject(model);
      Map<String, Item> copyScan = scanner.scanObject(copy);
      Collection<Item> copyItems = copyScan.values();
      for (Item copyItem : copyItems)
	if (copyItem.isWritable())
	{
	  Object value = modelScan.get(copyItem.getKey()).getValue();
	  System.out.println(copyItem.getKey() + " : " + value);
	  System.out.println(value.getClass().getName());
	  copyItem.setValue(value);
	}
    }
    catch (java.lang.reflect.InvocationTargetException e)
    {
      System.err.println(e.getClass().toString() + " : " + e.getMessage());
      java.lang.Throwable cause = e.getCause();
      System.err.println(cause.getClass().toString() + " : " + cause.getMessage());
    }
  }

  /**
   *
   */
  private static Object createCopyInstance(Object model)
  {
    try
    {
      // create an object of the same class
      Class _class = model.getClass();
      System.out.println(_class.getName());
      Constructor constructor = _class.getConstructor();
      Object copy = constructor.newInstance();
      return copy;
    }
    catch (NoSuchMethodException e)
    {
      System.err.println(e.getClass().toString() + " : " + e.getMessage());
    }
    catch (InstantiationException e)
    {
      // this should not happen
      System.err.println(e.getClass().toString() + " : " + e.getMessage());
      assert false;
    }
    catch (IllegalAccessException e)
    {
      System.err.println(e.getClass().toString() + " : " + e.getMessage());
    }
    catch (java.lang.reflect.InvocationTargetException e)
    {
      System.err.println(e.getClass().toString() + " : " + e.getMessage());
      java.lang.Throwable cause = e.getCause();
      System.err.println(cause.getClass().toString() + " : " 
	+ cause.getMessage());
    }
    return null;
  }
}
