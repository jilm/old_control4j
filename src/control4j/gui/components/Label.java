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
import control4j.gui.VisualObject;
import java.text.DecimalFormat;

/**
 *
 *  A component dedicated to show a line of text.
 *
 */
public class Label extends VisualObject
{
  /** font size */
  private float fontSize = 12;

  /** The text that will be displayed */
  protected String text = "";

  private boolean isFixedWidth = false;
  private boolean isFixedHeight = false;

  private float horizontalAlignment = 0.0f;
  private float verticalAlignment = 0.5f;

  private int x;
  private int y;
  private int width;
  private int height;

  /**
   *
   */
  public Label()
  {
  }

  @Setter(key="Text")
  public void setText(String text)
  {
    if (text == null)
      this.text = "";
    else
      this.text = text;
    if (component != null)
      ((JLabel)component).setText(this.text);
  }

  @Getter(key="Text")
  public String getText()
  {
    return text;
  }

  @Setter(key="Font Size")
  public void setFontSize(double size)
  {
    fontSize = (float)size;
    if (component != null)
    {
      Font font = component.getFont();
      component.setFont(font.deriveFont(fontSize));
    }
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
  }

  @Getter(key="Width")
  public int getWidth()
  {
    return width;
  }

  @Setter(key="Width")
  public void setWidth(int width)
  {
    isFixedWidth = true;
    this.width = width;
    if (component != null)
      component.setSize(width, height);
  }

  @Getter(key="Height")
  public int getHeight()
  {
    return height;
  }

  @Setter(key="Height")
  public void setHeight(int height)
  {
    isFixedHeight = true;
    this.height = height;
    if (component != null)
      component.setSize(width, height);
  }

  @Getter(key="Horizontal Alignment")
  public double getHorizontalAlignment()
  {
    return horizontalAlignment;
  }

  @Setter(key="Horizontal Alignment")
  public void setHorizontalAlignment(double value)
  {
    horizontalAlignment = (float)value;
  }

  @Getter(key="Vertical Alignment")
  public double getVerticalAlignment()
  {
    return verticalAlignment;
  }

  @Setter(key="Vertical Alignment")
  public void setVerticalAlignment(double value)
  {
    verticalAlignment = (float)value;
  }

  @Override
  protected JComponent createSwingComponent()
  {
    return new JLabel();
  }

  @Override
  protected void configureVisualComponent()
  {
    component.setLocation(x, y);
    ((JLabel)component).setText(text);
    Font font = component.getFont();
    component.setFont(font.deriveFont(fontSize));
  }

}
