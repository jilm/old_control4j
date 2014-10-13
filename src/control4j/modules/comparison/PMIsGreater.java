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

public class PMIsGreater extends ProcessModule
{

  @Override 
  public Signal[] process(Signal[] input)
  {
    int size = getNumberOfAssignedOutputs();
    if (input[0].isValid())
    {
      double reference = input[0].getValue();
      for (int i=0; i<size; i++)
        if (input[i+1].isValid())
	  input[i] = Signal.getSignal(reference < input[i+1].getValue());
        else
	  input[i] = Signal.getSignal();
    }
    else
      for (int i=0; i<size; i++)
        input[i] = Signal.getSignal();
    return input;
  }

}
