package control4j.application;

/*
 *  Copyright 2013, 2014, 2015 Jiri Lidinsky
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import control4j.tools.DeclarationReference;

/**
 *
 *  Crate class for module definition. Contains all the information needed
 *  to create and configure an instance of a class which implements the 
 *  functionality of the module.
 *
 */
public class Module extends Configurable
{

  /** 
   *  Name of the class which implements functionality of that module.
   *  It may not contain empty string or null value. 
   */
  private String className;

  /**
   *  Returns the name of the class that implements the functionality
   *  of the module.
   *
   *  @return class name, it will never be null
   */
  public String getClassName()
  {
    return className;
  }

  /** 
   *  Array of the input references with fixed index.
   */
  private ArrayList<Input> inputArray;

  /**
   *  Puts given input reference to the specified index.
   */
  public void putInput(int index, Input input)
  {
    if (inputArray == null)
      inputArray = new ArrayList<Input>();
    inputArray.set(index, input);
    // TODO input with duble indexes
  }

  /**
   *  Returns the highest assigned index plus one.
   */
  public int getInputSize()
  {
    if (inputArray == null) 
      return 0;
    else
      return inputArray.size();
  }

  /**
   *  Returns input with given index.
   */
  public Input getInput(int index)
  {
    if (inputArray == null) {} // TODO
    return inputArray.get(index);
  }

  /** Buffer for input references without index specified. */
  private ArrayList<Input> variableInput;

  /**
   *  Adds an input which doesn't have index attached.
   */
  public void putInput(Input input)
  {
    if (variableInput == null)
      variableInput = new ArrayList<Input>();
    variableInput.add(input);
  }

  /**
   *  Returns number of variable input signals.
   */
  public int getVariableInputSize()
  {
    if (variableInput == null) return 0;
    return variableInput.size();
  }

  /**
   *  Returns a variable input on given position.
   */
  public Input getVariableInput(int index)
  {
    if (variableInput == null) {} // TODO
    return variableInput.get(index);
  }

  /** 
   *  An array of the output definitions.
   */
  private ArrayList<Output> outputArray;

  /**
   *  Puts given output reference to the specified index.
   */
  public void putOutput(int index, Output output)
  {
    if (outputArray == null)
      outputArray = new ArrayList<Output>();
    outputArray.set(index, output);
    // TODO output with duble indexes
  }

  /**
   *  Returns the highest assigned index plus one.
   */
  public int getOutputSize()
  {
    if (outputArray == null) 
      return 0;
    else
      return outputArray.size();
  }

  /**
   *  Returns output with given index.
   */
  public Output getOutput(int index)
  {
    if (outputArray == null) {} // TODO
    return outputArray.get(index);
  }

  /** Buffer for output references without index specified. */
  private ArrayList<Output> variableOutput;

  /**
   *  Adds an output which doesn't have index attached.
   */
  public void putOutput(Output output)
  {
    if (variableOutput == null)
      variableOutput = new ArrayList<Output>();
    variableOutput.add(output);
  }

  /**
   *  Returns number of variable output signals.
   */
  public int getVariableOutputSize()
  {
    if (variableOutput == null) return 0;
    return variableOutput.size();
  }

  /**
   *  Returns a variable output on given position.
   */
  public Output getVariableOutput(int index)
  {
    if (variableOutput == null) {} // TODO
    return variableOutput.get(index);
  }

  /**
   *  @param className
   *             name of the class that implements the functionality
   *             of the module. May not be null or empty string
   *
   *  @throws IllegalArgumentException
   *             if className is null or an empty string
   */
  public Module(String className)
  {
    if (className == null || className.length() == 0)
      throw new IllegalArgumentException();
    else
      this.className = className;
  }

  /** References to the resource definitions. */
  private HashMap<String, Reference> resourceRefs;

  /**
   *  Puts a reference to some resource definition.
   */
  public void putResource(String key, String href, Scope scope)
  {
    if (resourceRefs == null)
      resourceRefs = new HashMap<String, Reference>();
    resourceRefs.put(key, new Reference(href, scope));
  }

  /** Resource definitions. */
  private HashMap<String, Resource> resources;

  /**
   *  Puts a resource definition.
   */
  public void putResource(String key, Resource resource)
  {
    if (resources == null)
      resources = new HashMap<String, Resource>();
    resources.put(key, resource);
  }

  /** A set of input tags. */
  private HashSet<String> inputTags;

  public void addInputTag(String name)
  {
    if (inputTags == null)
      inputTags = new HashSet<String>();
    inputTags.add(name);
  }

  /** A set of output tags. */
  private HashSet<String> outputTags;

  public void addOutputTag(String name)
  {
    if (outputTags == null)
      outputTags = new HashSet<String>();
    outputTags.add(name);
  }

}
