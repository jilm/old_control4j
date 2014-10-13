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

import control4j.application.Input;
import control4j.application.Output;
import control4j.application.SignalManager;
import control4j.application.ModuleDeclaration;
import control4j.application.SignalDeclaration;

/**
 *  Represents processing module, it is a module that gets input, performs
 *  some calculation on it and provides output.
 */
public abstract class ProcessModule extends Module
{

  /**
   *  Array that maps module inputs into the array of signal values
   *  and signal declarations. Not all of the inputs must be assigned.
   *  unassigned inputs have negative indexes.
   */
  private int[] inputMap;

  /**
   *  Array that maps module outputs into the array of signal values
   *  and signal declarations. Not all of the outputs must be assigned.
   *  unassigned outputs have negative indexes.
   */
  private int[] outputMap;
  
  /**
   *  Initialize the internal structures of the module based on the
   *  declaration. Namely, it createas and asignes the input and
   *  output mapping arrays.
   */
  protected final void initialize(ModuleDeclaration declaration)
  {
    // mandatory configuration
    initializeInputMap(declaration);
    initializeOutputMap(declaration);
    // custom configuration
    IConfigBuffer configuration = declaration.getConfiguration();
    initialize(configuration);
    // inputs configuration
    for (int i=0; i<inputMap.length; i++)
    {
      Input input = declaration.getInput(i);
      if (input != null)
      {
        configuration = input.getConfiguration();
        setInputConfiguration(i, configuration);
      }
    }
    // outputs configuration
    for (int i=0; i<outputMap.length; i++)
    {
      Output output = declaration.getOutput(i);
      if (output != null)
      {
        configuration = output.getConfiguration();
        setOutputConfiguration(i, configuration);
      }
    }
  }

  /**
   *  This method should implement the module functionality.
   */
  protected abstract Signal[] process(Signal[] input);

  /**
   *  Collects all of the input which were assigned to the module,
   *  and than calls the process method. Returned signal values
   *  than puts into the data buffer.
   */
  @Override
  final void execute(DataBuffer data)
  {
    // collect input signal values
    Signal[] input = data.get(inputMap);
    // call the exec function
    Signal[] output = process(input);
    // store the output into the data buffer
    data.put(output, outputMap);
  }

  /**
   *  Returns number of input signals which were assigned to the module.
   */
  public int getNumberOfAssignedInputs()
  {
    return inputMap.length;
  }

  public int getNumberOfAssignedOutputs()
  {
    return outputMap.length;
  }

  public void setInputConfiguration(int input,  IConfigBuffer configuration)
  { }

  public void setOutputConfiguration(int input, IConfigBuffer configuration)
  { }

  protected SignalDeclaration getInputSignalDeclaration(int index)
  {
    int handler = inputMap[index];
    if (handler >= 0)
      return SignalManager.getInstance().get(handler);
    else
      return null;
  }

  protected SignalDeclaration getOutputSignalDeclaration(int index)
  {
    int handler = outputMap[index];
    if (handler >= 0)
      return SignalManager.getInstance().get(handler);
    else
      return null;
  }

  public int[] getInputMap()
  {
    return inputMap;
  }

  public int[] getOutputMap()
  {
    return outputMap;
  }


  private void initializeInputMap(ModuleDeclaration declaration)
  {
    // allocate the input indexes array
    int size = declaration.getInputsSize();
    inputMap = new int[size];
    // fill in the array of input indexes
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
	catch (java.util.NoSuchElementException e)
	{
	  // !!! vyjimka, nedefinovany signal
	  ErrorManager.getInstance()
	    .reportUndeclaredSignal(input.getDeclarationReferenceText());
	  inputMap[i] = -1;
	}
      }
    }
  }

  private void initializeOutputMap(ModuleDeclaration declaration)
  {
    // allocate the input indexes array
    int size = declaration.getOutputsSize();
    outputMap = new int[size];
    // fill in the array of output indexes
    SignalManager signalManager = SignalManager.getInstance();
    for (int i=0; i<size; i++)
    {
      Output output = declaration.getOutput(i);
      if (output == null)
        outputMap[i] = -1;
      else
      {
	try
	{
          outputMap[i] 
	    = signalManager.getHandler(output.getScope(), output.getSignal());
	}
	catch (java.util.NoSuchElementException e)
	{
	  // !!! vyjimka, nedefinovany signal
	  ErrorManager.getInstance()
	    .reportUndeclaredSignal(output.getDeclarationReferenceText());
	  outputMap[i] = -1;
	}
      }
    }
  }

}
