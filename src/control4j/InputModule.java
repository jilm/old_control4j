package control4j;

/*
 *  Copyright 2013, 2014 Jiri Lidinsky
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

import java.util.NoSuchElementException;
import control4j.application.Input;
import control4j.application.SignalManager;
import control4j.application.SignalDeclaration;
import control4j.application.ModuleDeclaration;

/**
 *  Represents input module, it is a module which takes input but doesn't
 *  provide any output for further processing. Typicaly it may be a module
 *  which writes values into the output hardware.
 *  Abstract class which must be extended by each input module.
 */
public abstract class InputModule extends Module
{
  
  /**
   *  Array that maps module inputs into the array of signal values
   *  and signal declarations. Not all of the inputs must be assigned.
   *  unassigned inputs have negative indexes.
   */
  private int[] inputMap;

  protected void initialize(ModuleDeclaration declaration)
  {
    // allocate the indexes array
    int size = declaration.getInputsSize();
    inputMap = new int[size];
    // fill in the array of indexes
    SignalManager signalManager = SignalManager.getInstance();
    for (int i=0; i<size; i++)
    {
      Input input = declaration.getInput(i);
      if (input == null)
        inputMap[i] = -1;
      else
      {
	try
	{
          inputMap[i] 
	    = signalManager.getHandler(input.getScope(), input.getSignal());
	}
	catch (NoSuchElementException e)
	{
	  // !!! vyjimka, nedefinovany signal
	  ErrorManager.getInstance()
	    .reportUndeclaredSignal(input.getDeclarationReferenceText());
	  inputMap[i] = -1;
	}
      }
    }
    // call custom configuration
    IConfigBuffer configuration = declaration.getConfiguration();
    initialize(configuration);
    // call custom configuration of the inputs
    for (int i=0; i<inputMap.length; i++)
    {
      Input input = declaration.getInput(i);
      if (input != null)
      {
        configuration = input.getConfiguration();
	setInputConfiguration(i, configuration);
      }
    }
  }

  /**
   *  Method, that must implement module functionality. This method
   *  must be overwritten.
   *
   *  @param input
   *             input signal values. Inputs that were not assigned
   *             contains null value.
   */
  protected abstract void put(Signal[] input);

  /**
   *  Collects all of the input which were assigned to the module,
   *  and than calls the put method.
   */
  @Override
  final void execute(DataBuffer data)
  {
    // collect input signal values
    Signal[] input = data.get(inputMap);
    // call the exec function
    put(input);
  }

  /**
   *  Returns number of input signals which were assigned to the module.
   */
  public int getNumberOfAssignedInputs()
  {
    return inputMap.length;
  }

  public void setInputConfiguration(int index, IConfigBuffer configuration)
  { }

  protected SignalDeclaration getSignalDeclaration(int index)
  {
    int handle = inputMap[index];
    if (handle >= 0)
      return SignalManager.getInstance().get(handle);
    else
      return null;
  }

  public int[] getInputMap()
  {
    return inputMap;
  }

  @Override
  public void dump(java.io.PrintWriter writer)
  {
    super.dump(writer);
    writer.print("Input map: ");
    dumpIO(inputMap, writer);
    writer.println();
  }


}
