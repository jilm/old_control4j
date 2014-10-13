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
 *  Interface to the harware that measure analog values. The hardware may 
 *  provide more than one input channel. Channels are distinguished by
 *  zero based index. The temperature may not be available, becouse of 
 *  some hardware failure in such a case status method may be called to
 *  get informed about the reason of the problem.
 */
public interface IAnalogInput
{
  /**
   *  Returns the measured value.
   *
   *  @param index
   *             zero based index used to distinguish input channel
   *  @return measured value
   *
   *  @throws ValueNotAvailableException
   *             if the value is not available for some reason, the
   *             hardware failure for instance
   */
  double getAnalogInput(int index) throws ValueNotAvailableException;

  /**
   *  Returns the string representation of the unit
   *
   *  @param index
   *             zero based index used to distinguish input channel
   *  @return value unit
   *
   *  @throws ValueNotAvailableException
   *             if the information is not available for some reason, the
   *             hardware failure for instance
   */
  String getAnalogInputUnit(int index) throws ValueNotAvailableException;

  /**
   *  Returns status information. 
   *
   *  @param index
   *             zero based index used to distinguish input channel
   *  @return status information. Returns zero if everything is OK,
   *             and non zero value otherwise.
   */
  int getAnalogInputStatus(int index);

  /**
   *  Returns the date and time of the measurement.
   *
   *  @param index
   *             zero based index used to distinguish input channel
   *  @return the measurement date and time
   *
   *  @throws ValueNotAvailableException
   *             if the measurement could not be done for some reason,
   *             the hardware failure for instance
   */
  Date getAnalogInputTimestamp(int index) throws ValueNotAvailableException;

  /**
   *  Returns the number of channels, which is supported by the
   *  hw module.
   *
   *  @return number of supported channels
   */
  int getNumberOfAnalogInputChannels();
}
