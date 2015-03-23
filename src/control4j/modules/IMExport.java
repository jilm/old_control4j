package control4j.modules;

/*
 *  Copyright 2015 Jiri Lidinsky
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

import java.util.Collection;

import control4j.InputModule;
import control4j.Resource;
import control4j.Signal;
import control4j.application.ModuleDeclaration;
import control4j.protocols.IRequest;
import control4j.protocols.signal.Request;
import control4j.protocols.signal.Response;
import control4j.protocols.signal.DataRequest;
import control4j.protocols.signal.DataResponse;
import control4j.resources.IServer;

/**
 *
 *  Exports input signals through the given resource. This module
 *  is dedicated to provide data to another control4j instance, or
 *  to some external program or application.
 *
 */
public class IMExport extends InputModule
{

  @Resource
  public IServer<Request> server;

  /** Names of the input signal that will be used as an identifier */
  protected String ids[];

  /**
   *  The module needs to know the names of the input signals
   *  to use them as ids inside the exprot message.
   */
  /*                      TODO
  @Override
  protected void initialize(ModuleDeclaration declaration)
  {
    super.initialize(declaration);
    ids = new String[declaration.getInputsSize()];
    for (int i=0; i<ids.length; i++)
      ids[i] = declaration.getInput(i).getSignal();
  }
  */

  /**
   *  Sends input signals throught the given server resource.
   */
  @Override
  protected void put(Signal[] input, int inputLength)
  {
    Collection<Request> requests = server.getRequests();
    for (Request request : requests)
    {
      if (request instanceof DataRequest)
      {
	DataResponse response 
	    = (DataResponse)((DataRequest)request).getResponse();
	for (int i=0; i<inputLength; i++)
	  response.put(ids[i], input[i]);
      }
    }
  }

}
