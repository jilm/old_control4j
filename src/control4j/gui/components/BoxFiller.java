package control4j.gui.components;

/*
 *  Copyright 2013, 2014, 2015 Jiri Lidinsky
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

import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.BoxLayout;

import control4j.scanner.Getter;
import control4j.scanner.Setter;

/**
 *
 *  An empty rectangle (box) which may be used to place a space between
 *  two components inside a Box component.
 *
 *  @see control4j.gui.components.Box
 *
 */
@control4j.annotations.AGuiObject(name="Box filler")
public class BoxFiller extends control4j.gui.VisualObject
{

  /**
   *  Size of the box. It depends on the axis of parent box if size
   *  represents width or height.
   */
  private int size;

  private boolean glue = false;

  @Getter(key="Size")
  public int getSize()
  {
    return size;
  }

  @Setter(key="Size")
  public void setSize(int size)
  {
    this.size = size;
    if (component != null)
    {
      ((javax.swing.Box.Filler)component).changeShape(getMinSize(), getPreffSize(), getMaxSize());
    }
  }

  @Getter(key="Is Glue")
  public boolean isGlue()
  {
    return glue;
  }

  @Setter(key="Is Glue")
  public void setGlue(boolean isGlue)
  {
    glue = isGlue;
    if (component != null)
    {
      ((javax.swing.Box.Filler)component).changeShape(getMinSize(), getPreffSize(), getMaxSize());
    }
  }

  @Override
  protected JComponent createSwingComponent()
  {
    return new javax.swing.Box.Filler(getMinSize(), getPreffSize(), getMaxSize());
  }

  @Override
  public void configureVisualComponent()
  {
  }

  private Dimension getMinSize() {
    if (component != null) {
      boolean isLineAxis
        = ((BoxLayout)component.getParent().getLayout()).getAxis()
        == BoxLayout.LINE_AXIS;
      int size = glue ? 0 : this.size;
      int width = isLineAxis ? size : 0;
      int height = isLineAxis ? 0 : size;
      return new Dimension(width, height);
    }
    return null;
  }

  private Dimension getMaxSize() {
    if (component != null) {
      boolean isLineAxis
        = ((BoxLayout)component.getParent().getLayout()).getAxis()
        == BoxLayout.LINE_AXIS;
      int size = glue ? Integer.MAX_VALUE : this.size;
      int width = isLineAxis ? size : 0;
      int height = isLineAxis ? 0 : size;
      return new Dimension(width, height);
    }
    return null;
  }

  private Dimension getPreffSize() {
    if (component != null) {
      boolean isLineAxis
        = ((BoxLayout)component.getParent().getLayout()).getAxis()
        == BoxLayout.LINE_AXIS;
      int size = glue ? 0 : this.size;
      int width = isLineAxis ? size : 0;
      int height = isLineAxis ? 0 : size;
      return new Dimension(width, height);
    }
    return null;
  }

}
