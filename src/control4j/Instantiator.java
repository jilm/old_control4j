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

import control4j.application.Input;
import control4j.application.Output;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/**
 *
 *  A class which is responsible for modules instantiation.
 *
 */
public class Instantiator 
{

  /**
   *  An empty constructor.
   */
  public Instantiator() {}

  control4j.application.Application applicationDef;

  Application result;

  /**
   *  Creates instances of all of the modules. The sequence is as 
   *  follows:
   *  <ol>
   *    <li>Creates instance of the module.
   *    <li>Creates input and output map.
   *    <li>Calls initialization method of the Module.
   *    <li>Calls initialization input and output methods of the Module.
   *    <li>Assignes the resources.
   *    <li>Register as a ICycleListener.
   *  </ol>
   */
  public Application instantiate(
      control4j.application.Application application) 
  {

    applicationDef = application;
    ArrayList<Pair<InputModule, int[]>> inputModules 
        = new ArrayList<Pair<InputModule, int[]>>();
    ArrayList<Triple<ProcessModule, int[], int[]>> processModules
        = new ArrayList<Triple<ProcessModule, int[], int[]>>();
    ArrayList<Pair<OutputModule, int[]>> outputModules
        = new ArrayList<Pair<OutputModule, int[]>>();

    // Create instance of all of the modules
    int modules = application.getModulesSize();
    for (int i = 0; i < modules; i++) 
    {
      try 
      {
        // get module definition
        control4j.application.Module moduleDef = application.getModule(i);
        // get module class
        String className = moduleDef.getClassName();
        Class<Module> moduleClass
            = (Class<Module>)Class.forName(className);
        // create instance
        Module moduleInstance = instantiate(moduleDef);
        // create input map
        int[] inputMap = getInputMap(moduleDef, moduleClass);
        // create output map
        int[] outputMap = getOutputMap(moduleDef, moduleClass);
        // method initialization
        moduleInstance.initialize(moduleDef.getConfiguration());
        // resource assignment
        Set<String> resourceKeys = moduleDef.getResourceKeys();
        for (String key : resourceKeys)
        {
          control4j.application.Resource resourceDef 
              = moduleDef.getResource(key);
          Resource resource = Resource.getResource(
              resourceDef.getClassName(), resourceDef.getConfiguration());
          moduleInstance.putResource(key, resource);
        }
        // TODO: check that all of the resources are assigned
        if (moduleInstance instanceof InputModule)
        {
          if (outputMap != null) {} // TODO: outputs for input module !
          InputModule inputModule = (InputModule)moduleInstance;
          initializeInputModule(moduleDef, inputModule, inputMap);
          inputModules.add(
              new ImmutablePair<InputModule, int[]>(inputModule, inputMap));
        }
        else if (moduleInstance instanceof ProcessModule)
        {
          ProcessModule processModule = (ProcessModule)moduleInstance;
          initializeProcessModule(
              moduleDef, processModule, inputMap, outputMap);
          processModules.add(
              new ImmutableTriple<ProcessModule, int[], int[]>(
              processModule, inputMap, outputMap));
        }
        else if (moduleInstance instanceof OutputModule)
        {
          if (inputMap != null) {} // TODO: inputs for output module !
          OutputModule outputModule = (OutputModule)moduleInstance;
          initializeOutputModule(
              moduleDef, outputModule, outputMap);
          outputModules.add(
              new ImmutablePair<OutputModule, int[]>(outputModule, outputMap));
        }
        else
        {
          // TODO: Not a module
        }
      }
      catch (Exception e)
      {
        // TODO:
      }
    }

    Application result = new Application();
    return result;
  }

  /**
   *  Provides configuration of the given module that is specific
   *  for input modules.
   */
  protected void initializeInputModule(
      control4j.application.Module moduleDef, InputModule module, int[] map)
  {
    for (int j = 0; j < map.length; j++)
      if (map[j] >= 0)
      {
        module.setInputConfiguration(
            j, moduleDef.getInput(map[j]).getConfiguration());
      }
  }

