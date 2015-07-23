package control4j.gui;

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

import javax.swing.JComponent;

/**
 *
 *
 *
 */
public abstract class VisualContainer extends VisualObject
{

  /**
   *  Index of the first changer object in the list of children.
   */
  private int firstChangerIndex = 0;

  /**
   *  Creates a visual component for each VisualObject child.
   *  Child visual component is than added to this visual component
   *  and finally, configureVisualComponent is called for each
   *  VisualObject child.
   */
  @Override
  public void configureVisualComponent()
  {
  }

  /**
   *  Calls a releseVisualComponent for all of the visual child objects.
   *  Moreover, it removes child visual components from this visual
   *  component.
   */
  @Override
  public void releaseVisualComponent()
  {
    super.releaseVisualComponent();
  }

  @Override
  public boolean isVisualContainer()
  {
    return true;
  }

  /**
   *
   */
  @Override
  public GuiObject clone(boolean full)
  {
    VisualContainer clone = (VisualContainer)super.clone(full);
    clone.firstChangerIndex = this.firstChangerIndex;
    return clone;
  }

}
