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
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;

/**
 *
 */
public class Triangle extends ChangeableComponent
{

  /**
   *  Delka strany trojuhelnika
   */
  private double size = 20.0;

  /**
   *  Rotace trojuhelnika v radianech
   */
  private double rotation = 0.0;

  private int[] xPoints = new int[3];
  private int[] yPoints = new int[3];
  private int width;

  public Triangle()
  {
    super();
    calculatePoints();
  }

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

  @Getter(key="Size")
  public double getTriangleSize()
  {
    return size;
  }

  @Setter(key="Size")
  public void setTriangleSize(double size)
  {
    this.size = size;
    calculatePoints();
    revalidate();
    repaint();
  }

  @Getter(key="Rotation")
  public double getRotation()
  {
    return rotation / Math.PI * 180.0;
  }

  @Setter(key="Rotation")
  public void setRotation(double rotation)
  {
    this.rotation = rotation / 180.0 * Math.PI;
    calculatePoints();
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

  @Override
  public Dimension getPreferredSize()
  {
    return new Dimension(width, width);
  }

  @Override
  public void paintComponent(Graphics g)
  {
    g.fillPolygon(xPoints, yPoints, 3);
  }

  /**
   *
   */
  private void calculatePoints()
  {
    double v = size / Math.sqrt(3);
    double x = v * Math.sin(rotation) + v;
    double y = v * Math.cos(rotation) + v;
    xPoints[0] = (int)Math.round(x);
    yPoints[0] = (int)Math.round(y);
    x = v * Math.sin(rotation + Math.PI * 2.0 / 3.0) + v;
    y = v * Math.cos(rotation + Math.PI * 2.0 / 3.0) + v;
    xPoints[2] = (int)Math.round(x);
    yPoints[2] = (int)Math.round(y);
    x = v * Math.sin(rotation - Math.PI * 2.0 / 3.0) + v;
    y = v * Math.cos(rotation - Math.PI * 2.0 / 3.0) + v;
    xPoints[1] = (int)Math.round(x);
    yPoints[1] = (int)Math.round(y);
    width = (int)Math.round(2.0 * v);
  }

  @Override
  public Object clone() throws CloneNotSupportedException
  {
    Triangle clone = (Triangle)super.clone();
    clone.xPoints = new int[3];
    clone.yPoints = new int[3];
    return clone;
  }

}
