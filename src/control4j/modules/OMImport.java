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

import control4j.OutputModule;
import control4j.Resource;
import control4j.Signal;
//import control4j.application.ModuleDeclaration;
import control4j.protocols.signal.Request;
import control4j.protocols.signal.Response;
import control4j.protocols.signal.DataRequest;
import control4j.protocols.signal.DataResponse;
import control4j.resources.communication.SignalClient;

/**
 *
 *
 */
public class OMImport extends OutputModule
{

  @Resource
  public SignalClient client;

  /** Names of the input signal that will be used as an identifier */
  protected String ids[];

  /**
   *  The module needs to know the names of the input signals
   *  to use them as ids inside the exprot message.
   */
  /*               TODO !!!
  @Override
  protected void initialize(ModuleDeclaration declaration)
  {
    super.initialize(declaration);
    ids = new String[declaration.getOutputsSize()];
    for (int i=0; i<ids.length; i++)
      ids[i] = declaration.getOutput(i).getSignal();
  }
  */

  /**
   *  Sends input signals throught the given server resource.
   */
  @Override
  public void get(Signal[] output, int outputLength)
  {
    DataResponse response = (DataResponse)client.read();
    if (response != null)
    {
      for (int i=0; i<outputLength; i++)
      {
	output[i] = response.get(ids[i]);
      }
    }
    for (int i=0; i<outputLength; i++)
      if (output[i] == null)
	output[i] = Signal.getSignal();
  }

}
