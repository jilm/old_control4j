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
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.JComponent;
import javax.swing.JPanel;
import control4j.scanner.Setter;
import control4j.scanner.Getter;
import control4j.gui.VisualContainer;

/**
 *
 */
@control4j.annotations.AGuiObject(name="Titled panel")
public class TitledPanel extends Panel
{

  private String title;

  public TitledPanel()
  {
    super();
  }

  @Setter(key="Title")
  public void setTitle(String title)
  {
    this.title = title;
    if (component != null)
    {
      ((TitledBorder)component.getBorder()).setTitle(title);
      component.repaint();
    }
  }

  @Getter(key="Title")
  public String getTitle()
  {
    return title;
  }

  /**
   *
   */
  @Override
  public void configureVisualComponent()
  {
    component.setBorder(BorderFactory.createTitledBorder(title));
    super.configureVisualComponent();
  }

}
