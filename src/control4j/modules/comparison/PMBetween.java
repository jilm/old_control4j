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
 *  Compares three signals and returns true only if the third signal 
 *  is greater than the first one and smaller than the second one.
 */
public class PMBetween extends ProcessModule
{
  /**
   *  Compares three signals and returns true only if the third signal
   *  is greater than the first one and smaller than the second one.
   *
   *  <p>It expects the input to be an array of size at least three 
   *  elements. The third and each subsequent element is compared 
   *  with the first two elements. If it is greater or equal to the 
   *  first one and simoultaneously if it is smaller or equal to the 
   *  second one, the appropriate output signal is true. Otherwise the 
   *  output signal is false. The size of the returned array is
   *  equal to the size of the input array minus two. The first output
   *  element is the result of comparison of the third input element,
   *  the second output element is the result of comparison of the
   *  fourth input element, etc.
   *
   *  <p>If the first or the second input element is invalid, all
   *  of the output elements are invalid. If the third or any subsequent
   *  element is invalid, only the corresponding output element signal
   *  is invalid.
   *  
   *  <p>Timestamp of the output signal is set to the timestamp
   *  of corresponding input signal.
   *
   *  @param input
   *             must be an array of size at least three elements.
   *
   *  @return an array of size that is equat to the size of input
   *             array minus two
   */
  @Override
  public Signal[] process(Signal[] input)
  {
    int size = getNumberOfAssignedOutputs();
    if (input[0].isValid() && input[1].isValid())
    {
      double min = input[0].getValue();
      double max = input[1].getValue();
      for (int i=0; i<size; i++)
        if (input[i+2].isValid())
	{
	  double valueToBeCompared = input[i+2].getValue();
	  boolean compResult = valueToBeCompared >= min
	    && valueToBeCompared <= max;
	  input[i] = Signal.getSignal(compResult, input[i+2].getTimestamp());
	}
	else
	{
	  input[i] = Signal.getSignal(input[i+2].getTimestamp());
	}
    }
    else
    {
      for (int i=0; i<size; i++)
        input[i] = Signal.getSignal(input[i+2].getTimestamp());
    }
    return input;
  }
}
