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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

public class SingleLayout implements LayoutManager {

  public SingleLayout() {}

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
      int nmembers = parent.getComponentCount();
      int x = 0;  // actual position
      int y = 0;

      for (int i = 0; i < nmembers; i++) {
        Component component = parent.getComponent(i);
        if (component.isVisible()) {
          component.setSize(width, height);
          component.setLocation(x, y);
          return;
        }
      }
    }
  }

  public Dimension minimumLayoutSize(Container parent) {
    return null;
  }

  public Dimension preferredLayoutSize(Container parent) {
    return null;
  }

  //---------------------------------------------------------------------------

}
