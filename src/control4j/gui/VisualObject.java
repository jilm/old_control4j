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

import java.util.ArrayList;
import java.util.NoSuchElementException;
import javax.swing.JComponent;

/**
 *
 *  Serves as a facade for objects which are instances of
 *  swing components and a container for properties.
 *
 *  <p>Each instance may contain exactly one swing component
 *  which is responsible for painting. But instance of descendant
 *  of this class may live even without such component. In this
 *  case it serves as a container for visual component settings
 *  or properties. Appropriate swing component is created as
 *  soon as this object is connected as a child to the object which
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
   *
   */
  public static final String LINK_KEY = "facade";

  /**
   *  Swing component that is responsible for painting. This
   *  field may contain null value.
   */
  protected JComponent component;

  /**
   *
   */
  public JComponent getVisualComponent()
  {
    return component;
  }

  /**
   *  Calls createSwingComponent method to create new visual
   *  component. Than sets the link JComponent to this object.
   *  The createSwingComponent method should be called only
   *  through this method.
   *
   *  @see #createSwingComponent
   */
  public final JComponent createVisualComponent()
  {
    component = createSwingComponent();
    component.putClientProperty(LINK_KEY, this);
    return component;
  }

  /**
   *  This method should create instance of appropriate swing
   *  component which will be responsible for painting. This
   *  method should not be called directly, call
   *  createVisualComponent instead.
   *
   *  @see #createVisualComponent
   */
  protected abstract JComponent createSwingComponent();

  /**
   *  This method shoudl configure visual component to be
   *  in accordance with this object settings.
   */
  public abstract void configureVisualComponent();

  /**
   *
   */
  public void releaseVisualComponent()
  {
    component = null;
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
    // TODO:
    // create new class instance and copy properties
    VisualObject clone = (VisualObject)super.clone(full);
    // create clones of all the children
    //if (size() > 0)
    //{
     // clone.children = new ArrayList<GuiObject>(size());
      //for (GuiObject child : children)
	//clone.addChild(child.clone(full));
    //}
    //else
      //clone.children = null;
    // return result
    return clone;
  }

  /**
   *  Returns true.
   */
  @Override
  public boolean isVisual()
  {
    return true;
  }

  /**
   *  Returns false.
   */
  @Override
  public boolean isVisualContainer()
  {
    return false;
  }

  @Override
  public boolean isAssignable(GuiObject object) {
    return !object.isVisual();
  }

}
