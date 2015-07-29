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
 *  Paints a pump.
 *
 */
@AGuiObject(name="Pump", tags={"heating"})
public class Pump extends VisualObjectBase
{

  private int size = 20;
  private boolean fill = false;
  private double rotation = Math.PI;

  /**
   *
   */
  public Pump()
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
    if (component != null)
      component.repaint();
  }

  @Getter("Rotation")
  public double getRotation()
  {
    return rotation / Math.PI * 180d;
  }

  @Setter("Rotation")
  public void setRotation(double rotation)
  {
    this.rotation = rotation / 180d * Math.PI;
    if (component != null)
      component.repaint();
  }

  @Override
  protected JComponent createSwingComponent()
  {
    return new PumpPainter();
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
  private class PumpPainter extends JComponent
  {

    /**
     *
     */
    @Override
    public void paintComponent(Graphics g)
    {
      super.paintComponent(g);
      // set rotation
      Graphics2D g2 = (Graphics2D)g;
      g2.rotate(rotation, size/2, size/2);
      // paint circle
      float circleSize = size * 3f/4f;
      float radius = circleSize * 0.5f;
      int x = Math.round((size - circleSize) * 0.5f);
      g.drawOval(x, x, Math.round(circleSize), Math.round(circleSize));
      // paint triangle inside the circle
      float a = radius * 2f * (float)Math.sin(Math.PI / 3f);
      float v = a * (float)Math.cos(Math.PI / 6f);
      int[] xPoints = new int[3];
      xPoints[0] = size - x;
      xPoints[1] = Math.round(xPoints[0] - v);
      xPoints[2] = xPoints[1];
      int[] yPoints = new int[3];
      yPoints[0] = size / 2;
      yPoints[1] = Math.round(yPoints[0] - 0.5f * a);
      yPoints[2] = Math.round(yPoints[0] + 0.5f * a);
      if (fill)
        g.fillPolygon(xPoints, yPoints, 3);
      else
        g.drawPolygon(xPoints, yPoints, 3);
      // paint connector lines
      g.drawLine(0, size/2, x, size/2);
      g.drawLine(size-x, size/2, size, size/2);
    }

  }

}
