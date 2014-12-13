package control4j.resources.communication;

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
import control4j.ConfigItem;
import control4j.ICycleEventListener;
import control4j.Signal;
import control4j.resources.Resource;
import control4j.tools.IResponseCrate;
import control4j.protocols.tcp.SignalOverTcp;
import static control4j.tools.Logger.*;

public class SignalClient extends Resource implements ICycleEventListener
{

  @ConfigItem
  public String host;

  @ConfigItem
  public int port;

  private SignalOverTcp channel;
  private IResponseCrate<Signal[]> response;
  private Signal[] request;

  @Override 
  public void prepare()
  {
    channel = new SignalOverTcp(host, port);
  }

  public void write(Signal[] message)
  {
    request = message;
  }

  public Signal[] read()
  {
    try
    {
      if (response != null && response.isFinished())
        return response.getResponse();
      else
        return null;
    }
    catch (IOException e)
    {
      return null;
    }
  }

  public void cycleStart()
  {
  }

  public void processingStart()
  {
  }

  public void cycleEnd()
  {
    if (response == null || response.isFinished())
    {
      if (request == null) request = new Signal[0];
      finest("Sending request: " + request.toString());
      response = channel.write(request);
    }
  }

}
