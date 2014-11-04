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

import control4j.scanner.Getter;
import control4j.scanner.Setter;
import control4j.ConfigItem;
import control4j.Signal;
import control4j.SignalFormat;
import control4j.modules.IGuiUpdateListener;
import control4j.tools.Preferences;
import control4j.gui.ColorParser;
import control4j.gui.SignalAssignment;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.AbstractButton;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

/**
 *
 *  Provides access to properties that are common to all of the
 *  components.
 *
 */
public abstract class AbstractComponent extends ChangeableComponent
implements control4j.gui.IComponentName
{

  /**
   *  A name of the component which was entered by the user.
   *  If it was not entered yet, appropriate getter method returns
   *  default name.
   */
  private String name = null;

  /**
   *
   */
  @Setter(key="Name")
  public void setName(String name)
  {
    this.name = name;
  }

  /**
   *
   */
  @Getter(key="Name")
  public String getName()
  {
    if (name == null || name.length() == 0)
      return getDefaultName();
    else
      return name;
  }

  /**
   *  Returns a name in the form: ClassName_Number.
   */
  protected String getDefaultName()
  {
    return getClass().getSimpleName() + String.valueOf(getCounter());
  }

  /**
   *
   */
  protected abstract int getCounter();

  @Getter(key="X") @Override
  public int getX()
  {
    return super.getX();
  }

  @Setter(key="X")
  public void setX(int x)
  {
    Rectangle rect = getBounds();
    rect.x = x;
    setBounds(rect);
  }

  @Getter(key="Y") @Override
  public int getY()
  {
    return super.getY();
  }

  @Setter(key="Y")
  public void setY(int y)
  {
    Rectangle rect = getBounds();
    rect.y = y;
    setBounds(rect);
  }
  
  @Getter(key="Foreground Color") @Override
  public Color getForeground()
  {
    return super.getForeground();
  }

  @Setter(key="Foreground Color") @Override
  public void setForeground(Color color)
  {
    super.setForeground(color);
  }

  @Getter(key="Background Color") @Override
  public Color getBackground()
  {
    return super.getBackground();
  }

  @Setter(key="Background Color") @Override
  public void setBackground(Color color)
  {
    super.setBackground(color);
  }

}
