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
import control4j.scanner.Setter;
import control4j.scanner.Getter;
import control4j.scanner.Scanner;
import static control4j.tools.Logger.*;

/**
 *  Change color of gui component according to boolean interpretation of
 *  input signal. Color of component property may have tree values.
 */
public class Led extends Changer<Color>
{

  private Color trueColor = Color.green;
  private Color falseColor = trueColor.darker();
  private Color invalidColor = Color.yellow;

  @Getter(key="True Color")
  public Color getTrueColor()
  {
    return trueColor;
  }

  @Setter(key="True Color")
  public void setTrueColor(Color trueColor)
  {
    this.trueColor = trueColor;
  }

  @Getter(key="False Color")
  public Color getFalseColor()
  {
    return falseColor;
  }

  @Setter(key="False Color")
  public void setFalseColor(Color falseColor)
  {
    this.falseColor = falseColor;
  }

  @Getter(key="Invalid Color")
  public Color getInvalidColor()
  {
    return invalidColor;
  }

  @Setter(key="Invalid Color")
  public void setInvalidColor(Color invalidColor)
  {
    this.invalidColor = invalidColor;
  }

  @Override
  protected void update(Signal input)
  {
    Color color;
    if (!input.isValid())
      color = invalidColor;
    else
      color = input.getBoolean() ? trueColor : falseColor;
    setPropertyValue(color);
  }

  @Override
  public Class getPropertyClass()
  {
    return Color.class;
  }
  
  @Override
  public Object clone() throws CloneNotSupportedException
  {
    Led clone = (Led)super.clone();
    clone.trueColor = trueColor;
    clone.falseColor = falseColor;
    clone.invalidColor = invalidColor;
    return clone;
  }
}
