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
   *  The index of the first Changer object in the children list.
   */
  private int firstChangerIndex = 0;

  /**
   *  A component that is responsible for painting. 
   *  May contain null value.
   */
  private JTabbedPane visualComponent;

  /**
   *  Add given screen at the end of the list of children.
   *  If this object has a visual component, it creates a
   *  visual component of the given screen as well and adds 
   *  it as a new tab. Finally it requests repainting.
   *
   *  @param screen
   *             a screen to be added
   *
   *  @throws NullPointerException
   *             if the screen parameter contains null value
   */
  public void add(Screen screen)
  {
    // add it to the list of children
    insertChild(screen, firstChangerIndex);
    firstChangerIndex++;
    // register as a change listener
    screen.addChangeListener(this);
    // create visual component
    if (visualComponent != null)
    {
      JComponent screenComponent 
	= ((VisualObject)screen).createVisualComponent();
      visualComponent.addTab(screen.getTitle(), screenComponent);
      ((VisualObject)screen).configureVisualComponent();
      visualComponent.repaint();
    }
  }

  /**
   *  Inserts given screen at the specified position.
   *  If this object has a visual component, it creates a
   *  visual component of the given screen as well and adds 
   *  it as a new tab. Finally it requests repainting.
   *
   *  @param screen
   *             a screen to be added
   *
   *  @param index
   *             a place where the screen is to be added
   *
   *  @throws NullPointerException
   *             if the screen parameter contains null value
   *
   *  @throws IndexOutOfBoundsException
   *             if index is negative number or is greater than
   *             the current number of screens
   */
  public void insert(Screen screen, int index)
  {
    // insert it to the list of children
    if (index > firstChangerIndex)
      throw new IndexOutOfBoundsException();
    insertChild(screen, index);
    firstChangerIndex++;
    // register change listener
    screen.addChangeListener(this);
    // create visual component
    if (visualComponent != null)
    {
      JComponent screenComponent 
	= ((VisualObject)screen).createVisualComponent();
      visualComponent.insertTab(
	screen.getTitle(), null, screenComponent, null, index);
      ((VisualObject)screen).configureVisualComponent();
      visualComponent.repaint();
    }
  }

  /**
   *  Removes screen at the given index from the list of children.
   *  If this object has the visual component, the tab of the screen
   *  is removed as well. Moreover, the visual component of the
   *  removed screen is released.
   *
   *  @param index
   *             an index of the screen that will be removed
   *
   *  @return the screen object which was removed
   *
   *  @throws IndexOutOfBoundsException
   *             if index is negative number or is greater or equal
   *             to the number of screens
   */
  public Screen removeScreen(int index)
  {
    if (index >= firstChangerIndex)
      throw new IndexOutOfBoundsException();
    // if there is visual component, release it
    if (visualComponent != null)
    {
      visualComponent.remove(index);
      ((VisualObject)getScreen(index)).releaseVisualComponent();
    }
    // remove it from the list of children
    Screen screen = (Screen)removeChild(index);
    firstChangerIndex--;
    screen.removeChangeListener(this);
    return screen;
  }

  /**
   *  Returns screen at the specified position.
   *
   *  @param index
   *             a zero based index of the screen that will be returned
   *
   *  @return a screen at the given index
   *
   *  @throws IndexOutOfBoundsException
   *             if the index is negative number or is greater or equal
   *             to the number of screens
   */
  public Screen getScreen(int index)
  {
    if (index >= firstChangerIndex)
      throw new IndexOutOfBoundsException();
    return (Screen)getChild(index);
  }

  /**
   *  Returns number of screens currently in the list.
   *
   *  @return number of screens that are children of this object
   */
  public int getScreenCount()
  {
    return firstChangerIndex;
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
      visualComponent = new JTabbedPane();
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
      for (int i=0; i<firstChangerIndex; i++)
      {
	Screen screen = getScreen(i);
	JComponent screenComponent 
	  = ((VisualObject)screen).createVisualComponent();
	visualComponent.addTab(screen.getTitle(), screenComponent);
	((VisualObject)screen).configureVisualComponent();
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
      for (int i=0; i<getScreenCount(); i++)
	((VisualObject)getScreen(i)).releaseVisualComponent();
      visualComponent.removeAll();
    }
    visualComponent = null;
  }

  /**
   *
   */
  @Override
  public void insert(Changer child, int index)
  {
    if (index < 0)
      throw new IndexOutOfBoundsException();
    insertChild(child, index + firstChangerIndex);
  }

  /**
   *
   */
  @Override
  public Changer removeChanger(int index)
  {
    if (index < 0)
      throw new IndexOutOfBoundsException();
    return (Changer)removeChild(index + firstChangerIndex);
  }

  /**
   *
   */
  @Override
  public Changer getChanger(int index)
  {
    if (index < 0)
      throw new IndexOutOfBoundsException();
    return (Changer)getChild(index + firstChangerIndex);
  }

  /**
   *
   */
  @Override
  public int getChangerCount()
  {
    return size() - firstChangerIndex;
  }

  /**
   *  This method is called by screen objects if something is changed.
   *  If the title of the screen has changed, it actualize appropriate
   *  tab title.
   */
  public void propertyChanged(ChangeEvent e)
  {
    if (visualComponent != null)
      if (e.getKey().equals("Title"))
      {
        int index = children.indexOf(e.getSource());
        String title = getScreen(index).getTitle();
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
