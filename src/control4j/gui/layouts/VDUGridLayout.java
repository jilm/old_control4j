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

  //--------------------------------------------- LayoutManager Implementation.

  public void addLayoutComponent(String name, Component component) {
  }

  public void removeLayoutComponent(Component component) {
  }

  public void layoutContainer(Container parent) {
    synchronized (parent.getTreeLock()) {

      Insets insets = parent.getInsets();
      int width = parent.getWidth() - insets.left - insets.right;
      int height = parent.getHeight() - insets.top - insets.bottom;
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
      int cols = Math.max(width / maxWidth, 1);
      int rows = Math.max(height / maxHeight, 1);
      float factX;
      float factY;
      while ((cols * rows) < count) {
        // The area is smaller than needed.
        factX = ((float)width / (float)cols) / (float)maxWidth;
        factY = ((float)height / (float)rows) / (float)maxHeight;
        if (factX > factY) {
          cols++;
        } else {
          rows++;
        }
      }

      // layout components
      maxWidth = width / cols;
      maxHeight = height / rows;
      count = 0;
      for (Component component :
          IteratorUtils.asIterable(getComponentIterator(parent))) {
        component.setSize(maxWidth, maxHeight);
        component.setLocation(
            (count % cols) * maxWidth, (count / cols) * maxHeight);
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
