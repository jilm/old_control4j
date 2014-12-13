package control4j.modules;

/*
 *  Copyright 2013, 2014 Jiri Lidinsky
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

import control4j.OutputModule;
import control4j.Resource;
import control4j.Signal;
import control4j.resources.communication.SignalClient;

public class OMNetClientReceiver extends OutputModule
{

  @Resource
  public SignalClient channel;

  public Signal[] get()
  {
    Signal[] output = channel.read();
    if (output == null)
    {
      output = new Signal[getNumberOfAssignedOutputs()];
      for (int i=0; i<output.length; i++)
        output[i] = Signal.getSignal();
    }
    return output;
  }
  
}
