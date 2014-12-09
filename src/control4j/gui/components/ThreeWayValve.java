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
import control4j.scanner.Getter;
import control4j.scanner.Setter;
import control4j.tools.Preferences;

/**
 *
 *  Paints a Tree-way valve
 *
 */
@AGuiObject(name="Three-way valve", tags={"heating"})
public class ThreeWayValve extends VisualObjectBase
{

  private int size = 20;
  private boolean fill = false;
  private double rotation = Math.PI;

  @Getter(key="Size")
  public int getSize()
  {
    return this.size;
  }

  @Setter(key="Size")
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

  @Getter(key="Fill")
  public boolean getFill()
  {
    return fill;
  }

  @Setter(key="Fill")
  public void setFill(boolean fill)
  {
    this.fill = fill;
    if (component != null)
      component.repaint();
  }

  @Getter(key="Rotation")
  public double getRotation()
  {
    return rotation / Math.PI * 180d;
  }

  @Setter(key="Rotation")
  public void setRotation(double rotation)
  {
    this.rotation = rotation / 180d * Math.PI;
    if (component != null)
      component.repaint();
  }

  @Override
  protected JComponent createSwingComponent()
  {
    return new ValvePainter();
  }

  @Override
  protected void configureVisualComponent()
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
  private class ValvePainter extends JComponent
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
      float v = size * 3f/8f;
      float a = v / (float)Math.cos(Math.PI / 6f);
      int x = Math.round(0.5f * size - v);
      // paint three triangles
      int[] xPoints = new int[3];
      xPoints[0] = x;
      xPoints[1] = x;
      xPoints[2] = size / 2;
      int[] yPoints = new int[3];
      yPoints[0] = Math.round(0.5f * (size - a));
      yPoints[1] = size - yPoints[0];
      yPoints[2] = size / 2;
      for (int i=0; i<=2; i++)
      {
        g2.rotate(i * 0.5d * Math.PI, size/2, size/2);
        if (fill)
          g.fillPolygon(xPoints, yPoints, 3);
        else
          g.drawPolygon(xPoints, yPoints, 3);
        // paint connector lines
        g.drawLine(0, size/2, x, size/2);
      }
    }

  }

}
