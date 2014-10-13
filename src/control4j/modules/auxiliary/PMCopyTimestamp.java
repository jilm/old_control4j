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

public class PMCopyTimestamp extends ProcessModule
{

  public Signal[] process(Signal[] input)
  {
    Date reference = input[0].getTimestamp();
    int size = getNumberOfAssignedInputs() - 1;
    Signal[] result = new Signal[size];
    for (int i=0; i<size; i++)
      result[i] = input[i+1].clone(reference);
    return result;
  }

}
