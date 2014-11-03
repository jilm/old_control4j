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
 *
 */
public class SetText extends Changer<String>
{

  private String text;

  @Override
  protected void update(Signal input)
  {
    if (input.isValid())
    {
      boolean value = input.getBoolean();
      if (value)
      {
        setPropertyValue(text);
      }
    }
  }

  @Override
  public Class getPropertyClass()
  {
    return String.class;
  }

  @Getter(key="Text")
  public String getText()
  {
    return text;
  }

  @Setter(key="Text")
  public void setText(String text)
  {
    this.text = text;
  }

}
