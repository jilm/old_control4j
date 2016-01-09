package control4j.modules.bool;

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

import control4j.AVariableInput;
import control4j.Signal;
import control4j.ProcessModule;

/**
 *
 *  Provides boolean OR operation.
 *
 */
@AVariableInput
public class PMOr extends ProcessModule
{

  /**
   *  Performs a logical OR operation on the input and returns
   *  result. It expects one or more input signals and provides
   *  exactly one output signal. Value of output should be treated
   *  as a boolean value.
   *
   *  <p>There is a valid false value on the output if only if all
   *  of the input signals are valid false values. There is a valid
   *  true value on the output if there is at least one valid true
   *  input signal. Otherwise the output signal is invalid. 
   *  Timestamp of the output corresponds to the actual system time.
   *
   *  @param input an array of size at least one. It shall not
   *             contain null value.
   *
   *  @return an array of size one. It contains Signal whose value 
   *             is logical OR on input signals. Returned signal is 
   *             valid as long as it is possible to infer output.
   *             Timestamp corresponds to the system time in the
   *             moment of module invocation.
   */
  @Override @AVariableInput(startIndex=0)
  public void process(
      Signal[] input, int inputLength, Signal[] output, int outputLength)
  {
    boolean valid = true;
    for (int i=0; i<inputLength; i++)
      if (input[i].isValid() && input[i].getBoolean())
      {
	output[0] = Signal.getSignal(true);
        return;
      }
      else if (!input[i].isValid())
        valid = false;

    if (valid)
      output[0] = Signal.getSignal(false);
    else
      output[0] = Signal.getSignal();
  }

}
