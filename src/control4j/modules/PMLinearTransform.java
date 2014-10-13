package control4j.modules;

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

import control4j.Module;
import control4j.Signal;
import control4j.ProcessModule;
import control4j.IConfigBuffer;
import control4j.ConfigItem;

public class PMLinearTransform extends ProcessModule
{
  @ConfigItem(optional = true)
  public double mul = 1.0;

  @ConfigItem(optional = true)
  public double add = 0.0;

  private int size;

  @Override
  protected void initialize(IConfigBuffer configuration)
  {
    super.initialize(configuration);
    size = Math.min(getNumberOfAssignedInputs(), getNumberOfAssignedOutputs());
  }

  /**
   *  Calculates linear transformation of its inputs.
   */
  @Override
  public Signal[] process(Signal[] input)
  {
    for (int i=0; i<size; i++)
    {
      if (input[i].isValid())
      {
        double value = input[i].getValue() * mul + add;
        input[i] = Signal.getSignal(value, input[i].getTimestamp());
      }
      else
      {
        input[i] = Signal.getSignal(input[i].getTimestamp());
      }
    }
    return input;
  }
}
