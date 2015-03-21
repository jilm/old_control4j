package control4j.resources.papouch;

/*
 *  Copyright 2013 - 2015 Jiri Lidinsky
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
import java.io.IOException;
import control4j.Resource;
import control4j.ConfigItem;
import control4j.IConfigBuffer;
import control4j.ICycleEventListener;
import control4j.tools.IResponseCrate;
import control4j.resources.IAnalogInput;
import control4j.resources.ValueNotAvailableException;
import control4j.resources.spinel.Spinel;
import control4j.protocols.spinel.SpinelMessage;

/**
 *
 *  Analog input module.
 *
 */
public class AD4 extends control4j.resources.Resource
implements IAnalogInput, ICycleEventListener
{

  /**
   *  Spinel address of the module.
   */
  @ConfigItem
  public int address;

  /**
   *  Resource that encapsulates communication line, the hw module is
   *  attached to.
   */
  @Resource
  public Spinel spinel;

  private final int channels = 4; // number of input channels
  private int status = -1;
  private int[] measurementStatus = new int[channels];
  private double[] values = new double[channels];
  private Date timestamp;
  private SpinelMessage request;
  private IResponseCrate<SpinelMessage> response = null;

  @Override
  public void prepare()
  {
    request = new SpinelMessage(address, 0x51, new int[] { 0 });
  }

  public double getAnalogInput(int index) throws ValueNotAvailableException
  {
    if (index < 0 || index >= channels)
      throw new IndexOutOfBoundsException();
    if (getAnalogInputStatus(index) == 0)
      return values[index];
    else
      throw new ValueNotAvailableException();
  }

  public String getAnalogInputUnit(int index) throws ValueNotAvailableException
  {
    if (index < 0 || index >= channels)
      throw new IndexOutOfBoundsException();
    return "";
  }

  public int getAnalogInputStatus(int index)
  {
    if (index < 0 || index >= channels)
      throw new IndexOutOfBoundsException();
    if (status != 0)
      return status;
    else
      return measurementStatus[index];
  }

  public Date getAnalogInputTimestamp(int index) 
  throws ValueNotAvailableException
  {
    if (index < 0 || index >= channels)
      throw new IndexOutOfBoundsException();
    if (getAnalogInputStatus(index) == 0)
      return timestamp;
    else
      throw new ValueNotAvailableException();
  }

  public int getNumberOfAnalogInputChannels()
  {
    return channels;
  }

  public void cycleStart()
  {
    try
    {
      if (response == null)
        response = spinel.write(request);
    }
    catch (java.io.IOException e) { }
  }

  /**
   *  Not used
   */
  public void cycleEnd()
  {
  }

  public void processingStart()
  {
    if (response != null && response.isFinished())
    {
      try
      {
	this.timestamp = response.getTimestamp();
        SpinelMessage message = response.getResponse();
	processResponse(message);
      }
      catch (IOException e)
      {
        status = -1;
      }
      response = null;
    }
    else
    {
      status = -1;
    }
  }

  private void processResponse(SpinelMessage response)
  {
    if (response.getInst() == 0)
    {
      for (int i=0; i<channels; i++)
      {
        int channel = response.getData(i*4) - 1;
        int status = response.getData(i*4+1);
        int value = (response.getData(i*4+2) << 8) + response.getData(i*4+3);
        if (status == 0x80)
	{
	  values[channel] = (double)(value)/10000.0;
	  measurementStatus[channel] = 0;
        }
	else
	  measurementStatus[channel] = -1;
      }
      this.status = 0;
    }
    else
      this.status = -1;
  }
}
