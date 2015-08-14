package control4j.gui.components;

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

import control4j.gui.VisualContainer;
import control4j.gui.layouts.VDUGridLayout;

import cz.lidinsky.tools.reflect.Getter;
import cz.lidinsky.tools.reflect.Setter;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 *  Panel that uses Flow layout.
 *
 */
@control4j.annotations.AGuiObject(name="Box", tags={"box layout"})
public class Box extends VisualContainer implements ComponentListener
{

  /**
   *
   */
  private int alignmentPoint = 0;
  private int x;
  private int y;
  private boolean isLineAxis = true;
  private int space = 0;

  /**
   *
   */
  @Setter("Alignment Point")
  public void setAlignmentPoint(int index)
  {
    alignmentPoint = index;
  }

  /**
   *
   */
  @Getter("Alignment Point")
  public int getAlignmentPoint()
  {
    return alignmentPoint;
  }

  @Getter("X")
  public int getX()
  {
    return x;
  }

  @Setter("X")
  public void setX(int x)
  {
    this.x = x;
    if (component != null)
    {
      JComponent parent = (JComponent)component.getParent();
      Insets insets = parent.getInsets();
      component.setLocation(x + insets.left, y + insets.top);
    }
  }

  @Getter("Y")
  public int getY()
  {
    return y;
  }

  @Setter("Y")
  public void setY(int y)
  {
    this.y = y;
    if (component != null)
    {
      JComponent parent = (JComponent)component.getParent();
      Insets insets = parent.getInsets();
      component.setLocation(x + insets.left, y + insets.top);
    }
  }

  @Getter("Line Axis")
  public boolean isLineAxis()
  {
    return isLineAxis;
  }

  @Setter("Line Axis")
  public void setAxis(boolean isLineAxis)
  {
    if (component != null && this.isLineAxis != isLineAxis)
    {
      //int axis = isLineAxis ? BoxLayout.LINE_AXIS : BoxLayout.PAGE_AXIS;
      //BoxLayout layout = new BoxLayout(component, axis);
      //component.setLayout(layout);
      component.revalidate();
      component.repaint();
    }
    this.isLineAxis = isLineAxis;
  }

  @Getter("Space")
  public int getSpace()
  {
    return space;
  }

  @Setter("Space")
  public void setSpace(int space)
  {
    this.space = space;

  }

  /**
   *
   */
  @Override
  protected JComponent createSwingComponent()
  {
    return new JPanel();
  }

  /**
   *
   */
  @Override
  public void configureVisualComponent()
  {
    JComponent parent = (JComponent)component.getParent();
    parent.addComponentListener(this);
    Insets insets = parent.getInsets();
    component.setLocation(x + insets.left, y + insets.top);
    //int axis = isLineAxis ? BoxLayout.LINE_AXIS : BoxLayout.PAGE_AXIS;
    //BoxLayout layout = new BoxLayout(component, axis);
    component.setLayout(new VDUGridLayout());
    super.configureVisualComponent();
    component.revalidate();
    component.repaint();
  }

  public void componentHidden(ComponentEvent event) {
  }

  public void componentMoved(ComponentEvent event) {
  }

  public void componentResized(ComponentEvent event) {
    JComponent parent = (JComponent)component.getParent();
  }

  public void componentShown(ComponentEvent event) {
  }

}
