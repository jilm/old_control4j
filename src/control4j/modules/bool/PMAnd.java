package control4j.modules.bool;

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

/**
 *  Provide boolean AND operation on the input.
 */
public class PMAnd extends ProcessModule
{

  /**
   *  Perform a logical AND operation on the input and return
   *  result. It expects one or more input signals and provides
   *  exactly one output signal. Value of output is boolean
   *  value. Output signal is invalid if and only if at least
   *  one of the input signals is invalid. Timestamp of the
   *  output corresponds to the actual system time.
   *
   *  @param input an array of size at least one. It shall not
   *         contain null value.
   *  @return an array of size one. It contains Signal whose
   *         value is logical AND on input signals. Returned
   *         signal is invalid if at least one of the input
   *         signals is invalid. Timestamp corresponds to the
   *         system time in moment of module invocation.
   */
  public Signal[] process(Signal[] input)
  {
    int size = getNumberOfAssignedInputs();
    if (!input[0].isValid())
      return new Signal[] {Signal.getSignal()};
    boolean result = input[0].getBoolean();
    for (int i=1; i<size; i++)
    {
      if (!input[i].isValid())
        return new Signal[] {Signal.getSignal()};
      result = result && input[i].getBoolean();
    }
    return new Signal[] {Signal.getSignal(result)};
  }
}
