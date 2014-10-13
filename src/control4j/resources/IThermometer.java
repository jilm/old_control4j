package control4j.resources;

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

/**
 *  Interface to harware thermometer. The hardware may provide more than
 *  one temperature, so the thermometers are distinguished by zero based
 *  index. The temperature may not be available, becouse of hardware
 *  failure for instance, so the application should call getTemperatureStatus
 *  before it gets themperature itself.
 */
public interface IThermometer
{
  /**
   *  Returns the measured temperature.
   *
   *  @param index
   *             zero based index used to distinguish channels when
   *             the module supports more than one.
   *  @return measured temperature
   *
   *  @throws ValueNotAvailableException
   *             if the value is not available for some reason, the
   *             hardware failure for instance
   */
  double getTemperature(int index) throws ValueNotAvailableException;

  /**
   *  Returns the string representation of the temperature unit.
   *
   *  @param index
   *             zero based index used to distinguish channels when
   *             the module supports more than one.
   *  @return temperature unit
   *
   *  @throws ValueNotAvailableException
   *             if the information is not available for some reason, the
   *             hardware failure for instance
   */
  String getTemperatureUnit(int index) throws ValueNotAvailableException;

  /**
   *  Returns status information. 
   *
   *  @param index
   *             zero based index used to distinguish channels when
   *             the module supports more than one.
   *  @return status information
   */
  int getTemperatureStatus(int index);

  /**
   *  Returns the measurement date and time.
   *
   *  @param index
   *             zero based index used to distinguish channels when
   *             the module supports more than one.
   *  @return the measurement date and time
   *
   *  @throws ValueNotAvailableException
   *             if the measurement could not be done for some reason,
   *             the hardware failure for instance
   */
  Date getTemperatureTimestamp(int index) throws ValueNotAvailableException;

  /**
   *  Returns the number of channels, which is supported by the
   *  thermometer.
   *
   *  @return number of channels, which is supported
   */
  int getNumberOfTemperatureChannels();
}
