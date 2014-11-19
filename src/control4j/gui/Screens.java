package control4j.gui;

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

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import java.awt.Dimension;
import control4j.scanner.Getter;
import control4j.scanner.Setter;
import control4j.gui.components.Screen;

/**
 *
 *  Maintains set of screens that contains the GUI components. 
 *  This is the root object in the gui tree.
 *
 *  <p>The only child of Screens may be a screen or a changer.
 *
 */
public class Screens extends VisualContainer
implements IChangeListener
{

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
   *  @throws IllegalArgumentException
   *             if child is not instance of Screen class
   */
  @Override
  public void add(VisualObject child)
  {
    if (child instanceof Screen)
    {
      super.add(child);
      child.addChangeListener(this);
      // insert a tab title for the screen
      if (visualComponent != null)
      {
	int index = getVisualObjectCount()-1;
	visualComponent.setTitleAt(index, ((Screen)child).getTitle());
      }
    }
    else
      throw new IllegalArgumentException();
  }

  /**
   *  @throws IllegalArgumentException
   *             if child is not an instance of Screen class
   */
  @Override
  public void insert(VisualObject child, int index)
  {
    if (child instanceof Screen)
    {
      super.insert(child, index);
      child.addChangeListener(this);
      // insert a tab title for the screen
      if (visualComponent != null)
	visualComponent.setTitleAt(index, ((Screen)child).getTitle());
    }
    else
      throw new IllegalArgumentException();
  }

  @Override
  public GuiObject removeChild(int index)
  {
    getChild(index).removeChangeListener(this);
    return super.removeChild(index);
  }

  /**
   *  Returns width of the screens area.
   */
  @Getter(key="Width")
  public int getWidth()
  {
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
  @Setter(key="Width")
  public void setWidth(int width)
  {
    if (width < 0)
      throw new IllegalArgumentException();
    this.width = width;
    if (visualComponent != null)
    {
      visualComponent.setSize(width, height);
    }
    fireChangeEvent(new ChangeEvent(this, "Width", this.width));
  }
  
  /**
   *  Returns height of the screens area.
   */
  @Getter(key="Height")
  public int getHeight()
  {
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
  @Setter(key="Height")
  public void setHeight(int height)
  {
    if (height < 0)
      throw new IllegalArgumentException();
    this.height = height;
    if (visualComponent != null)
    {
      visualComponent.setSize(width, height);
    }
    fireChangeEvent(new ChangeEvent(this, "Height", this.height));
  }

  /**
   *  Creates and returns new instance of JTabbedPane.
   */
  @Override
  public JComponent createVisualComponent()
  {
    if (visualComponent == null)
    {
      visualComponent = new JTabbedPane();
      visualComponent.putClientProperty(LINK_KEY, this);
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
  public void configureVisualComponent()
  {
    if (visualComponent != null)
    {
      visualComponent.setSize(width, height);
      for (int i=0; i<getVisualObjectCount(); i++)
      {
	VisualObject screen = getVisualObject(i);
	JComponent screenComponent = screen.createVisualComponent();
	visualComponent.addTab(((Screen)screen).getTitle(), screenComponent);
	screen.configureVisualComponent();
      }
      visualComponent.repaint();
    }
    else
      assert false;
  }

  /**
   *  Returns the visual component if exists, otherwise returns null.
   */
  @Override
  public JComponent getVisualComponent()
  {
    return visualComponent;
  }

  /**
   *  Releases the visual component. It subsequently releases visual
   *  components of all the children.
   */
  @Override
  protected void releaseVisualComponent()
  {
    if (visualComponent != null)
    {
      for (int i=0; i<getVisualObjectCount(); i++)
	getVisualObject(i).releaseVisualComponent();
      visualComponent.removeAll();
    }
    visualComponent = null;
  }

  /**
   *  This method is called by screen objects if something is changed.
   *  If the title of the screen has changed, it actualize appropriate
   *  tab title.
   */
  public void propertyChanged(ChangeEvent e)
  {
    if (visualComponent != null)
      if (e.getKey().equals("Title") || e.getKey().equals("Name"))
      {
        int index = children.indexOf(e.getSource());
        String title = ((Screen)getVisualObject(index)).getTitle();
        visualComponent.setTitleAt(index, title);
      }
  }

  /**
   *
   */
  public void showScreen(int index)
  {
    if (visualComponent != null)
      visualComponent.setSelectedIndex(index);
  }

  /**
   *
   */
  public void showScreen(Screen screen)
  {
    if (visualComponent != null)
      visualComponent.setSelectedComponent(screen.getVisualComponent());
  }

}
