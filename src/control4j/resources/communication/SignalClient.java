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
import control4j.protocols.signal.Client;
import control4j.protocols.signal.Request;
import control4j.protocols.signal.Response;
import control4j.protocols.signal.DataRequest;
import control4j.protocols.signal.DataResponse;
import static control4j.tools.Logger.*;

public class SignalClient extends Resource implements ICycleEventListener
{

  @ConfigItem
  public String host;

  @ConfigItem
  public int port;

  private Client client;
  private IResponseCrate<Response> response;
  private Request request;

  @Override 
  public void prepare()
  {
    client = new Client(host, port);
    client.start();
  }

  public void write(Request message)
  {
  }

  public Response read()
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
      try
      {
        if (request == null) request = new DataRequest();
        finest("Sending request: " + request.toString());
        response = client.write(request);
      }
      catch (java.io.IOException e)
      {
      }
  }

}
