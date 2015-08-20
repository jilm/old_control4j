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

import cz.lidinsky.tools.CollectionUtils;
import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;
import cz.lidinsky.tools.Validate;
import cz.lidinsky.tools.reflect.Getter;
import cz.lidinsky.tools.reflect.Setter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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

  private boolean dirty;

  public VDU() {
    super();
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
      //Dimension size = computeSize();
      //valueComponent.setPreferredSize(doubleDimension(size));
      //valueComponent.setMaximumSize(doubleDimension(size));
      //valueComponent.setMinimumSize(size);
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
      //Dimension size = computeSize();
      //valueComponent.setPreferredSize(doubleDimension(size));
      //valueComponent.setMaximumSize(doubleDimension(size));
      //valueComponent.setMinimumSize(size);
      component.revalidate();
      component.repaint();
    }
  }

  private int cells;

  public void setCellCount(int cells) {
    dirty = this.cells != cells;
    this.cells = cells;
  }

  public int getCellCount() {
    return cells;
  }

  public void addCell() {
    dirty = true;
    cells++;
  }

  public void setValue(int index, double value) {
    getVDU().setValue(index, value);
  }

  private ArrayList<String> labels = new ArrayList<String>();

  public void setLabel(int index, String label) {
    Validate.checkIndex(cells, index);
    CollectionUtils.add(labels, index, label);
  }

  @Override
  protected JComponent createSwingComponent() {
    return new control4j.gui.swing.VDU();
  }

  @Override
  public void configureVisualComponent() {
    super.configureVisualComponent();
    getVDU().addCells(cells);
    for (int i = 0; i < labels.size(); i++) {
      getVDU().setLabel(i, labels.get(i));
    }
    dirty = true;
    update();
  }

  public void update() {
    if (component != null) {
      component.revalidate();
      component.repaint();
    }
  }

  private control4j.gui.swing.VDU getVDU() {
    return (control4j.gui.swing.VDU)component;
  }

}
