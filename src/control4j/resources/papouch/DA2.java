package control4j.resources.papouch;

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

import java.io.IOException;
import control4j.Resource;
import control4j.ConfigItem;
import control4j.ICycleEventListener;
import control4j.tools.IResponseCrate;
import control4j.resources.IAnalogOutput;
import control4j.resources.spinel.Spinel;
import control4j.protocols.spinel.SpinelMessage;

/**
 *  Encapsulates a digital analog converter DA2 made by papouch.com
 *  manufacturer. This module has two analog outputs. And uses
 *  communication protocol spinel.
 */
public class DA2 extends control4j.resources.Resource 
implements IAnalogOutput, ICycleEventListener
{
  /**
   *  Spinel address of the module.
   */
  @ConfigItem
  public int address;

  /**
   *  Name of the spinel resource class.
   */
  @Resource
  public Spinel spinel;

  private int status = -1;
  private Double[] output = new Double[2];
  private IResponseCrate<SpinelMessage> response;

  /**
   *  Set value on output with index index. The value is sent to the
   *  hw output at the end of the processing cycle.
   *
   *  @param index
   *             zero based index of the output. Allowed values are
   *             zero and one.
   *  @param value
   *             value that will be set on output specified with param
   *             index. Allowed values lies in the range from 0.0 to 1.0.
   *  @throws IndexOutOfBoundsException
   *             if the index is less than zero or greater than one.
   */
  public void setAnalogOutput(int index, double value)
  {
    if (index < 0 || index > 1)
      throw new IndexOutOfBoundsException();

    output[index] = value;
  }

  /**
   *  Returns information about problems with potential failure of the
   *  hardware or communication line.
   *
   *  @param index
   *             zero based index of the output channel. Allowed values
   *             are zero and one. The returned information is always 
   *             the same for both channels.
   *  @return zero if everything is OK; non zero value if there are
   *             hardware problems or problems with communication.
   */
  public int getAnalogOutputStatus(int index)
  {
    if (index < 0 || index > 1)
      throw new IndexOutOfBoundsException();

    return status;
  }

  /**
   *  Returnes the number of output channels, which is two.
   *
   *  @return number two.
   */
  public int getNumberOfAnalogOutputs()
  {
    return 2;
  }

  /**
   *  Not used
   */
  public void cycleStart()
  {
  }

  /**
   *  Inspects the result of the last commucation with the hardware module
   *  and sets the status property.
   */
  public void processingStart()
  {
    if (response != null && response.isFinished())
    {
      try
      {
        SpinelMessage message = response.getResponse();
	if (message.getInst() == 0)
	  status = 0;
        else
	  status = -1;
      }
      catch (IOException e)
      {
        status = -1;
      }
      response = null;
    }
    else
      status = -1;
  }

  /**
   *  Sends required new value which was set by the setAnalogOutput
   *  function to the DA2 module. Only channel/channels whose value 
   *  has been set during the last processing cycle is changed. 
   */
  public void cycleEnd()
  {
    if (response == null)
    {
      SpinelMessage message = getMessage();
      try
      {
        if (message != null)
          response = spinel.write(message);
      }
      catch (java.io.IOException e) { }
    }
    else
    {
      status = -1;
    }
  }

  /**
   *  Compose and return the spinel message which will be sent
   *  to the module DA2. It returns null if none of the channels
   *  has been set during the last processing cycle.
   *
   *  @return request message to set analog output/outputs to the
   *          required values. Returns null if no output has been
   *          set during the last processing cycle.
   */
  private SpinelMessage getMessage()
  {
    int[] data;
    if (output[0] == null && output[1] == null)
      return null;
    else if (output[0] != null && output[1] != null)
      data = new int[6];
    else
      data = new int[3];

    int index = 0;
    if (output[0] != null)
    {
      data[index] = 0x01;
      convertValue(output[0], data, index+1);
      output[0] = null;
      index += 3;
    }
    if (output[1] != null)
    {
      data[index] = 0x02;
      convertValue(output[1], data, index+1);
      output[1] = null;
    }

    SpinelMessage message = new SpinelMessage(address, 0x40, data);
    return message;
  }

  /**
   *  Convert double value to the raw value for the DA2 module.
   *
   *  @param value
   *             value that will be converted. A number from
   *             range 0.0 to 1.0 is expected.
   *  @param data
   *             an array where the result will be stored. The
   *             array must have at least the size index+2
   *  @param index
   *             index into the data array where the first raw
   *             value will be stored.
   */
  protected void convertValue(double value, int[] data, int index)
  {
    value = value > 1.0d ? 1.0d : value;
    value = value < 0.0d ? 0.0d : value;
    long rawValue = Math.round(value * 65535.0d);
    int rawValueMsb = (int)((rawValue & 0xFF00l) >> 8);
    int rawValueLsb = (int)(rawValue & 0xFFl);
    data[index] = rawValueMsb;
    data[index+1] = rawValueLsb;
  }
}
