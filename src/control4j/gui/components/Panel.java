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

  @Getter(key="Width")
  public int getWidth()
  {
    return width;
  }

  @Setter(key="Width")
  public void setWidth(int width)
  {
    this.width = width;
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
  }

  @Override
  protected JComponent createSwingComponent()
  {
    return new JPanel();
  }

  @Override
  protected void configureVisualComponent()
  {
    component.setBounds(x, y, width, height);
    super.configureVisualComponent();
    component.revalidate();
    component.repaint();
  }

}