  /**
   *  Provides configuration of the given module that is specific
   *  for process modules.
   */
  protected void initializeProcessModule(
      control4j.application.Module moduleDef, ProcessModule module,
      int[] inputMap, int[] outputMap)
  {
    for (int j = 0; j < inputMap.length; j++)
      if (inputMap[j] >= 0)
      {
        module.setInputConfiguration(
            j, moduleDef.getInput(inputMap[j]).getConfiguration());
      }
    for (int j = 0; j < outputMap.length; j++)
      if (outputMap[j] >= 0)
      {
        module.setOutputConfiguration(
            j, moduleDef.getOutput(outputMap[j]).getConfiguration());
      }
  }

  /**
   *  Provides configuration of the given module that is specific
   *  for output modules.
   */
  protected void initializeOutputModule(
      control4j.application.Module moduleDef, OutputModule module, int[] map)
  {
    for (int j = 0; j < map.length; j++)
      if (map[j] >= 0)
      {
        module.setOutputConfiguration(
            j, moduleDef.getOutput(map[j]).getConfiguration());
      }
  }

  /**
   *  Create and return an instance of the module which correspond
   *  to the given module definition.
   */
  protected Module instantiate(control4j.application.Module moduleDef)
  {
    try
    {
      // get module class
      String className = moduleDef.getClassName();
      Class<Module> moduleClass
          = (Class<Module>)Class.forName(className);

      // create the instance
      Module instance = moduleClass.newInstance();
    }
    catch (ClassNotFoundException e)
    {
      // TODO:
    }
    catch (InstantiationException e)
    {
      // TODO:
    }
    catch (IllegalAccessException e)
    {
      // TODO:
    }
    return null;
  }

  /**
   *  Creates and returns the input map for a given module definition.
   */
  protected int[] getInputMap(
      control4j.application.Module moduleDef, Class<Module> moduleClass) 
  {

    // get AVariableInput annotation, if any
    AVariableInput varInput 
        = moduleClass.getAnnotation(AVariableInput.class);

    // calculate required size of the map array
    int varInputSize = moduleDef.getVariableInputSize();
    if (varInputSize > 0 && varInput == null) 
    {
      // TODO: variable input is not supported!
    } 
    else if (varInput != null)
    {
      int varInputIndex = varInput.startIndex();
      moduleDef.setVariableInputStartIndex(varInputIndex);
      // TODO: index collision
    }
    int mapSize = moduleDef.getInputSize();
    if (mapSize == 0) return null;

    // allocate the map array
    int[] map = new int[mapSize];
    Arrays.fill(map, -1);

    // fixed index input
    for (int i=0; i<mapSize; i++)
    {
      map[i] = moduleDef.getFixedInputMap().get(i);
    }

    return map;
  }

  /**
   *  Creates and returns the output map for a given module definition.
   */
  protected int[] getOutputMap(
      control4j.application.Module moduleDef, Class<Module> moduleClass)
  {
    // get AVariableOutput annotation, if any
    AVariableOutput varOutput 
        = moduleClass.getAnnotation(AVariableOutput.class);

    // calculate required size of the map array
    int varOutputSize = moduleDef.getVariableOutputSize();
    if (varOutputSize > 0 && varOutput == null)
    {
      // TODO: variable output is not supported!
    }
    else if (varOutput != null)
    {
      int varOutputIndex = varOutput.startIndex();
      moduleDef.setVariableOutputStartIndex(varOutputIndex);
      // TODO: index collision
    }
    int mapSize = moduleDef.getOutputSize();
    if (mapSize == 0) return null;

    // allocate the map array
    int[] map = new int[mapSize];
    Arrays.fill(map, -1);

    // fixed index output
    for (int i = 0; i < mapSize; i++)
    {
      map[i] = moduleDef.getFixedOutputMap().get(i);
    }

    return map;
  }
}
