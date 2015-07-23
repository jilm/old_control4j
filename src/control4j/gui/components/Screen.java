package control4j.gui.components;

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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import control4j.scanner.Getter;
import control4j.scanner.Setter;
import control4j.gui.ChangeEvent;

/**
 *
 *
 *
 */
public class Screen extends control4j.gui.VisualContainer
{

  /**
   *  Screen title.
   */
  private String title;

  /**
   *
   */
  private Color background = null;

  @Override
  public void setName(String name) {
    super.setName(name);
    setTabTitle();
  }

  /**
   *
   */
  @Getter(key="Title")
  public String getTitle()
  {
    if (title != null && title.length() > 0)
      return title;
    else
      return getName();
  }

  /**
   *
   */
  @Setter(key="Title")
  public void setTitle(String title) {
    // set internal field
    if (title != null) {
      this.title = title.trim();
    } else {
      this.title = null;
    }
    setTabTitle();
  }

  protected void setTabTitle() {
    // if there is a visual component, set the title !
    if (component != null) {
      JTabbedPane parent = (JTabbedPane)component.getParent();
      int index = parent.indexOfComponent(component);
      if (index >= 0) {
        parent.setTitleAt(index, getTitle());
      }
    }
  }

  /**
   *
   */
  @Getter(key="Background Color")
  public Color getBackground()
  {
    if (background != null)
      return background;
    else if (component != null)
      return component.getBackground();
    else
      return null;
  }

  /**
   *
   */
  @Setter(key="Background Color")
  public void setBackground(Color color)
  {
    this.background = color;
    if (component != null)
    {
      if (background != null)
        component.setBackground(background);
      component.repaint();
    }
  }

  /**
   *
   */
  @Override
  protected JComponent createSwingComponent()
  {
    return new PanelPainter();
  }

  /**
   *
   */
  @Override
  public void configureVisualComponent() {
    if (background != null)
      component.setBackground(background);
    super.configureVisualComponent();
    setTabTitle();
    component.revalidate();
    component.repaint();
  }

}
