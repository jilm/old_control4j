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

import cz.lidinsky.tools.reflect.Getter;
import cz.lidinsky.tools.reflect.Setter;
import control4j.tools.Preferences;
import control4j.gui.ColorParser;
import control4j.gui.VisualObject;
import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import control4j.annotations.AGuiObject;

/**
 *
 *  Paints circle
 *
 */
@AGuiObject(name="Circle", tags={"basic shape"})
public class Circle extends VisualObjectBase
{

  private int size = 20;
  private boolean fill = false;

  /**
   *
   */
  public Circle()
  {
    super();
  }

  @Getter("Size")
  public int getSize()
  {
    return this.size;
  }

  @Setter("Size")
  public void setSize(int size)
  {
    this.size = size;
    if (component != null)
    {
      component.setSize(size, size);
      component.setPreferredSize(new Dimension(size, size));
      component.setMaximumSize(new Dimension(size, size));
      component.setMinimumSize(new Dimension(size, size));
    }
  }

  @Getter("Fill")
  public boolean getFill()
  {
    return fill;
  }

  @Setter("Fill")
  public void setFill(boolean fill)
  {
    this.fill = fill;
    if (component != null) component.repaint();
  }

  @Override
  protected JComponent createSwingComponent()
  {
    return new CirclePainter();
  }

  @Override
  public void configureVisualComponent()
  {
    super.configureVisualComponent();
    component.setSize(size, size);
    component.setPreferredSize(new Dimension(size, size));
    component.setMaximumSize(new Dimension(size, size));
    component.setMinimumSize(new Dimension(size, size));
    component.revalidate();
    component.repaint();
  }

  /**
   *  Painter class
   */
  private class CirclePainter extends JComponent
  {

    /**
     *
     */
    @Override
    public void paintComponent(Graphics g)
    {
      super.paintComponent(g);
      // paint an indicator
      if (fill)
        g.fillOval(0, 0, size-1, size-1);
      else
	g.drawOval(0, 0, size-1, size-1);
    }

  }

}
