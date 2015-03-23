package control4j;

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

import java.util.Arrays;

import control4j.application.Input;
import control4j.application.Output;

/**
 *
 *  A class which is responsible for modules instantiation.
 *
 */
public class Instantiator
{

  public Instantiator()
  { }

  control4j.application.Application definitions;

  /**
   *  Takes the collection of the module definitions and create
   *  instances of classes that implements the module functionality.
   */
  public Application instantiate(
      control4j.application.Application application)
  {
    definitions = application;
    Application result;
    int modules = application.getModulesSize();
    // TODO Allocates the destination array
    // Create instance of all of the modules
    for (int i=0; i<modules; i++)
    {
      // get module definition
      controlj4.application.Module definition 
          = application.getModule(i);
      // create instance
      Module instance = instantiate(definition);
      // save result
      result.put(i, instance);
    }
    return result;
  }

  /**
   *  Create and return an instance of the module which correspond
   *  to the given module definition.
   */
  protected Module instantiate(control4j.application.Module definition)
  {
    try
    {
      // get module definition
      controlj4.application.Module definition 
          = application.getModule(i);

      // get module class
      String className = definition.getClassName;
      Class<Module> moduleClass
          = (Class<Module>)Class.forName(className);

      // create the instance
      Module instance = moduleClass.newInstance();

      // assign input and output maps
      int[] map = getInputMap(definition, moduleClass);
      if (map != null) instance.setInputMap(map);
      map = getOutputMap(definition, moduleClass);
      if (map != null) instance.setOutputMap(map);

      // configure new instance
      IConfigBuffer configuration = definition.getConfiguration();
      instance.initialize(configuration);

      // assign resources

      // set configuration of input and output
    }
    catch (ClassNotFoundException e)
    {
      // TODO
    }
    catch (InstantiationException e)
    {
      // TODO
    }
    catch (IllegalAccessException e)
    {
      // TODO
    }
  }

  /**
   *  Creates and returns the input map for a given module definition.
   */
  protected int[] getInputMap(
      control4j.application.Module definition, Class<Module> moduleClass)
  {
    // get AVariableInput annotation, if any
    AVariableInput varInput 
	= moduleClass.getAnnotation(AVariableInput.class);

    // calculate required size of the map array
    int mapSize = 0;
    int fixedInputSize = definition.getInputSize();
    int varInputIndex = 0;
    int varInputSize = definition.getVariableInputSize();
    if (varInputSize == 0)
    {
      mapSize = fixedInputSize;
    }
    else if (varInput == null)
    {
      // TODO variable input is not supported!
    }
    else if (fixedInputSize >= varInput.startIndex())
    {
      // TODO index collision
    }
    else
    {
      varInputIndex = varInput.startIndex();
      mapSize = varInputIndex + varInputSize - 1;
    }
    if (mapSize == 0) return null;

    // allocate the map array
    int[] map = new int[mapSize];
    Arrays.fill(map, -1);

    // fixed index input
    for (int i=0; i<fixedInputSize; i++)
    {
      // get input definition
      Input input = definition.getInput(i);
      // get the index of the appropriate signal definition
      control4j.application.Signal signal = definitions.getSignal(
	  input.getHref(), input.getScope());
      int index = definitions.getSignalIndex(signal);
      // put the index into the map
      map[i] = index;
    }

    // input with variable index
    for (int i=0; i<varInputSize; i++)
    {
      // get input definition
      Input input = definition.getVariableInput(i);
      // get the index of the appropriate signal definition
      control4j.application.Signal signal = definitions.getSignal(
	  input.getHref(), input.getScope());
      int index = definitions.getSignalIndex(signal);
      // put the index into the map
      map[i+varInputIndex] = index;
    }

    return map;
  }

  /**
   *  Creates and returns the input map for a given module definition.
   */
  protected int[] getOutputMap(
      control4j.application.Module definition, Class<Module> moduleClass)
  {
    // get AVariableOutput annotation, if any
    AVariableOutput varOutput 
	= moduleClass.getAnnotation(AVariableOutput.class);

    // calculate required size of the map array
    int mapSize = 0;
    int fixedOutputSize = definition.getOutputSize();
    int varOutputIndex = 0;
    int varOutputSize = definition.getVariableOutputSize();
    if (varOutputSize == 0)
    {
      mapSize = fixedOutputSize;
    }
    else if (varOutput == null)
    {
      // TODO variable output is not supported!
    }
    else if (fixedOutputSize >= varOutput.startIndex())
    {
      // TODO index collision
    }
    else
    {
      varOutputIndex = varOutput.startIndex();
      mapSize = varOutputIndex + varOutputSize - 1;
    }
    if (mapSize == 0) return null;

    // allocate the map array
    int[] map = new int[mapSize];
    Arrays.fill(map, -1);

    // fixed index output
    for (int i=0; i<fixedOutputSize; i++)
    {
      // get output definition
      Output output = definition.getOutput(i);
      // get the index of the appropriate signal definition
      control4j.application.Signal signal = definitions.getSignal(
	  output.getHref(), output.getScope());
      int index = definitions.getSignalIndex(signal);
      // put the index into the map
      map[i] = index;
    }

    // output with variable index
    for (int i=0; i<varOutputSize; i++)
    {
      // get output definition
      Output output = definition.getVariableOutput(i);
      // get the index of the appropriate signal definition
      control4j.application.Signal signal = definitions.getSignal(
	  output.getHref(), output.getScope());
      int index = definitions.getSignalIndex(signal);
      // put the index into the map
      map[i+varOutputIndex] = index;
    }

    return map;
  }
}
