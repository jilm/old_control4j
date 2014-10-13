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

import java.util.Date;
import control4j.Signal;
import control4j.Resource;
import control4j.OutputModule;
import control4j.IConfigBuffer;
import control4j.UnsupportedNumberOfIOException;
import control4j.resources.IBinaryInput;
import control4j.resources.ValueNotAvailableException;

/**
 *  Provides values from a binary input hardware module.
 */
public class OMBinaryInput extends OutputModule
{
  private Signal[] result;

  /**
   *  Binary input resource.
   */
  @Resource
  public IBinaryInput hardware;

  /**
   *  Initialize module, throws exception if number of assigned
   *  outputs exceeds number of channels, which are supported
   *  by the hardware.
   *
   *  @param configuration
   *             not used, may be null
   *  @throws UnsupportedNumberOfIOException
   *             if there are more assigned outputs than number
   *             of channels provided by the hardware
   */
  @Override
  protected void initialize(IConfigBuffer configuration)
  {
    super.initialize(configuration);
    int channels = hardware.getNumberOfBinaryInputs();
    if (channels < getNumberOfAssignedOutputs())
    {
      throw new UnsupportedNumberOfIOException();
    }
    result = new Signal[channels];
  }

  /**
   *  Returns array of binary input values. Size of returned array
   *  is equal to number of channels, supported by the
   *  hardware module. Returned values are from the last measurement.
   *  It means that if there are some problems (in communication
   *  for instance), these values may be few cycles old.
   *
   *  @return an array of binary input values. If there is no
   *          new data since the last measurement, invalid signals
   *          are returned.
   */
  @Override
  public Signal[] get()
  {
    for (int i=0; i<result.length; i++)
    {
      try
      {
	boolean value = hardware.getBinaryInput(i);
	Date timestamp = hardware.getBinaryInputTimestamp(i);
        result[i] = Signal.getSignal(value, timestamp);
      }
      catch(ValueNotAvailableException e)
      {
        result[i] = Signal.getSignal();
      }
    }
    return result;
  }
}
