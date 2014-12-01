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
import javax.swing.JComponent;

/**
 *
 */
@control4j.annotations.AGuiObject(name="Triangle", tags={"basic shape"})
public class Triangle extends VisualObjectBase
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

  /**
   *
   */
  public Triangle()
  {
    super();
  }

  /**
   *
   */
  @Getter(key="Size")
  public double getTriangleSize()
  {
    return size;
  }

  /**
   *
   */
  @Setter(key="Size")
  public void setTriangleSize(double size)
  {
    this.size = size;
    calculatePoints();
    if (component != null)
    {
      component.setPreferredSize(new Dimension(width, width));
      component.setBounds(getX(), getY(), width, width);
      component.revalidate();
      component.repaint();
    }
  }

  /**
   *
   */
  @Getter(key="Rotation")
  public double getRotation()
  {
    return rotation / Math.PI * 180.0;
  }

  /**
   *
   */
  @Setter(key="Rotation")
  public void setRotation(double rotation)
  {
    this.rotation = rotation / 180.0 * Math.PI;
    calculatePoints();
    if (component != null)
    {
      component.setPreferredSize(new Dimension(width, width));
      component.setBounds(getX(), getY(), width, width);
      component.revalidate();
      component.repaint();
    }
  }

  /**
   *
   */
  @Override
  protected JComponent createSwingComponent()
  {
    return new TrianglePainter();
  }

  /**
   *
   */
  @Override
  protected void configureVisualComponent()
  {
    super.configureVisualComponent();
    calculatePoints();
    component.setPreferredSize(new Dimension(width, width));
    component.setBounds(getX(), getY(), width, width);
    component.revalidate();
    component.repaint();
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

  /**
   *
   */
  private class TrianglePainter extends JComponent
  {

    /**
     *
     */
    @Override
    protected void paintComponent(Graphics g)
    {
      g.fillPolygon(xPoints, yPoints, 3);
    }

  }

}
