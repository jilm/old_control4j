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
import javax.swing.JTabbedPane;
import java.awt.Dimension;
import cz.lidinsky.tools.reflect.Getter;
import cz.lidinsky.tools.reflect.Setter;
import control4j.gui.components.Screen;

/**
 *
 *  Maintains a set of screens that contains the GUI components.
 *  This is the root object in the gui tree.
 *
 *  <p>The only child of Screens may be a screen or a changer.
 *
 */
public class Screens extends VisualContainer
implements IChangeListener {

  /**
   *  The width of the area for screens.
   */
  private int width = 800;

  /**
   *  The height of the area for screens.
   */
  private int height = 600;

  /**
   *  A component that is responsible for painting.
   *  May contain null value.
   */
  private JTabbedPane visualComponent;

  /**
   *  Returns width of the screens area.
   */
  @Getter("Width")
  public int getWidth() {
    return width;
  }

  /**
   *  Sets new width of the screens area. If the visual component
   *  exists, the width of its area will be changed and repaint
   *  will be requested.
   *
   *  @param width
   *             new width of the screens area in pixels
   *
   *  @throws IllegalArgumentException
   *             if the width is negative number
   */
  @Setter("Width")
  public void setWidth(int width) {
    if (width < 0)
      throw new IllegalArgumentException();
    this.width = width;
    if (visualComponent != null)
    {
      visualComponent.setSize(width, height);
      visualComponent.revalidate();
      visualComponent.repaint();
    }
    fireChangeEvent(new ChangeEvent(this, "Width", this.width));
  }

  /**
   *  Returns height of the screens area.
   */
  @Getter("Height")
  public int getHeight() {
    return height;
  }

  /**
   *  Sets new height of the screens area. If the visual component
   *  exists, the height of its area will be changed and repaint
   *  will be requested.
   *
   *  @param height
   *             new height of the screens area in pixels
   *
   *  @throws IllegalArgumentException
   *             if the height is negative number
   *
   */
  @Setter("Height")
  public void setHeight(int height) {
    if (height < 0)
      throw new IllegalArgumentException();
    this.height = height;
    if (visualComponent != null) {
      visualComponent.setSize(width, height);
      visualComponent.revalidate();
      visualComponent.repaint();
    }
    fireChangeEvent(new ChangeEvent(this, "Height", this.height));
  }

  /**
   *  Creates and returns new instance of JTabbedPane.
   */
  @Override
  protected JComponent createSwingComponent() {
    if (visualComponent == null) {
      visualComponent = new JTabbedPane();
    }
    else
      assert false;
    return visualComponent;
  }

  /**
   *  Provides configuration of visual component. This method
   *  is called after the visual component si created and added
   *  as a child to parent visual component. The purpouse of
   *  this method is to take all of the properties like widht,
   *  height and set the size of visual component. After that,
   *  it calls createVisualComponent and configureVisualComponent
   *  even for all of the visual children.
   */
  @Override
  public void configureVisualComponent() {
    if (visualComponent != null)
    {
      visualComponent.setSize(width, height);
      visualComponent.setPreferredSize(new Dimension(width, height));
      //for (int i=0; i<getVisualObjectCount(); i++)
      //{
        //VisualObject screen = getVisualObject(i);
        //JComponent screenComponent = screen.createVisualComponent();
        //visualComponent.addTab(((Screen)screen).getTitle(), screenComponent);
        //screen.configureVisualComponent();
      //}
      super.configureVisualComponent();
      visualComponent.revalidate();
      visualComponent.repaint();
    }
    else
      assert false;
  }

  /**
   *  Returns the visual component if exists, otherwise returns null.
   */
  @Override
  public JComponent getVisualComponent() {
    return visualComponent;
  }

  /**
   *  Releases the visual component. It subsequently releases visual
   *  components of all the children.
   */
  @Override
  public void releaseVisualComponent() {
    if (visualComponent != null)
    {
      visualComponent.removeAll();
      visualComponent = null;
    }
  }

  /**
   *  This method is called by screen objects if something is changed.
   *  If the title of the screen has changed, it actualize appropriate
   *  tab title.
   */
  public void propertyChanged(ChangeEvent e) {
  }

  /**
   *
   */
  public void showScreen(int index) {
    if (visualComponent != null) {
      visualComponent.setSelectedIndex(index);
    }
  }

  /**
   *
   */
  public void showScreen(Screen screen) {
    if (visualComponent != null) {
      visualComponent.setSelectedComponent(screen.getVisualComponent());
    }
  }

  /**
   *  Only a Screen object or a Changer object may be a child of
   *  this object.
   */
  @Override
  public boolean isAssignable(GuiObject object) {
    if (!object.isVisual()) {
      return true;
    } else if (object instanceof Screen) {
      return true;
    } else {
      return false;
    }
  }

}
