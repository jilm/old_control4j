package control4j.modules.math;

/*
 *  Copyright 2013, 2015 Jiri Lidinsky
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

import control4j.Signal;
import control4j.ProcessModule;

/**
 *  Provides mathematical operation division.
 */
public class PMDiv extends ProcessModule
{

  /**
   *  Provides mathematical operation division. It expects two inputs,
   *  first one is used as a dividend and the second one as a divisor.
   *  It provides one output signal.
   *
   *  The output signal is invalid if one of the input signals is invalid,
   *  or if the divisor is zero.
   *
   *  Timestamp of the output signal is set to the system time.
   *
   *  @param input
   *             must be an array of size at least two. First two elements
   *             may not be null. Extra elements are ignored.
   *
   *  @return the input array where the first element contains the result
   *             of the division. Another elements should be ignored.
   */
  @Override
  public void process(
      Signal[] input, int inputLength, Signal[] output, int outputLength)
  {
    if (input[0].isValid() && input[1].isValid())
    {
      double result = input[0].getValue() / input[1].getValue();
      if (Double.isInfinite(result) || Double.isNaN(result))
        output[0] = Signal.getSignal();
      else
        output[0] = Signal.getSignal(result);
    }
    else
    {
      output[0] = Signal.getSignal();
    }
  }
}
