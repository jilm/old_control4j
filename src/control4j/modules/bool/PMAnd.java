package control4j.modules.bool;

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

import control4j.AVariableInput;
import control4j.Signal;
import control4j.ProcessModule;

/**
 *
 *  Returns a result of the boolean AND operation on its input signals.  It
 *  expects one or more input signals and provides exactly one output signal.
 *
 *  Input: variable, scalar boolean, it expects at least one input scalar
 *  boolean value.
 *
 *  Output: 0, scalar boolean, Output signal gets a valid true value if and
 *  only if all of the input signals contain valid true values. If at least one
 *  input signal contains valid false value, then the output contains valid
 *  false value. Otherwise the output contains invalid value. Timestamp of the
 *  output corresponds to the actual system time.
 *
 */
@AVariableInput
public class PMAnd extends ProcessModule
{

  /**
   *  Performs a logical AND operation of input signals and returns
   *  the result. It expects one or more input signals and provides
   *  exactly one output signal. Value of output should be treated
   *  as a boolean value.
   *
   *  <p>Output signal contains a valid true value if and only if
   *  all of the input signals contain valid true values. If at least
   *  one input signal contains valid false value, then the output
   *  contains valid false value. Otherwise the output contains
   *  invalid value. Timestamp of the output corresponds to the
   *  actual system time.
   *
   *  @param input an array of size at least one. It shall not
   *            contain null value.
   *
   *  @return an array of size one. It contains Signal whose value
   *            is logical AND on the input signals. Returned signal
   *            is valid as long as it is possible to infer the
   *            output value. Timestamp corresponds to the system
   *            time in moment of module invocation.
   */
  @Override
  public void process(
      Signal[] input, int inputLength, Signal[] output, int outputLength)
  {
    boolean valid = true;
    for (int i=0; i<inputLength; i++)
      if (input[i].isValid() && !input[i].getBoolean())
      {
        output[0] = Signal.getSignal(false);
        return;
      }
      else if (!input[i].isValid())
        valid = false;

    if (valid)
      output[0] = Signal.getSignal(true);
    else
      output[0] = Signal.getSignal();
  }

}
