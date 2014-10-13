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
 *  Interface to the harware that provides analog outputs. One hw module
 *  may of course provide more than one channel. Output channels are 
 *  distinguished by zero based index. You may use status method to get 
 *  informed of hw dificulties with module.
 */
public interface IAnalogOutput
{
  /**
   *  Sets analog output to the new value. New value is not set
   *  at the output immediately, but at the end of the cycle.
   *
   *  @param index
   *             zero based index used to distinguish the output
   *  @param value
   *             new value to be set at the output
   */
  void setAnalogOutput(int index, double value);

  /**
   *  Returns status information for the output with index index. 
   *  If everything is OK it returns zero. If there is some problem
   *  it returns non zero value.
   *
   *  @param index
   *             zero based index used to distinguish the output
   *  @return status information
   */
  int getAnalogOutputStatus(int index);

  /**
   *  Returns the number of output channels, which is supported by the
   *  hw module.
   *
   *  @return number of outputs
   */
  int getNumberOfAnalogOutputs();
}
