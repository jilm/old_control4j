package control4j.hw.papouch;

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
import control4j.tools.IResponseCrate;
import control4j.protocols.spinel.SpinelOverTcp;
import control4j.protocols.spinel.SpinelMessage;
import control4j.protocols.spinel.SpinelException;
import control4j.scanner.Getter;

public class TQS3 extends Papouch
{
  public static final int MEASUREMENT = 0x51;
  public static final int COMMUNICATION_PARAM_READ = 0xf0;
  public static final int COMMUNICATION_PARAM_SET = 0xe0;
  public static final int COMMUNICATION_ERROR_READ = 0xf4;
  //public static final int MODEL_NUMBER_READ = 0xf3;
  public static final int STATUS_READ = 0xf1;
  public static final int STATUS_SET = 0xe1;
  public static final int USER_DATA_READ = 0xf2;
  public static final int USER_DATA_SET = 0xe2;
  public static final int CHECKSUM_READ = 0xfe;
  public static final int CHECKSUM_ENABLE = 0xee;
  public static final int ADDRESS_CHANGE = 0xeb;
  public static final int CONFIGURATION_ENABLE = 0xe4;
  public static final int RESET = 0xe3;
  public static final int COMMUNICATION_PROTOCOL_SET = 0xed;
  public static final int SERIAL_NUMBER_READ = 0xfa;

  public TQS3(SpinelOverTcp channel, int address)
  {
    super(channel, address);
  }

  public static double getOneTimeMeasurement(SpinelMessage message)
  throws SpinelException
  {
    if (message.getInst() == 0)
    {
      short rawValue = (short)((message.getData(0) << 8) + message.getData(1));
      double value = (double)rawValue;
      value = Math.floor(value / 3.2d + 0.5d) * 0.1d;
      return value;
    }
    else
      throw new SpinelException();
  }

  @Getter(key="measurement")
  public double getOneTimeMeasurement() throws IOException
  {
    SpinelMessage request = new SpinelMessage(address, MEASUREMENT);
    IResponseCrate<SpinelMessage> responseCrate = channel.send(request);
    SpinelMessage response = responseCrate.getResponse();
    return getOneTimeMeasurement(response);
  } 

  public static CommunicationParams getCommunicationParams(SpinelMessage message)
  throws SpinelException
  {
    if (message.getInst() == 0)
      return new CommunicationParams(message.getData(0), message.getData(1));
    else
      throw new SpinelException();
  }

  @Getter(key="communication-params")
  public CommunicationParams getCommunicationParams() throws IOException
  {
    SpinelMessage request = new SpinelMessage(address, COMMUNICATION_PARAM_READ);
    IResponseCrate<SpinelMessage> responseCrate = channel.send(request);
    SpinelMessage response = responseCrate.getResponse();
    return getCommunicationParams(response);
  }

  public static int getStatus(SpinelMessage message) throws SpinelException
  {
    if (message.getInst() == 0)
      return message.getData(0);
    else
      throw new SpinelException();
  }

  @Getter(key="status")
  public int getStatus() throws IOException
  {
    SpinelMessage request = new SpinelMessage(address, STATUS_READ);
    return getStatus(send(request));
  }

  public static int getCommunicationErrors(SpinelMessage message)
  throws SpinelException
  {
    if (message.getInst() == 0)
      return message.getData(0);
    else
      throw new SpinelException();
  }

  @Getter(key="communication errors")
  public int getCommunicationErrors() throws IOException
  {
    SpinelMessage request = new SpinelMessage(address, STATUS_READ);
    return getCommunicationErrors(send(request));
  }

  public static String getUserData(SpinelMessage message) throws SpinelException
  {
    if (message.getInst() == 0)
    {
      int[] buffer = new int[16];
      for (int i=0; i<16; i++) buffer[i] = message.getData(i);
      return new String(buffer, 0, 16);
    }
    else
      throw new SpinelException();
  }

  @Getter(key="user data")
  public String getUserData() throws IOException
  {
    SpinelMessage request = new SpinelMessage(address, USER_DATA_READ);
    return getUserData(send(request));
  }

  public static SerialNumber getSerialNumber(SpinelMessage message)
  throws SpinelException
  {
    if (message.getInst() == 0)
    {
      int productNumber = message.getData(0) * 0x100 + message.getData(1);
      int serialNumber = message.getData(2) * 0x100 + message.getData(3);
      long other = message.getData(4) * 0x1000000l 
        + message.getData(5) * 0x10000l + message.getData(6) * 0x100l
	+ message.getData(7);
      return new SerialNumber(productNumber, serialNumber, other);
    }
    else
      throw new SpinelException();
  }

  @Getter(key="serial-number")
  public SerialNumber getSerialNumber() throws IOException
  {
    SpinelMessage request = new SpinelMessage(address, SERIAL_NUMBER_READ);
    return getSerialNumber(send(request));
  }

  public static boolean isChecksumEnabled(SpinelMessage message)
  throws SpinelException
  {
    if (message.getInst() == 0)
    {
      if (message.getData(0) == 0)
        return false;
      else if (message.getData(0) == 1)
        return true;
      else
        throw new SpinelException();
    }
    else
      throw new SpinelException();
  }

  public boolean isChecsumEnabled() throws IOException
  {
    SpinelMessage request = new SpinelMessage(address, CHECKSUM_READ);
    return isChecksumEnabled(send(request));
  }

}

