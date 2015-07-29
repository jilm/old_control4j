package control4j.gui;

/*
 *  Copyright 2013, 2014, 2015 Jiri Lidinsky
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

import static cz.lidinsky.tools.Validate.notNull;

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;
import cz.lidinsky.tools.reflect.ObjectMapUtils;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.Color;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

public class ColorParser {

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

  public static Transformer<Pair<Object, AccessibleObject>, Closure<String>>
  string2ColorClosureFactory(final boolean setAccessible) {

    return new Transformer<Pair<Object, AccessibleObject>, Closure<String>>() {

      public Closure<String> transform(
          final Pair<Object, AccessibleObject> parameter) {

        final Object object = notNull(parameter.getLeft());
        final AccessibleObject member = notNull(parameter.getRight());
        Class dataType = ObjectMapUtils.getValueDataType(member);
        if (Color.class.isAssignableFrom(dataType)) {

          return new Closure<String>() {

            public void execute(String value) {
              ObjectMapUtils.set(
                  object, member, getColor(value), setAccessible);
            }
          };

        } else {
          throw new CommonException()
            .setCode(ExceptionCode.UNSUPPORTED_TYPE)
            .set("message",
                "This setter closure is dedicated only for color members")
            .set("data type", dataType)
            .set("object", object)
            .set("member", member)
            .set("accessibility", setAccessible);
        }
      }
    };

  }
}
