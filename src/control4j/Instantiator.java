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

import control4j.application.ErrorCode;
import control4j.application.ErrorManager;
import control4j.application.Input;
import control4j.application.Output;
import control4j.application.Phase;

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;

import org.apache.commons.collections4.Transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/**
 *
 *  A class which is responsible for modules instantiation.
 *
 */
public class Instantiator
implements Transformer<control4j.application.Module, ModuleCrate> {

  public Instantiator() { }

  /**
   *  Creates the instance of the required module. The sequence is as
   *  follows:
   *  <ol>
   *    <li>Creates instance of the module. The constructor without
   *        params is called.
   *    <li>Calls {@link Module#initialize} method.
   *    <li>Register as an ICycleListener, if neccessary.
   *  </ol>
   *
   *  <p>Created instance is passed to the handler object.
   *
   *  <p>Problems during the process of instantiation are reported into
   *  the {@link control4j.application.ErrorManager} object.
   *
   *  @param moduleDef
   *             object that contains definition the the requested module
   */
  public ModuleCrate transform(control4j.application.Module moduleDef) {

    String className = moduleDef.getClassName();
    try {
      Class<Module> moduleClass
        = (Class<Module>)Class.forName(className);
      // create the instance
      Module moduleInstance = moduleClass.newInstance();
      // create the input map
      int[] inputMap = getInputMap(moduleDef, moduleInstance);
      // create the output map
      int[] outputMap = getOutputMap(moduleDef, moduleInstance);
      // method initialization
      moduleInstance.initialize(moduleDef);
      // create the module crate
      ModuleCrate moduleCrate
        = ModuleCrate.create(moduleInstance, inputMap, outputMap);
      // put module instance into the handler
      //handler.add(moduleCrate);
      // Register as an ICycleListener
      //if (moduleInstance instanceof ICycleEventListener) {
        //handler.addCycleEventListener((ICycleEventListener)moduleInstance);
      //}
      return moduleCrate;
    } catch (Exception e) {
      ErrorManager.newError()
        .setPhase(Phase.MODULE_INSTANTIATION)
        .setCause(
            new CommonException()
            .setCause(e)
            .set("class", className)
            .set("reference", moduleDef.getDeclarationReferenceText()));
      throw new CommonException()
        .setCause(e);
    }

  }

  //public void set(String key, String value) {
    //handler.set(key, value);
  //}

  /**
   *  Creates and returns the input map for the given module definition.
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

}
