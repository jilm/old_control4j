package control4j.modules.comparison;

/*
 *  Copyright 2013 Jiri Lidinsky
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
 *  Compares two signals and returns boolean true value if and only
 *  if values of these two signals are equal.
 */
public class PMEqual extends ProcessModule
{
  /**
   *  Compares two signals and returns boolean true value if and only
   *  if values of these two signals are equal.
   *
   *  <p>It expects two or more input signals. First signal (with index
   *  zero) is called reference signal. Value of the second signal and values
   *  of all subsequent signals are compared with the reference signal.
   *  It returns one signal for each comparison result, thus the number of
   *  output signals is equal to the number of input signals minus one.
   *  Output signal has boolean true value if values of two compared signals
   *  are equal. Otherwise it returns false value.
   *
   *  <p>Output signal is invalid if eather of the two compared signals
   *  is invalid.
   *
   *  <p>Timestamp of the returned signal is equal to the timestamp of the
   *  other of the two compared signals. First one is considered to be
   *  the reference signal.
   *
   *  @param input
   *             an array of size at least two. Elements may not be null
   *             values
   *
   *  @return an array of size that is equal to the size of input array
   *             minus one. Returned signals are boolean signals.
   */
  @Override
  protected Signal[] process(Signal[] input)
  {
    int size = getNumberOfAssignedOutputs();
    //Signal[] result = new Signal[input.length-1];
    if (input[0].isValid())
    {
      double reference = input[0].getValue();
      for (int i=0; i<size; i++)
        if (input[i+1].isValid())
        {
          input[i] = Signal.getSignal(reference == input[i+1].getValue(), input[i+1].getTimestamp());
        }
        else
        {
          input[i] = Signal.getSignal(input[i+1].getTimestamp());
        }
    }
    else
    {
      for (int i=0; i<size; i++)
        input[i] = Signal.getSignal(input[i+1].getTimestamp());
    }
    return input;
  }
}
