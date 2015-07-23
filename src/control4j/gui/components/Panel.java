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
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JPanel;
import control4j.scanner.Setter;
import control4j.scanner.Getter;
import control4j.gui.VisualContainer;

/**
 *
 */
@control4j.annotations.AGuiObject(name="Panel")
public class Panel extends VisualContainer
{

  private int x;
  private int y;
  private int width = 50;
  private int height = 50;

  public Panel()
  {
    super();
  }

  @Getter(key="X")
  public int getX()
  {
    return x;
  }

  @Setter(key="X")
  public void setX(int x)
  {
    this.x = x;
    if (component != null)
    {
      component.setLocation(x, y);
      component.revalidate();
      component.repaint();
    }
  }

  @Getter(key="Y")
  public int getY()
  {
    return y;
  }

  @Setter(key="Y")
  public void setY(int y)
  {
    this.y = y;
    if (component != null)
    {
      component.setLocation(x, y);
      component.revalidate();
      component.repaint();
    }
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
    if (component != null)
    {
      component.setSize(width, height);
      component.setPreferredSize(new Dimension(width, height));
      component.revalidate();
      component.repaint();
    }
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
    if (component != null)
    {
      component.setSize(width, height);
      component.setPreferredSize(new Dimension(width, height));
      component.revalidate();
      component.repaint();
    }
  }

  @Override
  protected JComponent createSwingComponent()
  {
    JPanel panel = new PanelPainter();
    panel.setLayout(null);
    return panel;
  }

  @Override
  public void configureVisualComponent()
  {
    component.setBounds(x, y, width, height);
    component.setPreferredSize(new Dimension(width, height));
    super.configureVisualComponent();
    component.revalidate();
    component.repaint();
  }

}
