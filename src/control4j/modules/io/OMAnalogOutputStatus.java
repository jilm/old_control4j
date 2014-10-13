package control4j.modules.io;

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
import control4j.Resource;
import control4j.OutputModule;
import control4j.resources.IAnalogOutput;

/**
 *  Provides information about the status of the analog output hardware.
 */
public class OMAnalogOutputStatus extends OutputModule
{

  /**
   *  Analog output hardware.
   */
  @Resource
  public IAnalogOutput hardware;

  /**
   *  Returns an array of signals that indicate status of the analog
   *  output hardware. Returned signals should be interpreted as
   *  boolean values. Signal has true value if everything is OK and
   *  false if some problem was detected.
   *
   *  <p>Size of returned array entirely depends on the number of
   *  channels of the output module hardware. Each signal indicates
   *  status of one channel.
   *
   *  <p>Returned signals are always valid and timestamp is set to
   *  the current system time.
   *
   *  @return an array of signals that indicates status of the analog 
   *          output hardware.
   */
  @Override
  public Signal[] get()
  {
    int size = hardware.getNumberOfAnalogOutputs();
    Signal[] result = new Signal[size];
    for (int i=0; i<size; i++)
    {
      int status = hardware.getAnalogOutputStatus(i);
      if (status == 0)
        result[i] = Signal.getSignal(true);
      else
        result[i] = Signal.getSignal(false);
    }
    return result;
  }

}
