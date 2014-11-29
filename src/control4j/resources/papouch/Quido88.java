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
import java.util.Date;
import control4j.Control;
import control4j.Resource;
import control4j.ConfigItem;
import control4j.IConfigBuffer;
import control4j.ICycleEventListener;
import control4j.resources.IThermometer;
import control4j.resources.IBinaryInput;
import control4j.resources.IBinaryOutput;
import control4j.resources.ValueNotAvailableException;
import control4j.resources.spinel.Spinel;
import control4j.protocols.spinel.SpinelMessage;
import control4j.tools.IResponseCrate;

/**
 *  Encapsulate hardware IO module Quido with eight binary inputs and
 *  eight binary outputs made by Papouch manufacturer. This module
 *  can measure temperature if the temperature sensor is connected.
 *  The temeperature measurement must be allowed by setting property
 *  temp-sensor to true.
 */
public class Quido88 extends control4j.resources.Resource 
implements ICycleEventListener, IThermometer, IBinaryInput, IBinaryOutput
{
  /**
   *  Spinel address of the module.
   */
  @ConfigItem 
  public int address;

  /**
   *  If the temperature sensor is connected to Quido and
   *  you want to use it for measurement, set this property
   *  to true. Default value is false.
   */
  @ConfigItem(key="temp-sensor", optional=true)
  public boolean temperatureSensorConnected = false;

  /**
   *  Name of the spinel resource.
   */
  @Resource 
  public Spinel spinel;

  /* binary output */
  private final int outputs = 8;  // number of outputs
  private Boolean[] output = new Boolean[outputs];
  private IResponseCrate<SpinelMessage> outputResponse;
  private int binaryOutputStatus = -1;

  /* binary input */
  private final int inputs = 8;  // number of inputs
  private SpinelMessage inputRequest;
  private IResponseCrate<SpinelMessage> inputResponse;
  private boolean[] binaryInput = new boolean[inputs];
  private Date binaryInputTimestamp;
  private int binaryInputStatus = -1;

  /* temperature measurement */
  private SpinelMessage temperatureRequest;
  private IResponseCrate<SpinelMessage> temperatureUnitResponse;
  private IResponseCrate<SpinelMessage> temperatureResponse;
  private String temperatureUnit = null;
  private double temperature;
  private Date temperatureTimestamp;
  private int temperatureStatus = 10;

  @Override
  public void prepare()
  {
    inputRequest = new SpinelMessage(address, 0x31);
    temperatureRequest = new SpinelMessage(address, 0x51, new int[] { 1 });
  }

  public void setBinaryOutput(int index, boolean value)
  {
    if (index >= outputs || index < 0)
      throw new IndexOutOfBoundsException();
    output[index] = value;
  }

  public int getBinaryOutputStatus(int index)
  {
    if (index >= outputs || index < 0)
      throw new IndexOutOfBoundsException();
    return binaryOutputStatus;
  }

  public int getNumberOfBinaryOutputs()
  {
    return outputs;
  }

  public boolean getBinaryInput(int index) throws ValueNotAvailableException
  {
    if (index >= inputs || index < 0)
      throw new IndexOutOfBoundsException();
    if (binaryInputStatus == 0)
      return binaryInput[index];
    else
      throw new ValueNotAvailableException();
  }

  public int getBinaryInputStatus(int index)
  {
    if (index >= inputs || index < 0)
      throw new IndexOutOfBoundsException();
    return binaryInputStatus;
  }

  public Date getBinaryInputTimestamp(int index) 
  throws ValueNotAvailableException
  {
    if (index >= inputs || index < 0)
      throw new IndexOutOfBoundsException();
    if (binaryInputStatus == 0)
      return binaryInputTimestamp;
    else
      throw new ValueNotAvailableException();
  }

  public int getNumberOfBinaryInputs()
  {
    return inputs;
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
    if (temperatureUnit != null)
      return temperatureUnit;
    else
      throw new ValueNotAvailableException();
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
   *  Send binary outputs into the Quido. It sends request to
   *  set only outputs which gets some value, unused outputs
   *  stay unchanged.
   */
  public void cycleEnd()
  {
    // set binary outputs
    if (outputResponse == null)
    {
      int outputs = 0;
      for (int i=0; i<output.length; i++)
        if (output[i] != null) outputs++;
      if (outputs > 0)
      {
        int[] data = new int[outputs];
        int j=0;
        for (int i=0; i<output.length; i++)
          if (output[i] != null)
          {
            data[j] = output[i] ? 0x80 : 0x00;
            data[j] += i + 1;
            j++;
	    output[i] = null;
          }
        SpinelMessage message = new SpinelMessage(address, 0x20, data);
        outputResponse = spinel.write(message);
      }
    }
  }

  /**
   *  Sends requests for new data.
   */
  public void cycleStart()
  {
    // Request for temperature
    if (temperatureSensorConnected)
    {
      // request for measurement
      if (temperatureResponse == null)
        temperatureResponse = spinel.write(temperatureRequest);
      // request for unit. Unit is determined only once
      if (temperatureUnit == null)
        if (temperatureUnitResponse == null)
	{
	  SpinelMessage message = new SpinelMessage(address, 0x1d);
	  temperatureUnitResponse = spinel.write(message);
	}
    }
    
    // Request for binary input
    if (inputResponse == null)
      inputResponse = spinel.write(inputRequest);
  }

  /**
   *  Processes responses for new data.
   */
  public void processingStart()
  {
    // temperature measurement
    if (temperatureSensorConnected)
    {
      temperatureStatus = 0;
      if (temperatureResponse.isFinished())
      {
        try
        {
          SpinelMessage response = temperatureResponse.getResponse();
	  if (response.getInst() == 0)
	  {
            temperatureTimestamp = temperatureResponse.getTimestamp();
            temperature = decodeTemperatureResponse(response);
          }
	  else
	  {
	    // something is wrong with the module Quido
	    temperatureStatus = 1;
	  }
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
        // data have not been recived yet
	temperatureStatus = 3;
      }

      // processing response for temperature unit
      if (temperatureUnit == null)
      {
        if (temperatureUnitResponse.isFinished())
        {
          try
          {
            SpinelMessage response = temperatureUnitResponse.getResponse();
	    if (response.getInst() == 0)
	    {
              int data = response.getData(1);
	      switch (data)
	      {
	        case 0:
		  temperatureUnit = "°C";
		  break;
                case 1:
		  temperatureUnit = "F";
		  break;
	        case 2:
		  temperatureUnit = "K";
		  break;
	        default:
		  // unknown temperature unit
		  temperatureStatus = 4;
	          break;
	      }
            }
	    else
	    {
	      // something is wrong with the module Quido
	      temperatureStatus = 5;
	    }
          }
	  catch (IOException e)
	  {
	    // something is wrong with the communication channal
	    temperatureStatus = 6;
	  }
          temperatureUnitResponse = null;
        }
        else
        {
          // data have not been recived yet
	  temperatureStatus = 7;
        }
      }
    }

    // process binary input response
    if (inputResponse.isFinished())
    {
      try
      {
        binaryInputTimestamp = inputResponse.getTimestamp();
        SpinelMessage response = inputResponse.getResponse();
	decodeBinaryInputResponse(response);
      }
      catch (IOException e)
      {
        // something is wrong with the communication line
	binaryInputStatus = -1;
      }
      inputResponse = null;
    }
    else
    {
      // response has not been received yet
      binaryInputStatus = -1;
    }

    // process binary output response
    if (outputResponse != null && outputResponse.isFinished())
    {
      try
      {
        SpinelMessage response = outputResponse.getResponse();
        decodeBinaryOutputResponse(response);
      }
      catch (IOException e)
      {
        // something is wrong with the communication line
        binaryOutputStatus = -1;
      }
      outputResponse = null;
    }
    else
    {
      // response has not been received yet
      binaryOutputStatus = -1;
    }

  }

  private double decodeTemperatureResponse(SpinelMessage message)
  {
    int rawValue = (message.getData(1) << 8) + message.getData(2);
    return (double)rawValue * 0.1;
  }

  private void decodeBinaryInputResponse(SpinelMessage message)
  {
    if (message.getInst() == 0)
    {
      int mask = 1;
      for (int i=0; i<8; i++)
      {
        binaryInput[i] = (message.getData(0) & mask) > 0;
        mask *= 2;
      }
      binaryInputStatus = 0;
    }
    else
    {
      // something is wrong with the module Quido
      binaryInputStatus = -1;
    }
  }

  private void decodeBinaryOutputResponse(SpinelMessage message)
  {
    if (message.getInst() == 0)
      binaryOutputStatus = 0;
    else
      binaryOutputStatus = -1;
  }
}
