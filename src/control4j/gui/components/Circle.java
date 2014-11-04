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
import control4j.ConfigItem;
import control4j.Signal;
import control4j.SignalFormat;
import control4j.modules.IGuiUpdateListener;
import control4j.tools.Preferences;
import control4j.gui.ColorParser;
import control4j.gui.SignalAssignment;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.AbstractButton;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

/**
 *  Two state indicator which simulates LED.
 */
public class Circle extends AbstractComponent
{

  private int size = 20;

  /**
   *
   */
  private static int counter;

  /**
   *
   */
  private final int number = ++counter;

  /**
   *
   */
  public Circle()
  {
    super();
  }

  /**
   *
   */
  @Override
  protected int getCounter()
  {
    return number;
  }

  @Getter(key="Size")
  public int getLedSize()
  {
    return this.size;
  }

  @Setter(key="Size")
  public void setLedSize(int size)
  {
    this.size = size;
    revalidate();
    repaint();
  }

  @Override
  public Dimension getPreferredSize()
  {
    return new Dimension(size, size);
  }

  @Override
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    // paint an indicator
    g.fillOval(0, 0, size, size);
  }

}
