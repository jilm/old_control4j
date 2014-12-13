package control4j.modules;

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

import control4j.Module;
import control4j.Signal;
import control4j.ConfigItem;
import control4j.InputModule;
import control4j.Resource;
import control4j.resources.communication.SignalServer;
import static control4j.tools.Logger.*;

/**
 *  Sends input data to the client over the TCP protocol.
 */ 
public class IMNetOutput extends InputModule
{

  @Resource
  public SignalServer server;
  
  /**
   *  Sends input data to a client over the TCP protocol.
   *  
   *  @param input 
   *             data so send
   */         
  @Override
  protected void put(Signal[] input)
  {
    server.send(input, getNumberOfAssignedInputs());
  }
  
}
