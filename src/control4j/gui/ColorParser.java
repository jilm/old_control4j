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

import java.awt.Color;
import java.lang.reflect.Field;

public class ColorParser
{

  /**
   *  Return colors for GUI.
   */
  public static Color getColor(String key)
  {
    // color from name
    try
    {
      Field[] colorFields = Color.class.getFields();
      for (Field field : colorFields)
      {
        if (field.getName().equals(key) && field.getType() == Color.class)
	{
	  return (Color)field.get(null);
	}
      }
    }
    catch (Exception e) { };

    // color from decimal number
    try
    {
      return Color.decode(key);
    }
    catch (NumberFormatException e) { };

    return null;
  }

}
