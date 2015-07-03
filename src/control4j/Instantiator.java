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
import control4j.application.ErrorManager;
import control4j.application.ErrorCode;

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
public class Instantiator {

  private ControlLoop handler;

  public Instantiator(ControlLoop handler) {
    this.handler = handler;
  }

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
  public void instantiate(control4j.application.Module moduleDef) {

    String className = moduleDef.getClassName();
    try {
      Class<Module> moduleClass
        = (Class<Module>)Class.forName(className);
      // create instance
      Module moduleInstance = moduleClass.newInstance();
      // create input map
      int[] inputMap = getInputMap(moduleDef, moduleInstance);
      // create output map
      int[] outputMap = getOutputMap(moduleDef, moduleInstance);
      // method initialization
      moduleInstance.initialize(moduleDef);
      // specific
      ModuleCrate moduleCrate
        = ModuleCrate.create(moduleInstance, inputMap, outputMap);
      handler.add(moduleCrate);
      // TODO: Register as an ICycleListener
      if (moduleInstance instanceof ICycleEventListener) {
        handler.addCycleEventListener((ICycleEventListener)moduleInstance);
      }
    } catch (ClassNotFoundException e) {
      reportModuleClassNotFound(e, className);
    } catch (InstantiationException e) {
      reportModuleInstantiationException(e, className);
    } catch (IllegalAccessException e) {
      reportModuleIllegalAccessException(e, className);
    }

  }

  public void set(String key, String value) {
    handler.set(key, value);
  }

  /**
   *  Creates and returns the input map for a given module definition.
   */
  protected int[] getInputMap(
      control4j.application.Module moduleDef, Module module) {

    // resolve variable indexes
    if (moduleDef.hasVariableInput()) {
      try {
        moduleDef.setVariableInputStartIndex(
            module.getVariableInputFirstIndex());
      } catch (UnsupportedOperationException e) {
        // module definition contains variable input, but the module
        // doesn't support it.
        ErrorManager.newError()
          .setCode(ErrorCode.INPUT_MAP)
          .setCause(e);
        return null;
      }
    }

    // calculate required size of the map array
    int mapSize = module.getInputSize(moduleDef.getInputSize() - 1);
    if (mapSize == 0) return null;

    // allocate the map array
    int[] map = new int[mapSize];
    Arrays.fill(map, -1);

    // fixed index input
    for (int i=0; i<mapSize; i++) {
      Input input = moduleDef.getInput(i);
      if (input != null) {
        map[i] = input.getPointer();
      }
    }

    return map;
  }

  /**
   *  Creates and returns the output map for a given module definition.
   */
  protected int[] getOutputMap(
      control4j.application.Module moduleDef, Module module) {

    // resolve variable indexes
    if (moduleDef.hasVariableOutput()) {
      try {
        moduleDef.setVariableOutputStartIndex(
            module.getVariableOutputFirstIndex());
      } catch (UnsupportedOperationException e) {
        // module definition contains variable input, but the module
        // doesn't support it.
        ErrorManager.newError()
          .setCode(ErrorCode.OUTPUT_MAP)
          .setCause(e);
        return null;
      }
    }

    int mapSize = module.getOutputSize(moduleDef.getOutputSize() - 1);
    if (mapSize == 0) return null;

    // allocate the map array
    int[] map = new int[mapSize];
    Arrays.fill(map, -1);

    // fixed index output
    for (int i = 0; i < mapSize; i++) {
      Output output = moduleDef.getOutput(i);
      if (output != null) {
        map[i] = output.getPointer();
      }
    }

    return map;
  }

  protected void reportModuleClassNotFound(Throwable e, String className)
  {
    severe(java.text.MessageFormat.format(
        "A module class: {0} was not found\n{1}", className, e.getMessage()));
    System.exit(1);
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

}
