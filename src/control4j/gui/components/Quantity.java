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
import control4j.gui.VisualObject;
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
 *
 *  A component which purpose is to show real numbers.
 *
 */
@control4j.annotations.AGuiObject(name="Quantity")
public class Quantity extends VisualObjectBase
{

  /** The number which will be displayed */
  private double value = Double.NaN;
  private DecimalFormat format = new DecimalFormat();
  private float fontSize = 12.0f;

  public int digits = 5;

  public Quantity()
  {
    super();
    //setHorizontalAlignment(1.0);
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
    if (component != null) 
    {
      update();
      Dimension size = computeSize();
      component.setPreferredSize(size);
      component.setMaximumSize(size);
      component.setMinimumSize(size);
      component.revalidate();
      component.repaint();
    }
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
    if (component != null) 
    {
      update();
      Dimension size = computeSize();
      component.setPreferredSize(size);
      component.setMaximumSize(size);
      component.setMinimumSize(size);
      component.revalidate();
      component.repaint();
    }
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
    if (component != null)
    {
      update();
    }
  }

  @Getter(key="Font Size")
  public double getFontSize()
  {
    return fontSize;
  }

  @Setter(key="Font Size")
  public void setFontSize(double fontSize)
  {
    this.fontSize = (float)fontSize;
    if (component != null)
    {
      component.setFont(component.getFont().deriveFont(this.fontSize));
      Dimension size = computeSize();
      component.setPreferredSize(size);
      component.setMaximumSize(size);
      component.setMinimumSize(size);
    }
  }

  @Override
  protected JComponent createSwingComponent()
  {
    return new JLabel();
  }

  @Override
  protected void configureVisualComponent()
  {
    super.configureVisualComponent();
    ((JLabel)component).setHorizontalAlignment(JLabel.RIGHT);
    component.setFont(component.getFont().deriveFont(fontSize));
    Dimension size = computeSize();
    component.setPreferredSize(size);
    component.setMaximumSize(size);
    component.setMinimumSize(size);
    update();
    component.revalidate();
    component.repaint();
  }

  private void update()
  {
    if (Double.isNaN(value))
      ((JLabel)component).setText("?");
    else
      ((JLabel)component).setText(format.format(value));
  }

  private Dimension computeSize()
  {
    // get the metrics of the font
    FontMetrics metrics = component.getFontMetrics(component.getFont());
    // create the text of the appropriate size
    int digits = getDigits();
    double number = (Math.pow(10.0d, digits) - 1) * -1;
    String text = format.format(number);
    // get rectangles for particular text
    int width = metrics.stringWidth(text);
    int height = metrics.getHeight();
    return new Dimension(width, height);
  }

}
