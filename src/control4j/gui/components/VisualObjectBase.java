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
import java.awt.Insets;
import control4j.gui.VisualObject;
import control4j.scanner.Setter;
import control4j.scanner.Getter;

/**
 *
 *  Define some properties that are common to most of the visual objects.
 *
 */
public abstract class VisualObjectBase extends VisualObject
{

  private int x;
  private int y;
  private Color foreground = Color.blue;
  private Color background = Color.gray;
  private boolean isOpaque;
  private boolean isVisible = true;

  @Getter(key="X")
  public int getX()
  {
    return x;
  }

  @Setter(key="X")
  public void setX(int x)
  {
    this.x = x;
    if (component != null)
    {
      Insets insets 
	  = ((VisualObject)getParent()).getVisualComponent().getInsets();
      component.setLocation(x + insets.left, y + insets.top);
      component.revalidate();
      component.repaint();
    }
  }

  @Getter(key="Y")
  public int getY()
  {
    return y;
  }

  @Setter(key="Y")
  public void setY(int y)
  {
    this.y = y;
    if (component != null)
    {
      Insets insets 
	  = ((VisualObject)getParent()).getVisualComponent().getInsets();
      component.setLocation(x + insets.left, y + insets.top);
      component.revalidate();
      component.repaint();
    }
  }

  @Getter(key="Foreground Color")
  public Color getForeground()
  {
    return foreground;
  }

  @Setter(key="Foreground Color")
  public void setForeground(Color color)
  {
    foreground = color;
    if (component != null)
    {
      component.setForeground(foreground);
      component.repaint();
    }
  }

  @Getter(key="Background Color")
  public Color getBackground()
  {
    return background;
  }

  @Setter(key="Background Color")
  public void setBackground(Color color)
  {
    background = color;
    if (component != null)
    {
      component.setBackground(background);
      component.repaint();
    }
  }

  @Getter(key="Is Opaque")
  public boolean isOpaque()
  {
    return isOpaque;
  }

  @Setter(key="Is Opaque")
  public void setOpaque(boolean isOpaque)
  {
    this.isOpaque = isOpaque;
    if (component != null)
    {
      component.setOpaque(isOpaque);
      component.repaint();
    }
  }

  @Getter(key="Is Visible")
  public boolean isVisible()
  {
    return isVisible;
  }

  @Setter(key="Is Visible")
  public void setVisibility(boolean isVisible)
  {
    this.isVisible = isVisible;
    if (component != null)
    {
      component.setVisible(isVisible);
      component.repaint();
    }
  }

  @Override
  protected void configureVisualComponent()
  {
    Insets insets 
        = ((VisualObject)getParent()).getVisualComponent().getInsets();
    component.setLocation(x + insets.left, y + insets.top);
    component.setOpaque(isOpaque);
    component.setBackground(background);
    component.setForeground(foreground);
    component.setVisible(isVisible);
  }

}
