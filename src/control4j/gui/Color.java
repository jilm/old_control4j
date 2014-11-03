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

import control4j.tools.Preferences;

public class Color extends java.awt.Color
{

  /**
   *  Kategory of the color. May be null, in such a case
   *  it is considered that the color was chousen as custom
   *  and must be content of the property color.
   */
  private String key;

  private Color(String key, int color)
  {
    super(color);
    this.key = key;
  }

  public static Color getColor(String key)
  {
    String strColor = Preferences.getInstance().getColor(key);
    java.awt.Color color = ColorParser.getColor(strColor);      
    return new Color(key, color.getRGB());
  }

  /**
   *  @return alias or null
   */
  public String getKey()
  {
    return key;
  }

}
