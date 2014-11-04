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

import control4j.ConfigItem;
import control4j.Signal;
import control4j.SignalFormat;
import control4j.modules.IGuiUpdateListener;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import control4j.scanner.Setter;
import control4j.scanner.Getter;
import java.text.DecimalFormat;

/**
 *  A component dedicated to show a line of text.
 */
public class Label extends AbstractComponent
{
  /** font size */
  private double fontSize = 12;

  /** The text that will be displayed */
  protected String text = "";

  /** Dimension of the window */
  protected Dimension dimension = new Dimension(50, 50);

  private boolean isFixedWidth = false;
  private boolean isFixedHeight = false;

  private double horizontalAlignment = 0.0;
  private double verticalAlignment = 0.5;

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
  public Label()
  {
  }

  /**
   *
   */
  @Override
  protected int getCounter()
  {
    return number;
  }

  @Setter(key="Text")
  public void setText(String text)
  {
    if (text == null)
      this.text = "";
    else
      this.text = text;
    computeSize();
    revalidate();
    repaint();
  }

  @Getter(key="Text")
  public String getText()
  {
    return text;
  }

  @Setter(key="Font Size")
  public void setFontSize(double size)
  {
    fontSize = size;
    setFont(getFont().deriveFont((float)fontSize));
    computeSize();
    revalidate();
    repaint();
  }

  @Getter(key="Font Size")
  public double getFontSize()
  {
    return fontSize;
  }

  @Getter(key="Fixed Width")
  public boolean isFixedWidth()
  {
    return isFixedWidth;
  }

  @Setter(key="Fixed Width")
  public void setFixedWidth(boolean value)
  {
    isFixedWidth = value;
    computeSize();
    revalidate();
    repaint();
  }

  @Getter(key="Fixed Height")
  public boolean isFixedHeight()
  {
    return isFixedHeight;
  }

  @Setter(key="Fixed Height")
  public void setFixedHeight(boolean value)
  {
    isFixedHeight = value;
    computeSize();
    revalidate();
    repaint();
  }

  @Getter(key="Width")
  @Override
  public int getWidth()
  {
    return super.getWidth();
  }

  @Setter(key="Width")
  public void setWidth(int width)
  {
    isFixedWidth = true;
    dimension.width = width;
    computeSize();
    revalidate();
    repaint();
  }

  @Getter(key="Height")
  @Override
  public int getHeight()
  {
    return super.getHeight();
  }

  @Setter(key="Height")
  public void setHeight(int height)
  {
    isFixedHeight = true;
    dimension.height = height;
    computeSize();
    revalidate();
    repaint();
  }

  @Getter(key="Horizontal Alignment")
  public double getHorizontalAlignment()
  {
    return horizontalAlignment;
  }

  @Setter(key="Horizontal Alignment")
  public void setHorizontalAlignment(double value)
  {
    horizontalAlignment = value;
    revalidate();
    repaint();
  }

  @Getter(key="Vertical Alignment")
  public double getVerticalAlignment()
  {
    return verticalAlignment;
  }

  @Setter(key="Vertical Alignment")
  public void setVerticalAlignment(double value)
  {
    verticalAlignment = value;
    revalidate();
    repaint();
  }

  protected void computeSize()
  {
    // get the metrics of the font
    FontMetrics metrics = getFontMetrics(getFont());
    // get rectangles for particular text
    int width = metrics.stringWidth(text);
    if (!isFixedWidth) dimension.width = width;
    if (!isFixedHeight) dimension.height = metrics.getHeight();
  }

  @Override
  protected void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    // paint background
    g.setColor(getBackground());
    g.fillRect(0, 0, dimension.width, dimension.height);
    // get text dimension and coordinates
    FontMetrics metrics = g.getFontMetrics();
    int width = metrics.stringWidth(text);
    int x = (int)Math.round((dimension.width - width) * horizontalAlignment);
    int height = metrics.getHeight();
    int y = (int)Math.round((dimension.height - height) * verticalAlignment);
    // paint value
    g.setColor(getForeground());
    g.drawString(text, x, g.getFontMetrics().getAscent() + y);
  }

  @Override
  public Dimension getPreferredSize()
  {
    return dimension;
  }

  @Override
  public Dimension getMinimumSize()
  {
    return dimension;
  }
  

}
