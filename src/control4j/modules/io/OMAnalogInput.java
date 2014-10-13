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
import control4j.resources.IAnalogInput;
import control4j.resources.ValueNotAvailableException;

/**
 *  Provides values that are measured by analog / digital converter.
 */
public class OMAnalogInput extends OutputModule
{
  private Signal[] result;

  /**
   *  Thermometer resource.
   */
  @Resource
  public IAnalogInput hardware;

  /**
   *  Initialize module, throws exception if number of assigned
   *  outputs exceeds number of channels, which are supported
   *  by the analog / digital converter.
   *
   *  @param configuration
   *             not used, may be null
   *  @throws UnsupportedNumberOfIOException
   *             if there are more assigned autputs than number
   *             of channels provided by the AD converter
   */
  @Override
  protected void initialize(IConfigBuffer configuration)
  {
    super.initialize(configuration);
    int channels = hardware.getNumberOfAnalogInputChannels();
    if (channels < getNumberOfAssignedOutputs())
    {
      throw new UnsupportedNumberOfIOException();
    }
    result = new Signal[channels];
  }

  /**
   *  Returns array of measured values. Size of returned array
   *  is equal to number of channels, supported by the
   *  AD converted. Returned values are from the last measurement.
   *  It means that if there are some problems (in communication
   *  for instance), these values may be few cycles old.
   *
   *  @return an array of measured values. If there is no
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
	double value = hardware.getAnalogInput(i);
	Date timestamp = hardware.getAnalogInputTimestamp(i);
	String unit = hardware.getAnalogInputUnit(i);
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
