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
 *  Paints a valve
 *
 */
@AGuiObject(name="Valve", tags={"heating"})
public class Valve extends VisualObjectBase
{

  private int size = 20;
  private boolean fill = false;
  private double rotation = 0d;

  /** Valid values are: 2-5 */
  private int type = 5;

  /** Actuator type, valid values are: 0-2 */
  private int actuator = 0;

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

  @Getter(key="Type")
  public int getType()
  {
    return type;
  }

  @Setter(key="Type")
  public void setType(int type)
  {
    this.type = type;
    if (component != null)
      component.repaint();
  }

  @Getter(key="Actuator")
  public int getActuator()
  {
    return actuator;
  }

  @Setter(key="Actuator")
  public void setActuator(int actuator)
  {
    this.actuator = actuator;
    if (component != null) component.repaint();
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
      // calculate dimensions
      float v = size * 3f/8f;
      float a = v / (float)Math.cos(Math.PI / 6f);
      int x = Math.round(0.5f * size - v);
      // paint triangles
      int[] xPoints = new int[3];
      xPoints[0] = x;
      xPoints[1] = x;
      xPoints[2] = size / 2;
      int[] yPoints = new int[3];
      yPoints[0] = Math.round(0.5f * (size - a));
      yPoints[1] = size - yPoints[0];
      yPoints[2] = size / 2;
      if (type >= 2 && type <= 4)
      {
        for (int i=0; i<type; i++)
        {
          if (fill)
            g.fillPolygon(xPoints, yPoints, 3);
          else
            g.drawPolygon(xPoints, yPoints, 3);
          // paint connector lines
          g.drawLine(0, size/2, x, size/2);
          g2.rotate(Math.PI * 0.5d, size/2, size/2);
        }
      }
      else if (type == 5)
      {
        for (int i=0; i<2; i++)
        {
          if (fill)
            g.fillPolygon(xPoints, yPoints, 3);
          else
            g.drawPolygon(xPoints, yPoints, 3);
          // paint connector lines
          g.drawLine(0, size/2, x, size/2);
          g2.rotate(Math.PI, size/2, size/2);
        }
        g2.rotate(Math.PI * 0.5d, size/2, size/2);
      }
      // paint actuator
      if (actuator == 1) // solenoid
      {
        int width = Math.round((float)size * 3f/8f);
        int height = Math.round((float)size / 4f);
        if (fill)
	  g2.fillRect(1, (size-width)/2, height, width);
        else
	  g2.drawRect(1, (size-width)/2, height, width);
        g2.drawLine(height+1, size/2, size/2, size/2);
      }
      else if (actuator == 2) // motor
      {
        int diameter = Math.round((float)size * 2f/6f);
        if (fill)
          g2.fillOval(0, (size-diameter)/2, diameter, diameter);
        else
          g2.drawOval(0, (size-diameter)/2, diameter, diameter);
        g2.drawLine(diameter, size/2, size/2, size/2);
      }
    }

  }

}
