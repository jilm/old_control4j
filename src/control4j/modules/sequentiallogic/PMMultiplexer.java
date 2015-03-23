package control4j.modules.sequentiallogic;

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
 *  Selects one of several input signals and forwards the selected input
 *  to the output.
 */
@AVariableInput(startIndex=1)
public class PMMultiplexer extends ProcessModule
{

  /**
   *  Selects one of several input signals and forwards the selected input
   *  to the output.
   *
   *  <p>It accepts two or more inputs. The first input is select input,
   *  it is interpreted as a scalar integer value. The input with index
   *  i is forwarded (copied) to the output if and only if the select input
   *  is valid and its rounded value is i-1.
   *
   *  <p>The output is invalid if eather the selected input is invalid,
   *  or the select input is invalid, or value of select input is less
   *  than zero, or value of select input is greater than the number
   *  of inputs minus two.
   *
   *  <p>The output signal is eather the clone of the selected input
   *  or it is invalid signal with timestamp set to the current system
   *  time.
   *
   *  @param input
   *             an array of size at least two. The first element is
   *             interpreted as scalar integer.
   *
   *  @return an array of size one. Returned value is eather the clone
   *             of the selected input (together with timestamp and
   *             data type) or it is an invalid signal.
   */
  @Override
  public void process(
      Signal[] input, int inputLength, Signal[] output, int outputLength)
  {
    //Signal[] result = new Signal[1];
    int size = inputLength - 1;
    if (input[0].isValid())
    {
      int selector = (int)Math.round(input[0].getValue());
      if (selector < 0 || selector > size-1)
        output[0] = Signal.getSignal();
      else
        output[0] = (Signal)input[selector+1].clone();
    }
    else
    {
      output[0] = Signal.getSignal();
    }
  }
}
