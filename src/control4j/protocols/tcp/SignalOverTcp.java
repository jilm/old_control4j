package control4j.protocols.tcp;

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

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import control4j.Signal;
import control4j.protocols.tcp.IInputStream;
import control4j.protocols.tcp.IOutputStream;
import control4j.protocols.tcp.RobustTcpTemplate;
import static control4j.tools.Logger.*;

/**
 */
public class SignalOverTcp extends RobustTcpTemplate<Signal[], Signal[]>
{

  /**
   *  Initialize communication.
   *
   *  @param host
   *             host name
   *
   *  @param port
   *             port number
   */
  public SignalOverTcp(String host, int port)
  {
    super(host, port, 7777);
  }

  @Override
  protected IInputStream<Signal[]> getSpecificInputStream(
      InputStream inputStream)
  {
    try
    {
      return new SignalInputStream(inputStream);
    }
    catch (IOException e) 
    { 
      catched(getClass().getName(), "getSpecificInputStream", e);
      return null; 
    } // TODO
  }

  @Override
  protected IOutputStream<Signal[]> getSpecificOutputStream(
      OutputStream outputStream)
  {
    try
    {
      return new SignalOutputStream(outputStream);
    }
    catch (IOException e) 
    { 
      catched(getClass().getName(), "getSpecificOutputStream", e);
      return null; 
    } // TODO
  }

}
