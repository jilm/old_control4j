package control4j.modules.math;

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

public class PMSquareRoot extends ProcessModule
{
  @Override
  public Signal[] process(Signal[] input)
  {
    int size = getNumberOfAssignedOutputs();
    for (int i=0; i<size; i++)
    {
      if (input[i].isValid() && input[i].getValue() >= 0.0)
      {
        input[i] = Signal.getSignal(Math.sqrt(input[i].getValue()), input[i].getTimestamp());
      }
      else
      {
        input[i] = Signal.getSignal(input[i].getTimestamp());
      }
    }
    return input;
  }
}
