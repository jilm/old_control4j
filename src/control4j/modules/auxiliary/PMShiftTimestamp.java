package control4j.modules.auxiliary;

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

import control4j.Module;
import control4j.Signal;
import control4j.ProcessModule;

import java.util.Date;

/**
 *  Shifts the timestamp of input signals by the given interval, whereas
 *  values stay unchanged.
 */
public class PMShiftTimestamp extends ProcessModule
{

  /**
   *  Shifts the timestamp of input signals by the given interval.
   *
   *  <p>First input (with index zero) is interpreted as an interval in
   *  milliseconds. This interval is added to the timestamp of all of the
   *  subsequent input signals. If the interval is greater than zero,
   *  timestamp is shifted to the future, if it is smaller than zero,
   *  the timestamp is shifted to the past.
   *
   *  <p>If interval signal is invalid, timestamp of input signals stays
   *  unchanged.
   *
   *  <p>An output is invalid if and only if corresponding input signal
   *  is invalid.
   *
   *  @param input
   *             an array of at least two elements. It may not be null.
   *             The first one is interpreted to be an interval which
   *             is added to the timestamp of all of the subsequent
   *             elements.
   *
   *  @return an array of size that is equal to the size of input array
   *             minus one.
   */
  public Signal[] process(Signal[] input)
  {
    int size = getNumberOfAssignedOutputs();
    Signal[] result = new Signal[size];
    if (input[0].isValid())
    {
      long interval = Math.round(input[0].getValue());
      for (int i=0; i<size; i++)
      {
        long timestamp = input[i+1].getTimestamp().getTime();
	timestamp += interval;
	result[i] = input[i+1].clone(new Date(timestamp));
      }
    }
    else
    {
      for (int i=0; i<size; i++)
        result[i] = (Signal)input[i+1].clone();
    }
    return result;
  }

}
