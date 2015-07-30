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
 *  Changes integer property of its parent. Typicaly it change position or
 *  dimension of some component depends on the value from technology.
 */
@control4j.annotations.AGuiObject(name="Move", tags={"integer"})
public class Move extends Changer<Integer>
{

  private int min;
  private int max;

  @Getter("Min")
  public int getMin()
  {
    return min;
  }

  @Setter("Min")
  public void setMin(int min)
  {
    this.min = min;
  }

  @Getter("Max")
  public int getMax()
  {
    return max;
  }

  @Setter("Max")
  public void setMax(int max)
  {
    this.max = max;
  }

  @Override
  protected void update(Signal input)
  {
    if (input.isValid())
    {
      double value = input.getValue();
      if (value > 1.0) value = 1.0;
      if (value < 0.0) value = 0.0;
      int result = (int)Math.round((double)(max - min) * value + (double)min);
      setPropertyValue(Integer.valueOf(result));
    }
  }

  @Override
  public Class getPropertyClass()
  {
    return int.class;
  }

}
