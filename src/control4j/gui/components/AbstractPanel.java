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
import java.awt.Component;
import control4j.scanner.Setter;
import control4j.scanner.Getter;

/**
 *
 */
public abstract class AbstractPanel extends ChangeablePanel
{

  @Override
  @Getter(key="Background")
  public Color getBackground()
  {
    return super.getBackground();
  }

  @Override
  @Setter(key="Background")
  public void setBackground(Color color)
  {
    super.setBackground(color);
  }

  @Override
  @Getter(key="X")
  public int getX()
  {
    return super.getX();
  }

  @Setter(key="X")
  public void setX(int x)
  {
    Rectangle bounds = getBounds();
    setBounds(x, bounds.y, bounds.width, bounds.height);
  }

  @Override
  @Getter(key="Y")
  public int getY()
  {
    return super.getY();
  }

  @Setter(key="Y")
  public void setY(int y)
  {
    Rectangle bounds = getBounds();
    setBounds(bounds.x, y, bounds.width, bounds.height);
  }


}
