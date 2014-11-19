package control4j.resources.spinel;

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

import control4j.ConfigItem;
import control4j.resources.Resource;
import control4j.tools.IResponseCrate;
import control4j.protocols.spinel.SpinelOverTcp;
import control4j.protocols.spinel.SpinelMessage;
import static control4j.tools.Logger.*;

public class Spinel extends Resource 
{
  @ConfigItem
  public String host;

  @ConfigItem
  public int port;

  private SpinelOverTcp channel;

  @Override 
  public void prepare()
  {
    channel = new SpinelOverTcp(host, port);
  }

  public IResponseCrate<SpinelMessage> write(SpinelMessage message)
  {
    finest("Sending request: " + message.toString());
    return channel.write(message);
  }

}
