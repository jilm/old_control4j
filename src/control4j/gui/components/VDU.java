/*
 *  Copyright 2015 Jiri Lidinsky
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

package control4j.gui.components;

import control4j.ConfigItem;
import control4j.Signal;
import control4j.SignalFormat;
import control4j.gui.VisualObject;
import control4j.modules.IGuiUpdateListener;

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;
import cz.lidinsky.tools.reflect.Getter;
import cz.lidinsky.tools.reflect.Setter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 *  A component which purpose is to display a signal.
 *
 */
@control4j.annotations.AGuiObject(name="VDU")
public class VDU extends VisualObjectBase {

  /** The number which will be displayed */
  private double value = Double.NaN;
  private DecimalFormat format = new DecimalFormat();
  private float fontSize = 12.0f;

  public int digits = 5;

  public VDU() {
    super();
    //setHorizontalAlignment(1.0);
  }

  @Getter("Fraction Digits")
  public int getFractionDigits() {
    return format.getMaximumFractionDigits();
  }

  @Setter("Fraction Digits")
  public void setFractionDigits(int digits) {
    format.setMaximumFractionDigits(digits);
    format.setMinimumFractionDigits(digits);
    if (component != null) {
      update();
      Dimension size = computeSize();
      valueLabel.setPreferredSize(doubleDimension(size));
      valueLabel.setMaximumSize(doubleDimension(size));
      valueLabel.setMinimumSize(size);
      component.revalidate();
      component.repaint();
    }
  }

  @Getter("Digits")
  public int getDigits() {
    return format.getMaximumIntegerDigits();
  }

  @Setter("Digits")
  public void setDigits(int digits) {
    format.setMaximumIntegerDigits(digits);
    this.digits = digits;
    if (component != null) {
      update();
      Dimension size = computeSize();
      valueLabel.setPreferredSize(doubleDimension(size));
      valueLabel.setMaximumSize(doubleDimension(size));
      valueLabel.setMinimumSize(size);
      component.revalidate();
      component.repaint();
    }
  }

  @Getter("Value")
  public double getValue() {
    return value;
  }

  @Setter("Value")
  public void setValue(double value) {
    this.value = value;
    if (component != null) {
      update();
    }
  }

  @Getter("Font Size")
  public double getFontSize() {
    return fontSize;
  }

  @Setter("Font Size")
  public void setFontSize(double fontSize) {
    this.fontSize = (float)fontSize;
    if (component != null) {
      component.setFont(component.getFont().deriveFont(this.fontSize));
      Dimension size = computeSize();
      valueLabel.setPreferredSize(doubleDimension(size));
      valueLabel.setMaximumSize(doubleDimension(size));
      valueLabel.setMinimumSize(size);
    }
  }

  String label;

  public void setLabel(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  private JLabel labelComponent;
  private JLabel valueLabel;
  private JLabel unit;

  @Override
  protected JComponent createSwingComponent() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    labelComponent = (JLabel)panel.add(new JLabel(label));
    valueLabel = (JLabel)panel.add(new JLabel());
    unit = (JLabel)panel.add(new JLabel("V"));
    return panel;
  }

  @Override
  public void configureVisualComponent() {
    super.configureVisualComponent();
    valueLabel.setHorizontalAlignment(JLabel.RIGHT);
    valueLabel.setFont(component.getFont().deriveFont(fontSize));
    Dimension size = computeSize();
    valueLabel.setPreferredSize(doubleDimension(size));
    valueLabel.setMaximumSize(doubleDimension(size));
    valueLabel.setMinimumSize(size);
    update();
    component.revalidate();
    component.repaint();
  }

  private void update() {
    if (Double.isNaN(value))
      valueLabel.setText("?");
    else
      valueLabel.setText(format.format(value));
  }

  private Dimension computeSize() {
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

  private static Dimension doubleDimension(Dimension in) {
    return new Dimension(in.width * 2, in.height * 2);
  }

}
