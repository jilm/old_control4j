package control4j.modules;

/*
 *  Copyright 2015 Jiri Lidinsky
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
import control4j.ConfigItem;
import control4j.ProcessModule;
import control4j.Signal;

@AMinInput(1)
@AMaxInput(1)
public class PMSaturation extends ProcessModule
{

  @ConfigItem(key="min-limit", optional=true)
  public double minLimit = Double.NEGATIVE_INFINITY;

  @ConfigItem(key="max-limit", optional=true)
  public double maxLimit = Double.POSITIVE_INFINITY;

  /**
   *  Doesn't allow the input signal to exceed given limits. It takes one 
   *  input and provides tree outputs. The first output is always between
   *  given limits. If the input is lower then min-limit, than the first
   *  output is min-limit and second output is true. If the input is
   *  greater than max-limit than the first output is max-limit and the
   *  threed output is true. If the input is between limits than the
   *  output is equal to the input and the second and threed outputs are
   *  false. If the input is invalid than all of the outputs are invalid.
   */
  public void process(
      Signal[] input, int inputLength, Signal[] output, int outputLength)
  {
    if (input[0].isValid())
    {
      if (input[0].getValue() > maxLimit)
      {
        output[0] = Signal.getSignal(maxLimit, input[0].getTimestamp());
        output[1] = Signal.getSignal(false, input[0].getTimestamp());
        output[2] = Signal.getSignal(true, input[0].getTimestamp());
      }
      else if (input[0].getValue() < minLimit)
      {
        output[0] = Signal.getSignal(minLimit, input[0].getTimestamp());
        output[1] = Signal.getSignal(true, input[0].getTimestamp());
        output[2] = Signal.getSignal(false, input[0].getTimestamp());
      }
      else
      {
        output[0] = Signal.getSignal(
            input[0].getValue(), input[0].getTimestamp());
        output[1] = Signal.getSignal(false, input[0].getTimestamp());
        output[2] = Signal.getSignal(false, input[0].getTimestamp());
      }
    }
    else
    {
      for (int i=0; i<3; i++)
        output[i] = Signal.getSignal();
    }
  }
}
