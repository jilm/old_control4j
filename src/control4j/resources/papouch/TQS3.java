package control4j.resources.papouch;

/*
 *  Copyright 2013, 2014 Jiri Lidinsky
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
import java.util.Date;
import control4j.Control;
import control4j.Resource;
import control4j.ConfigItem;
import control4j.IConfigBuffer;
import control4j.ICycleEventListener;
import control4j.resources.IThermometer;
import control4j.resources.ValueNotAvailableException;
import control4j.resources.spinel.Spinel;
import control4j.protocols.spinel.SpinelMessage;
import control4j.tools.IResponseCrate;

/**
 *
 *  Encapsulate hardware IO module TQS3 which is thermometer.
 *
 */
public class TQS3 extends control4j.resources.Resource 
implements ICycleEventListener, IThermometer
{
  /**
   *  Spinel address of the module.
   */
  @ConfigItem 
  public int address;

  /**
   *  Name of the spinel resource.
   */
  @Resource 
  public Spinel spinel;

  /* temperature measurement */
  private SpinelMessage temperatureRequest;
  private IResponseCrate<SpinelMessage> temperatureResponse; 
  private String temperatureUnit = "stC";
  private double temperature;
  private Date temperatureTimestamp;
  private int temperatureStatus = 10;

  @Override
  public void prepare()
  {
    temperatureRequest 
      = new SpinelMessage(address, control4j.hw.papouch.TQS3.MEASUREMENT); 
  }

  public double getTemperature(int index) throws ValueNotAvailableException
  {
    if (index != 0)
      throw new IndexOutOfBoundsException();
    if (temperatureStatus == 0)
      return temperature;
    else
      throw new ValueNotAvailableException();
  }

  public String getTemperatureUnit(int index) throws ValueNotAvailableException
  {
    if (index != 0)
      throw new IndexOutOfBoundsException();
    else
      return temperatureUnit;
  }

  public int getTemperatureStatus(int index)
  {
    if (index != 0)
      throw new IndexOutOfBoundsException();
    return temperatureStatus;
  }

  public Date getTemperatureTimestamp(int index) 
  throws ValueNotAvailableException
  {
    if (index != 0)
      throw new IndexOutOfBoundsException();
    if (temperatureStatus == 0)
      return temperatureTimestamp;
    else
      throw new ValueNotAvailableException();
  }

  public int getNumberOfTemperatureChannels()
  {
    return 1;
  }

  /**
   *
   */
  public void cycleEnd()
  {
  }

  /**
   *  Sends requests for new data.
   */
  public void cycleStart()
  {
    // Request for temperature
    if (temperatureResponse == null)
      temperatureResponse = spinel.write(temperatureRequest);
  }

  /**
   *  Processes responses for new data.
   */
  public void processingStart()
  {
    // temperature measurement
    if (temperatureResponse.isFinished())
    {
      try
      {
        SpinelMessage response = temperatureResponse.getResponse();
        temperature = control4j.hw.papouch.TQS3.getOneTimeMeasurement(response);
        temperatureStatus = 0;
      }
      catch (control4j.protocols.spinel.SpinelException e)
      {
        // something is wrong with the module
        temperatureStatus = 1;
      }
      catch (IOException e)
      {
        // something is wrong with the communication channal
        temperatureStatus = 2;
      }
      temperatureResponse = null;
    }
    else
    {
      // data have not been received yet
      temperatureStatus = 3;
    }
  }

}
