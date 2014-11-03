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

import control4j.scanner.Getter;
import control4j.scanner.Setter;
import control4j.ConfigItem;
import control4j.Signal;
import control4j.SignalFormat;
import control4j.modules.IGuiUpdateListener;
import control4j.tools.Preferences;
import control4j.gui.ColorParser;
import control4j.gui.SignalAssignment;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.AbstractButton;
import java.awt.Point;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

/**
 *  Rectangle element.
 */
public class Rectangle extends ChangeableComponent
{

  private int width = 20;
  private int height = 20;

  private boolean hasBorder = false;
  private Color borderColor = Color.BLACK;
  private int borderThickness = 1;

  @Override
  @Getter(key="X") 
  public int getX()
  {
    return super.getX();
  }

  @Setter(key="X")
  public void setX(int x)
  {
    Point location = getLocation();
    location.x = x;
    setLocation(location);
  }

  @Override
  @Getter(key="Y")
  public int getY()
  {
    return super.getY();
  }

  @Setter(key="Y")
  public void setY(int y)
  {
    Point location = getLocation();
    location.y = y;
    setLocation(location);
  }
  
  @Getter(key="Width")
  public int getWidth()
  {
    return width;
  }

  @Setter(key="Width")
  public void setWidth(int width)
  {
    this.width = width;
    revalidate();
    repaint();
  }

  @Getter(key="Height")
  public int getHeight()
  {
    return height;
  }

  @Setter(key="Height")
  public void setHeight(int height)
  {
    this.height = height;
    revalidate();
    repaint();
  }

  @Getter(key="Foreground Color") @Override
  public Color getForeground()
  {
    return super.getForeground();
  }

  @Setter(key="Foreground Color") @Override
  public void setForeground(Color color)
  {
    super.setForeground(color);
  }

  @Getter(key="Border")
  public boolean hasBorder()
  {
    return hasBorder;
  }

  @Setter(key="Border")
  public void setBorder(boolean border)
  {
    this.hasBorder = border;
    repaint();
  }

  @Getter(key="Border Color")
  public Color getBorderColor()
  {
    return borderColor;
  }

  @Setter(key="Border Color")
  public void setBorderColor(Color color)
  {
    borderColor = color;
    repaint();
  }

  @Getter(key="Border Thickness")
  public int getBorderThickness()
  {
    return borderThickness;
  }

  @Setter(key="Border Thickness")
  public void setBorderThickness(int thickness)
  {
    borderThickness = thickness;
    repaint();
  }

  @Override
  public Dimension getPreferredSize()
  {
    return new Dimension(width, height);
  }

  @Override
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    if (hasBorder)
    {
      // paint a border rectangle
      g.setColor(borderColor);
      g.fillRect(0, 0, width, height);
      // paint the interior of the rectangle
      g.setColor(getForeground());
      g.fillRect(borderThickness, borderThickness, width-2*borderThickness, height-2*borderThickness);
    }
    else
    {
      // paint the rectangle
      g.fillRect(0, 0, width, height);
    }
  }

}
