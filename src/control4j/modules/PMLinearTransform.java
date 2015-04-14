package control4j.modules;

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

import control4j.AMinInput;
import control4j.AMaxInput;
import control4j.Signal;
import control4j.ProcessModule;
import control4j.ConfigItem;

@AMinInput(1)
@AMaxInput(1)
public class PMLinearTransform extends ProcessModule
{
  @ConfigItem(optional = true)
  public double mul = 1.0;

  @ConfigItem(optional = true)
  public double add = 0.0;

  /**
   *  Calculates linear transformation of its input.
   */
  @Override
  public void process(
      Signal[] input, int inputLength, Signal[] output, int outputLength)
  {
    if (inputLength > 0 && outputLength > 0)
    {
      if (input[0].isValid())
      {
        double value = input[0].getValue() * mul + add;
        output[0] = Signal.getSignal(value, input[0].getTimestamp());
      }
      else
      {
        output[0] = Signal.getSignal(input[0].getTimestamp());
      }
    }
  }

}
