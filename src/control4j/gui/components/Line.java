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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JComponent;
import control4j.annotations.AGuiObject;
import control4j.gui.ColorParser;
import control4j.gui.VisualObject;
import cz.lidinsky.tools.reflect.Getter;
import cz.lidinsky.tools.reflect.Setter;
import control4j.tools.Preferences;

/**
 *
 *  Paints a line.
 *
 */
@AGuiObject(name="Line", tags={"basic shape"})
public class Line extends VisualObject
{

  private int x1;
  private int y1;
  private int x2;
  private int y2;
  private Color foreground;

  @Getter("X1")
  public int getX1()
  {
    return x1;
  }

  @Setter("X1")
  public void setX1(int x1)
  {
    this.x1 = x1;
    if (component != null)
    {
      setSize();
      component.revalidate();
      component.repaint();
    }
  }

  @Getter("Y1")
  public int getY1()
  {
    return y1;
  }

  @Setter("Y1")
  public void setY1(int y1)
  {
    this.y1 = y1;
    if (component != null)
    {
      setSize();
      component.revalidate();
      component.repaint();
    }
  }

  @Getter("X2")
  public int getX2()
  {
    return x2;
  }

  @Setter("X2")
  public void setX2(int x2)
  {
    this.x2 = x2;
    if (component != null)
    {
      setSize();
      component.revalidate();
      component.repaint();
    }
  }

  @Getter("Y2")
  public int getY2()
  {
    return y2;
  }

  @Setter("Y2")
  public void setY2(int y2)
  {
    this.y2 = y2;
    if (component != null)
    {
      setSize();
      component.revalidate();
      component.repaint();
    }
  }

  @Getter("Foreground Color")
  public Color getForeground()
  {
    return foreground;
  }

  @Setter("Foreground Color")
  public void setForeground(Color color)
  {
    foreground = color;
    if (component != null)
    {
      component.setForeground(color);
      component.repaint();
    }
  }

  private void setSize()
  {
    if (component != null)
    {
      int x = Math.min(x1, x2);
      int y = Math.min(y1, y2);
      int width = Math.abs(x1 - x2);
      if (width == 0)
      {
	width = 3;
	x -= 1;
      }
      int height = Math.abs(y1 - y2);
      if (height == 0)
      {
	height = 3;
	y -= 1;
      }
      component.setLocation(x, y);
      component.setSize(width, height);
      component.setPreferredSize(new Dimension(width, height));
      component.setMaximumSize(new Dimension(width, height));
      component.setMinimumSize(new Dimension(width, height));
    }
  }

  @Override
  protected JComponent createSwingComponent()
  {
    return new Painter();
  }

  @Override
  public void configureVisualComponent()
  {
    setSize();
    component.setForeground(foreground);
    component.revalidate();
    component.repaint();
  }

  /**
   *  Painter class
   */
  private class Painter extends JComponent
  {

    /**
     *
     */
    @Override
    public void paintComponent(Graphics g)
    {
      super.paintComponent(g);
      int x = Math.min(x1, x2);
      int y = Math.min(y1, y2);
      int width = Math.abs(x1 - x2);
      if (width == 0) x--;
      int height = Math.abs(y1 - y2);
      if (height == 0) y--;
      g.drawLine(x1-x, y1-y, x2-x, y2-y);
    }

  }

}
