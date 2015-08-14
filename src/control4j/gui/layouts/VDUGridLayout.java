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

package control4j.gui.layouts;

import static cz.lidinsky.tools.Validate.notNull;

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;

import org.apache.commons.collections4.IteratorUtils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Iterator;

public class VDUGridLayout implements LayoutManager {

  public VDUGridLayout() {}

  //--------------------------------------------------------------- Properties.

  private int hgap = 5;

  private int vgap = 5;

  //--------------------------------------------- LayoutManager Implementation.

  public void addLayoutComponent(String name, Component component) {
  }

  public void removeLayoutComponent(Component component) {
  }

  public void layoutContainer(Container parent) {
    synchronized (parent.getTreeLock()) {

      Insets insets = parent.getInsets();
      int width = parent.getWidth() - insets.left - insets.right - 2 * hgap;
      int height = parent.getHeight() - insets.top - insets.bottom - 2 * vgap;
      int x = 0;  // actual position
      int y = 0;

      // First of all, find the biggest component.
      Dimension combinedSize = new Dimension();
      int maxWidth = 0;
      int maxHeight = 0;
      int count = 0;
      for (Component component :
          IteratorUtils.asIterable(getComponentIterator(parent))) {
        Dimension preferredSize = component.getPreferredSize();
        maxWidth = Math.max(maxWidth, preferredSize.width);
        maxHeight = Math.max(maxHeight, preferredSize.height);
        count++;
      }

      // Calculate number of rows and colums
      float elementRatio = (float)maxWidth / (float)maxHeight;
      float min = Float.MAX_VALUE;
      int optRows = 1;
      int optCols = count;
      for (int rows = 1; rows <= count; rows++) {
        int cols = count / rows + ((count % rows) > 0 ? 1 : 0);
        float ratio = ((float)width / (float)cols)
          / ((float)height / (float)rows);
        float metrics = Math.abs(ratio - elementRatio);
        if (metrics < min) {
          min = metrics;
          optRows = rows;
          optCols = cols;
        }
      }

      // layout components
      maxWidth = (width - ((optCols - 1) * hgap)) / optCols;
      maxHeight = (height - ((optRows - 1) * vgap)) / optRows;
      count = 0;
      for (Component component :
          IteratorUtils.asIterable(getComponentIterator(parent))) {
        component.setSize(maxWidth, maxHeight);
        component.setLocation(
            (count % optCols) * maxWidth + (count % optCols) * hgap + hgap,
            (count / optCols) * maxHeight + (count / optCols) * vgap + vgap);
        count++;
      }
    }
  }

  public Dimension minimumLayoutSize(Container parent) {
    synchronized (parent.getTreeLock()) {

      int nmembers = parent.getComponentCount();
      int area = 0;
      for (int i = 0; i < nmembers; i++) {
        Component component = parent.getComponent(i);
        if (component.isVisible()) {
          Dimension dimension = component.getMinimumSize();
          area += dimension.width * dimension.height;
        }
      }
      int a = (int)Math.round(Math.sqrt(area));
      return new Dimension(a, a);
    }
  }

  public Dimension preferredLayoutSize(Container parent) {
    return minimumLayoutSize(parent);
  }

  //------------------------------------------------------------------ Private.

  // TODO:  only visible components!!!
  public static Iterator<Component> getComponentIterator(
      final Container parent) {

    return new Iterator<Component>() {

      private int index = 0;

      public boolean hasNext() {
        return index < parent.getComponentCount();
      }

      public Component next() {
        return parent.getComponent(index++);
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }

    };
  }

}
