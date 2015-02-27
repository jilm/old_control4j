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
import control4j.application.Output;
import control4j.application.ModuleDeclaration;
import control4j.application.SignalDeclaration;
import control4j.application.SignalManager;

/**
 *  Represents a module which provides only output for another processing.
 */
public abstract class OutputModule extends Module
{

  /**
   *  Array that maps module outputs into the array of signal values
   *  and signal declarations. Not all of the outputs must be assigned.
   *  unassigned outputs have negative values.
   */
  private int[] outputMap;
  
  /**
   *  Initialize the internal structures of the module based on the
   *  declaration. Namely, it createas and assignes the input and
   *  output map arrays, calls {@link #initialize(IConfigBuffer)}
   *  method to provide custom configuration and calls 
   *  {@link #setOutputConfiguration} method for each output to
   *  perform custom output configuration.
   *
   *  <p>This method is called only once, during the phase of 
   *  application building by the {@link Module#getInstance} method.
   *  
   *  @param declaration
   *             module declaration. May not have null value
   *
   *  @see #initialize(IConfigBuffer)
   *  @see #setOutputConfiguration
   *  @see Module#getInstance
   */
  protected void initialize(ModuleDeclaration declaration)
  {
    // output map initialization
    initializeOutputMap(declaration);
    // calls custom configuration
    IConfigBuffer configuration = declaration.getConfiguration();
    initialize(configuration);
    // calls output configuration
    for (int i=0; i<declaration.getOutputsSize(); i++)
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
   *  Method that provides output of the module for the further prcessing.
   *  This is the only method that must be overwritten by the module
   *  developer. This method will be called repeatedly by the execute,
   *  method during the processing phase.
   *
   *  @return the module output. The size of the returned array must
   *             be at least the size returned by the 
   *             {@link #getNumberOfAssignedOutputs} method. If it is
   *             larger, extra elements will be ignored. The 
   *             mandatory elements may not contain null values.
   *
   *  @throws RuntimeModuleException
   *             if something went wrong and the whole control loop
   *             should not be finished. But sometimes it is sufficient
   *             just return invalid signals.
   *
   *  @see #execute
   *  @see #getNumberOfAssignedOutputs
   */
  protected abstract Signal[] get();

  /**
   *  Calls {@link #get} method and then places the returned signals
   *  into the data buffer which is given as parameter. This method
   *  is called repeatedly by the instance of ControlLoop object during
   *  the processing phase.
   *
   *  @param data
   *             a data buffer to store output of the module
   *
   *  @see ControlLoop
   */
  @Override
  final void execute(DataBuffer data)
  {
    // call the exec function
    Signal[] output = get();
    // assign units
    assignUnits(outputMap, output);
    // store the output into the data buffer
    data.put(output, outputMap);
  }

  /**
   *  Returns number of outputs which will be used for the further
   *  processing. This number may be smaller than the amount of 
   *  outputs that are provided by this module, but may not be 
   *  greater.
   *
   *  @return the number of outputs which are expecting that this
   *             will provide.
   */
  public int getNumberOfAssignedOutputs()
  {
    return outputMap.length;
  }

  /**
   *  This method is used for custom output configuration. This method
   *  does nothing. If you (the module developer) want to provide
   *  custom configuration of particular output, just override this
   *  method.
   *
   *  <p>This method is called only once during the phase of application
   *  building by the method {@link #initialize(IConfigBuffer)} for
   *  each output.
   *
   *  <p>If parameters contain configuration that is not supported by
   *  this module or output, throws SyntaxErrorException.
   *
   *  @param index
   *             index of module output
   *
   *  @param configuration
   *             contains custom configuration for an output with given
   *             index. May contain null value if there is no custom
   *             configuration for the output.
   */
  public void setOutputConfiguration(int index, IConfigBuffer configuration)
  { }

  /**
   *  Returns a signal declaration object for output with
   *  given index. It is useful if module need informations such
   *  is the signal name or unit.
   *
   *  @param index
   *            module output index
   *
   *  @return signal declaration or null if output with given index
   *            is not used
   *
   *  @throws IndexOutOfBoundsException
   *            if index parameter is less then zero or greater 
   *            then number of assigned outputs
   */
  protected SignalDeclaration getOutputSignalDeclaration(int index)
  {
    int handler = outputMap[index];
    if (handler >= 0)
      return SignalManager.getInstance().get(handler);
    else
      return null;
  }

  public int[] getOutputMap()
  {
    return outputMap;
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
	catch (NoSuchElementException e)
	{
	  // !!! vyjimka, nedefinovany signal
	  ErrorManager.getInstance()
	    .reportUndeclaredSignal(output.getDeclarationReferenceText());
	  outputMap[i] = -1;
	}
      }
    }
  }

  @Override
  public void dump(java.io.PrintWriter writer)
  {
    super.dump(writer);
    writer.print("Output map: ");
    dumpIO(outputMap, writer);
    writer.println();
  }

}
