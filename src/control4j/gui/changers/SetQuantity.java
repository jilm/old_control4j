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

import java.lang.reflect.Method;
import control4j.Signal;
import cz.lidinsky.tools.reflect.Getter;
import cz.lidinsky.tools.reflect.Setter;
import control4j.scanner.Scanner;
import control4j.gui.Changer;
import static control4j.tools.Logger.*;

/**
 *
 */
@control4j.annotations.AGuiObject(name="Set quantity", tags={"real number"})
public class SetQuantity extends Changer<Double>
{

  @Override
  protected void update(Signal input)
  {
    double value;
    if (input.isValid())
      value = input.getValue();
    else
      value = Double.NaN;
    setPropertyValue(new Double(value));
  }

  @Override
  public Class getPropertyClass()
  {
    return double.class;
  }

}
