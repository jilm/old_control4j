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

import control4j.AVariableInput;
import control4j.Signal;
import control4j.ProcessModule;

/**
 *  Provides sum of the values on the input.
 */
public class PMSum extends ProcessModule
{

  /**
   *  Returns a sum of the input signal values.
   *
   *  <p>Provides only one output which is the sum of the input signal 
   *  values. Number of input signals must be at least one.
   *
   *  <p>If there is one or more invalid input signals, then the 
   *  output signal is invalid. Output signal is valid only if all
   *  of the input signals are valid.
   *
   *  <p>Timestamp of the output signal is set to the current system
   *  time.
   *
   *  @param input
   *             an array of size at least one. It may not contain
   *             <code>null</code> value
   *
   *  @return an array of size one. It contains the sum of the input
   *             signals values.
   */
  @Override @AVariableInput(startIndex=0)
  public void process(
      Signal[] input, int inputLength, Signal[] output, int outputLength)
  {
    double sum = 0.0;
    for (int i=0; i<inputLength; i++)
      if (input[i].isValid())
        sum += input[i].getValue();
      else
      {
        output[0] = Signal.getSignal();
        return;
      }
    output[0] = Signal.getSignal(sum);
  }
}
