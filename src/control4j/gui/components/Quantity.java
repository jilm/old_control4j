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

public class Quantity extends Label
{

  /** The number which will be displayed */
  private double value = Double.NaN;
  private DecimalFormat format = new DecimalFormat();

  public int digits = 5;

  public Quantity()
  {
    super();
    setHorizontalAlignment(1.0);
  }

  @Getter(key="Fraction Digits")
  public int getFractionDigits()
  {
    return format.getMaximumFractionDigits();
  }

  @Setter(key="Fraction Digits")
  public void setFractionDigits(int digits)
  {
    format.setMaximumFractionDigits(digits);
    format.setMinimumFractionDigits(digits);
    update();
    computeSize();
    repaint();
  }

  @Getter(key="Digits")
  public int getDigits()
  {
    return format.getMaximumIntegerDigits();
  }

  @Setter(key="Digits")
  public void setDigits(int digits)
  {
    format.setMaximumIntegerDigits(digits);
    this.digits = digits;
    update();
    computeSize();
  }

  @Getter(key="Value")
  public double getValue()
  {
    return value;
  }

  @Setter(key="Value")
  public void setValue(double value)
  {
    this.value = value;
    update();
    repaint();
  }

  @Override
  protected void computeSize()
  {
    // get the metrics of the font
    FontMetrics metrics = getFontMetrics(getFont());
    // get the text of the appropriate size
    int digits = getDigits();
    double number = (Math.pow(10.0d, digits) - 1) * -1;
    String text = format.format(number);
    // get rectangles for particular text
    int width = metrics.stringWidth(text);
    if (!isFixedWidth()) dimension.width = width;
    if (!isFixedHeight()) dimension.height = metrics.getHeight();
    setSize(dimension);
  }

  @Override
  protected void paintComponent(java.awt.Graphics g)
  {
    super.paintComponent(g);
  }

  protected void update()
  {
    String strValue;
    if (Double.isNaN(value))
      this.text = "?";
    else
      this.text = format.format(value);
  }


  @Override
  public Object clone() throws CloneNotSupportedException
  {
    Quantity clone = (Quantity)super.clone();
    clone.format = (DecimalFormat)format.clone();
    return clone;
  }


}
