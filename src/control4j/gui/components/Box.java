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

import java.awt.Color;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.FlowLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import control4j.scanner.Setter;
import control4j.scanner.Getter;
import control4j.gui.VisualContainer;

/**
 *
 *  Panel that uses Flow layout.
 *
 */
public class Box extends VisualContainer
{

  /**
   *
   */
  private int alignmentPoint = 0;
  private int x;
  private int y;

  /**
   *
   */
  @Setter(key="Alignment Point")
  public void setAlignmentPoint(int index)
  {
    alignmentPoint = index;
  }

  /**
   *
   */
  @Getter(key="Alignment Point")
  public int getAlignmentPoint()
  {
    return alignmentPoint;
  }

  @Getter(key="X")
  public int getX()
  {
    return x;
  }

  @Setter(key="X")
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

  @Getter(key="Y")
  public int getY()
  {
    return y;
  }

  @Setter(key="Y")
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

  /**
   *
   */
  @Override
  protected JComponent createSwingComponent()
  {
    return new JPanel(new FlowLayout());
  }

  /**
   *
   */
  @Override
  protected void configureVisualComponent()
  {
    JComponent parent = (JComponent)component.getParent();
    Insets insets = parent.getInsets();
    component.setLocation(x + insets.left, y + insets.top);
    super.configureVisualComponent();
    component.revalidate();
    component.repaint();
  }

}
