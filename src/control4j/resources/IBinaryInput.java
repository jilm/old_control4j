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
 *  Interface to the harware that provides binary inputs. One hw module
 *  may of course provide more than one input. Inputs are distinguished
 *  by zero based index. The input, or inputs may not be available due 
 *  to the hardware failure for example, in such a case status method
 *  may be used to get information about what is going on.
 */
public interface IBinaryInput
{
  /**
   *  Returns status of input with index index.
   *
   *  @param index
   *             zero based index used to distinguish the input
   *  @return true if the input is active (the circuit is closed)
   *             false if the input is not active (the circuit is
   *             opened
   *  @throws ValueNotAvailableException
   *             if the value is not available for some reason, the
   *             hardware failure for instance. More information
   *             about the reason may provide status method
   */
  boolean getBinaryInput(int index) throws ValueNotAvailableException;

  /**
   *  Returns status information for the input with index index. 
   *  If everything is OK it returns zero. If there is some problem
   *  it returns non zero value.
   *
   *  @param index
   *             zero based index used to distinguish inputs
   *  @return status information
   */
  int getBinaryInputStatus(int index);

  /**
   *  Returns the date and time of the measurement.
   *
   *  @param index
   *             zero based index used to distinguish inputs.
   *  @return the measurement date and time
   *
   *  @throws ValueNotAvailableException
   *             if the measurement could not be done for some reason,
   *             the hardware failure for instance
   */
  Date getBinaryInputTimestamp(int index) throws ValueNotAvailableException;

  /**
   *  Returns the number of inputs, which is supported by the
   *  hw module.
   *
   *  @return number of inputs
   */
  int getNumberOfBinaryInputs();
}
