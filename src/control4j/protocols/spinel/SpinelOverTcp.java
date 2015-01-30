package control4j.protocols.spinel;

/*
 *  Copyright 2013, 2015 Jiri Lidinsky
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
import java.io.OutputStream;
import control4j.protocols.tcp.IInputStream;
import control4j.protocols.tcp.IOutputStream;
import control4j.protocols.tcp.RobustTcpTemplate;

/**
 *  Provides communication over tcp through spinel protocol.
 */
public class SpinelOverTcp 
extends RobustTcpTemplate<SpinelMessage, SpinelMessage>
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
  public SpinelOverTcp(String host, int port)
  {
    super(host, port);
  }

  @Override
  protected IInputStream<SpinelMessage> getSpecificInputStream(InputStream inputStream)
  {
    return new SpinelInputStream(inputStream);
  }

  @Override
  protected IOutputStream<SpinelMessage> getSpecificOutputStream(OutputStream outputStream)
  {
    return new SpinelOutputStream(outputStream);
  }

  @Override
  protected SpinelMessage getEmptyRequest()
  {
    return new SpinelMessage(30, 40);
  }

}
