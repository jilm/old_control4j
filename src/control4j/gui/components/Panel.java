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
import control4j.scanner.Setter;
import control4j.scanner.Getter;

/**
 *
 */
public class Panel extends AbstractPanel
{

  private int width = 50;
  private int height = 50;

  /**
   *
   */
  private static int counter;

  /**
   *
   */
  private final int number = ++counter;

  public Panel()
  {
    super();
    setLayout(null);
  }

  /**
   *
   */
  @Override
  protected int getCounter()
  {
    return number;
  }

  @Override
  @Getter(key="Width")
  public int getWidth()
  {
    return super.getWidth();
  }

  @Setter(key="Width")
  public void setWidth(int width)
  {
    this.width = width;
    revalidate();
    repaint();
  }

  @Override
  @Getter(key="Height")
  public int getHeight()
  {
    return super.getHeight();
  }

  @Setter(key="Height")
  public void setHeight(int height)
  {
    this.height = height;
    revalidate();
    repaint();
  }

  @Override
  public void doLayout()
  {
    super.doLayout();
    for (int i=0; i<getComponentCount(); i++)
    {
      Component component = getComponent(i);
      Dimension size = component.getPreferredSize();
      if (size != null) component.setSize(size);
    }
  }

  @Override
  public Dimension getPreferredSize()
  {
    return new Dimension(width, height);
  }

}
