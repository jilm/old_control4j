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
import control4j.tools.Preferences;
import control4j.gui.ColorParser;
import control4j.gui.VisualObject;
import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

/**
 *
 *  Paints circle
 *
 */
public class Circle extends VisualObjectBase
{

  private int size = 20;

  /**
   *
   */
  public Circle()
  {
    super();
  }

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
    }
  }

  @Override
  protected JComponent createSwingComponent()
  {
    return new CirclePainter();
  }

  @Override
  protected void configureVisualComponent()
  {
    super.configureVisualComponent();
    component.setSize(size, size);
    component.setPreferredSize(new Dimension(size, size));
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
      g.fillOval(0, 0, size, size);
    }

  }

}
