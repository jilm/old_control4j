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
import control4j.resources.IThermometer;
import control4j.resources.ValueNotAvailableException;

/**
 *  Provides temperatures, which are measured by the thermometer.
 */
public class OMTemperature extends OutputModule
{
  private Signal[] result;

  /**
   *  Thermometer resource.
   */
  @Resource
  public IThermometer hardware;

  /**
   *  Initialize module, throws exception if number of assigned
   *  outputs exceeds number of channels, which are supported
   *  by the thermometer.
   *
   *  @param configuration
   *             not used, may be null
   *
   *  @throws UnsupportedNumberOfIOException
   *             if there are more assigned autputs than number
   *             of channels provided by the thermometer
   */
  @Override
  protected void initialize(IConfigBuffer configuration)
  {
    super.initialize(configuration);
    int channels = hardware.getNumberOfTemperatureChannels();
    if (channels < getNumberOfAssignedOutputs())
    {
      // throw exception !!!
      throw new UnsupportedNumberOfIOException();
    }
    result = new Signal[channels];
  }

  /**
   *  Returns array of measured temperatures. Size of returned
   *  is equal to number of channels, supported by the
   *  thermometer. Returned values are from the last measurement.
   *  It means that if there are some problems (in communication
   *  for instance), these values may be few cycles old.
   *
   *  @return an array of measured temperatures. If there is no
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
	double value = hardware.getTemperature(i);
	Date timestamp = hardware.getTemperatureTimestamp(i);
	String unit = hardware.getTemperatureUnit(i);
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
