package control4j.modules.vector;

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
 *  Returns the number of elements of the input vector.
 */
public class PMSize extends ProcessModule
{

  /**
   *  Returns the number of elements of the input vector.
   *  Size of input array must be at least one, size of
   *  returned array will be the same as size of input
   *  array. Signal of ith position in returned array
   *  contains size of the vector on ith position in the 
   *  input array.
   *
   *  @param input
   *            size must be at least one. Array must not
   *            contain null.
   *
   *  @return array that is the same size as the input
   *            array. Element on ith position corresponds
   *            to the element on ith position in the input
   *            array. Element is invalid if and only if
   *            corresponding input is invalid. Timestamp
   *            of output is the same as the timestamp of
   *            corresponding input signal.
   */
  @Override
  public Signal[] process(Signal[] input)
  {
    int size = getNumberOfAssignedOutputs();
    //Signal[] result = new Signal[input.length];
    for (int i=0; i<size; i++)
    {
      if (input[i].isValid())
        input[i] = Signal.getSignal(input[i].getSize(), input[i].getTimestamp());
      else
        input[i] = Signal.getSignal(input[i].getTimestamp());

    }
    return input;
  }
}
