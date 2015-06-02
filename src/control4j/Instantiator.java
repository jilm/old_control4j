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

import static control4j.tools.Logger.catched;
import static control4j.tools.Logger.severe;
import control4j.application.Input;
import control4j.application.Output;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import cz.lidinsky.scorpio.OMDA;

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
   *    <li>Creates instance of the module. The constructor of the
   *        module is called.
   *    <li>Calls {@link Module#initialize} method.
   *    <li>Assignes the resources. For each resource calls
   *        {@link Module#putResource} method.
   *    <li>Calls initialization input and output methods of the Module.
   *    <li>Register as a ICycleListener.
   *  </ol>
   */
  public Application instantiate(
      control4j.application.Application application) 
  {

    OMDA omda = new OMDA();
    // preparation
    applicationDef = application;
    ArrayList<Pair<InputModule, int[]>> inputModules 
        = new ArrayList<Pair<InputModule, int[]>>();
    ArrayList<Triple<ProcessModule, int[], int[]>> processModules
        = new ArrayList<Triple<ProcessModule, int[], int[]>>();
    ArrayList<Pair<OutputModule, int[]>> outputModules
        = new ArrayList<Pair<OutputModule, int[]>>();

    // Create instances of all of the modules
    int modules = application.getModulesSize();
    for (int i = 0; i < modules; i++) 
    {
      // get module definition
      control4j.application.Module moduleDef = application.getModule(i);
      // get module class
      String className = moduleDef.getClassName();
      try 
      {
        Class<Module> moduleClass
            = (Class<Module>)Class.forName(className);
	    //= (Class<Module>)ClassLoader.getSystemClassLoader().loadClass(className);
        // create instance
        Module moduleInstance = moduleClass.newInstance();
        // create input map
        int[] inputMap = getInputMap(moduleDef, moduleInstance);
        // create output map
        int[] outputMap = getOutputMap(moduleDef, moduleInstance);
        // method initialization
        moduleInstance.initialize(moduleDef.getConfiguration());
        // resource assignment
        ResourceManager resourceManager = ResourceManager.getInstance();
        Set<String> resourceKeys = moduleDef.getResourceKeys();
        for (String key : resourceKeys)
        {
          control4j.application.Resource resourceDef 
              = moduleDef.getResource(key);
          Class<Resource> resourceClass
              = (Class<Resource>)Class.forName(resourceDef.getClassName());
          Resource resource = resourceManager.getResource(resourceDef);
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
        // TODO: Register as an ICycleListener
      }
      catch (ClassNotFoundException e)
      {
        reportModuleClassNotFound(e, className);
      }
      catch (InstantiationException e)
      {
        reportModuleInstantiationException(e, className);
      }
      catch (IllegalAccessException e)
      {
        reportModuleIllegalAccessException(e, className);
      }
      //catch (Exception e)
      //{
        // TODO:
        //catched(getClass().getName(), "instantiate", e);
      //}
    }

    // return result
    Application result = new Application();
    // store modules
    result.inputModules
        = (Pair<InputModule, int[]>[])java.lang.reflect.Array.newInstance(
        Pair.class, inputModules.size());
    inputModules.toArray(result.inputModules);
    result.processModules
        = (Triple<ProcessModule, int[], int[]>[])
        java.lang.reflect.Array.newInstance(
        Triple.class, processModules.size());
    processModules.toArray(result.processModules);
    result.outputModules
        = (Pair<OutputModule, int[]>[])java.lang.reflect.Array.newInstance(
        Pair.class, outputModules.size());
    outputModules.toArray(result.outputModules);
    // data buffer size
    result.dataBufferSize = applicationDef.getSignalsSize();
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
            j, moduleDef.getInput(j).getConfiguration());
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
            j, moduleDef.getInput(j).getConfiguration());
      }
    for (int j = 0; j < outputMap.length; j++)
      if (outputMap[j] >= 0)
      {
        module.setOutputConfiguration(
            j, moduleDef.getOutput(j).getConfiguration());
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
            j, moduleDef.getOutput(j).getConfiguration());
      }
  }

  /**
   *  Creates and returns the input map for a given module definition.
   */
  protected int[] getInputMap(
      control4j.application.Module moduleDef, Module module)
  {
    // calculate required size of the map array
    int varInputSize = moduleDef.getVariableInputSize();
    if (varInputSize > 0 && !module.isVariableInputSupported())
    {
      throw new SyntaxErrorException(java.text.MessageFormat.format(
          "Variable input is not supported by the module {0}",
          module.getClass().getName()));
    } 
    else if (module.isVariableInputSupported())
    {
      moduleDef.setVariableInputStartIndex(
          module.getVariableInputFirstIndex());
      // TODO: index collision
    }
    int mapSize = module.getInputSize(moduleDef.getInputSize() - 1);
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
      control4j.application.Module moduleDef, Module module)
  {
    // calculate required size of the map array
    int varOutputSize = moduleDef.getVariableOutputSize();
    if (varOutputSize > 0 && !module.isVariableOutputSupported())
    {
      throw new SyntaxErrorException(java.text.MessageFormat.format(
          "Variable output is not supported by the module {0}",
          module.getClass().getName()));
    }
    else if (module.isVariableOutputSupported())
    {
      moduleDef.setVariableOutputStartIndex(
          module.getVariableOutputFirstIndex());
      // TODO: index collision
    }
    int mapSize = module.getOutputSize(moduleDef.getOutputSize() - 1);
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

  protected void reportModuleClassNotFound(Throwable e, String className)
  {
    severe(java.text.MessageFormat.format(
        "A module class: {0} was not found\n{1}", className, e.getMessage()));
  }

  protected void reportModuleInstantiationException(
      Throwable e, String className)
  {
    severe(java.text.MessageFormat.format(
        "It was not possible to create instance of a module with class name:"
        + " {0}; the message of the exception:\n{1}", className,
        e.getMessage()));
  }

  protected void reportModuleIllegalAccessException(
      Throwable e, String className)
  {
    severe(java.text.MessageFormat.format(
        "IllegalAccessException while module instance creation."
        + " Module class name: {0}; exception message:\n{1}",
        className, e.getMessage()));
  }

  public static void main(String[] args) throws Exception
  {
    java.io.File file = new java.io.File(args[0]);
    control4j.application.Loader loader
        = new control4j.application.Loader();
    control4j.application.Application application
        = loader.load(file);
    control4j.application.Preprocessor preprocessor
        = new control4j.application.Preprocessor();
    preprocessor.process(application);
    control4j.application.Sorter sorter
        = new control4j.application.Sorter();
    sorter.process(application);
    Instantiator instantiator = new Instantiator();
    Application instances = instantiator.instantiate(application);
    System.out.println(instances.toString());
  }

}
