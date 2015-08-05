package control4j.gui.changers;

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
import java.lang.reflect.Method;
import control4j.Signal;
import cz.lidinsky.tools.reflect.Getter;
import cz.lidinsky.tools.reflect.Setter;
import control4j.scanner.Scanner;
import control4j.gui.Changer;
import static control4j.tools.Logger.*;

/**
 *  Change color of gui component according to boolean interpretation of
 *  input signal. Color of component property may have tree values.
 */
@control4j.annotations.AGuiObject(name="Set color", tags={"color"})
public class SetColor extends Changer<Color>
{

  private Color color = Color.green;

  @Getter("Color")
  public Color getColor()
  {
    return color;
  }

  @Setter("Color")
  public void setColor(Color color)
  {
    this.color = color;
  }

  @Override
  public void update(Signal input)
  {
    if (input.isValid() && input.getBoolean())
      setPropertyValue(color);
  }

  @Override
  public Class getPropertyClass()
  {
    return Color.class;
  }

}
