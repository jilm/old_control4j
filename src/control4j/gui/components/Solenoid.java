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
 *  Paints a solenoid actuator for valve
 *
 */
@AGuiObject(name="Solenoid actuator", tags={"heating", "valve"})
public class Solenoid extends VisualObjectBase
{

  private int size = 20;
  private boolean fill = false;
  private double rotation = 0d;

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
    return new Painter();
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
  private class Painter extends JComponent
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
      int width = Math.round((float)size * 3f/8f);
      int height = Math.round((float)size / 4f);
      if (fill)
	g2.fillRect((size-width)/2, 0, width, height);
      else
	g2.drawRect((size-width)/2, 0, width, height);
      g2.drawLine(size/2, height, size/2, size/2);
    }

  }

}
