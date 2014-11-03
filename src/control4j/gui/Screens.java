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

import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import java.awt.Dimension;
import control4j.scanner.Getter;
import control4j.scanner.Setter;
import control4j.gui.components.Screen;
import control4j.gui.changers.IChangeable;

/**
 *  Set of screens that contains the GUI components.
 */
public class Screens extends JTabbedPane
{
  private int width = 800;
  private int height = 600;

  public Screens()
  {
    super();
  }

  /**
   *  Adds one screen with default parameters at the end of the screens.
   */
  public Screen addScreen()
  {
    return addScreen("Screen" + (getTabCount() + 1));
  }

  public Screen addScreen(String title)
  {
    Screen screen = new Screen();
    return addScreen(screen, title);
  }

  public Screen addScreen(Screen screen, String title)
  {
    addTab(title, screen);
    return screen;
  }

  @Override
  public Dimension getPreferredSize()
  {
    return new Dimension(width, height);
  }

  @Override
  public Dimension getMinimumSize()
  {
    return new Dimension(width, height);
  }

  @Override
  public Dimension getMaximumSize()
  {
    return new Dimension(width, height);
  }

  @Getter(key="Width")
  public int getScreensWidth()
  {
    return width;
  }

  @Setter(key="Width")
  public void setScreensWidth(int width)
  {
    this.width = width;
    setSize(width, height);
  }
  
  @Getter(key="Height")
  public int getScreensHeight()
  {
    return height;
  }

  @Setter(key="Height")
  public void setScreensHeight(int height)
  {
    this.height = height;
    setSize(width, height);
  }

  /**
   *
   */
  public Screen getSelectedScreen()
  {
    return (Screen)getSelectedComponent();
  }

  public Screen getScreen(int index)
  {
    return (Screen)getComponentAt(index);
  }

  public int getScreenCount()
  {
    return getTabCount();
  }

  public Screen removeScreen(int index)
  {
    Screen removed = getScreen(index);
    remove(index);
    return removed;
  }

}
