package control4j.modules;

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

import control4j.AMinInput;
import control4j.Signal;
import control4j.ProcessModule;

/**
 *  Returns a minimal and a maximal value of all the input signals. 
 *  Moreover it returns index of input with minimal and maximal value.
 */
@AMinInput(2)
public class PMExtreme extends ProcessModule
{

  /**
   *  Returns minimal and maximal value of all input signals. It takes
   *  any number of input signals, but the number of inputs should be at least
   *  two. It returns the array of size four and the meanig of the output is:
   *  <ol>
   *    <li>Max value
   *    <li>Index of the input signal with max value
   *    <li>Min value
   *    <li>Index of the input signal with min value
   *  </ol>
   *  If there are two or more input signals with min or max value, it returns
   *  the index of the first (lowest index) signal. Timestamp of all the output
   *  signals is set to the current system time. The output signals are invalid
   *  if and only if there is no valid input signal.
   *
   *  @param input 
   *             array of any size. It must not contain <code>null</code>
   *             value.
   *
   *  @return an array of size four. It contains max value, index of input 
   *             with max value, min value, index of input with min value.
   */
  @Override 
  public void process(
      Signal[] input, int inputLength, Signal[] output, int outputLength)
  {
    double max = Double.MIN_VALUE;
    double min = Double.MAX_VALUE;
    int maxIndex = 0;
    int minIndex = 0;
    boolean valid = false;

    for (int i=0; i<inputLength; i++)
      if (input[i].isValid())
      {
        if (valid)
        {
          if (input[i].getValue() > max)
          {
            max = input[i].getValue();
            maxIndex = i;
          }
          if (input[i].getValue() < min)
          {
            min = input[i].getValue();
            minIndex = i;
          }
        }
        else
        {
          max = input[i].getValue();
          min = input[i].getValue();
          maxIndex = i;
          minIndex = i;
          valid = true;
        }
      }

    if (valid)
    {
      output[0] = Signal.getSignal(max);
      output[1] = Signal.getSignal(maxIndex);
      output[2] = Signal.getSignal(min);
      output[3] = Signal.getSignal(minIndex);
    }
    else
    {
      output[0] = Signal.getSignal();
      output[1] = Signal.getSignal();
      output[2] = Signal.getSignal();
      output[3] = Signal.getSignal();
    }
  }

}
