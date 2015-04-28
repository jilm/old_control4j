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
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import control4j.tools.DeclarationReference;
import static control4j.tools.Logger.*;

import cz.lidinsky.tools.ToStringBuilder;

/**
 *
 *  Crate class for module definition. Contains all the information needed
 *  to create and configure an instance of the class which implements the 
 *  functionality of the module.
 *
 */
public class Module extends Configurable
{

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
   *  This array may contain null values!
   */
  private ArrayList<Input> inputArray;

  /**
   *  Puts given input reference to the specified index.
   */
  public void putInput(int index, Input input)
  {
    if (inputArray == null)
      inputArray = new ArrayList<Input>();
    // if the array list is not big enough
    inputArray.ensureCapacity(index+1);
    while (index >= inputArray.size())
      inputArray.add(null);
    // add the input into the list
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
    // if the array list is not big enough
    outputArray.ensureCapacity(index+1);
    while (index >= outputArray.size())
      outputArray.add(null);
    // add the input into the list
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

  /*
   *
   *     Resource References.
   *
   */

  /** References to the resource definitions. */
  private ArrayList<Triple<String, String, Scope>> resourceRefs
      = new ArrayList<Triple<String, String, Scope>>();

  /**
   *  Puts a reference to some resource definition.
   */
  public void putResource(String key, String href, Scope scope)
  {
    resourceRefs.add(new ImmutableTriple(key, href, scope));
    fine("New resource reference added");
  }

  public int getResourceRefsSize()
  {
    return resourceRefs.size();
  }

  public Triple<String, String, Scope> getResourceRef(int index)
  {
    return resourceRefs.get(index);
  }

  public void removeResourceRef(int index)
  {
    resourceRefs.remove(index);
  }

  /*
   *
   *     Resource Definitions.
   *
   *     Each resource of the module is identified by a unique key.
   *
   */

  /** Resource definitions. */
  private HashMap<String, Resource> resources 
      = new HashMap<String, Resource>();

  /**
   *  Puts a resource definition.
   */
  public void putResource(String key, Resource resource)
  {
    resources.put(key, resource);
  }

  /**
   *  Returns all of the resouce keys.
   */
  public Set<String> getResourceKeys()
  {
    return resources.keySet();
  }

  public Resource getResource(String key)
  {
    return resources.get(key); // TODO
  }

  /*
   *
   *     Input Tags
   *
   */

  /** A set of input tags. */
  private HashSet<String> inputTags;

  public void addInputTag(String name)
  {
    if (inputTags == null)
      inputTags = new HashSet<String>();
    inputTags.add(name);
  }

  public int getInputTagsSize()
  {
    if (inputTags == null) return 0;
    return inputTags.size();
  }

  public boolean containsInputTag(String name)
  {
    if (inputTags == null) return false;
    return inputTags.contains(name);
  }

  /** A set of output tags. */
  private HashSet<String> outputTags;

  public void addOutputTag(String name)
  {
    if (outputTags == null)
      outputTags = new HashSet<String>();
    outputTags.add(name);
  }

  public int getOutputTagsSize()
  {
    if (outputTags == null) return 0;
    return outputTags.size();
  }

  public boolean containsOutputTag(String name)
  {
    if (outputTags == null) return false;
    return outputTags.contains(name);
  }

  /*
   *
   *     Input Map.
   *
   *     There are two kinds of input. Input with explicitly given
   *     index, and input without it.
   *
   */

  private ArrayList<Integer> fixedInputMap = new ArrayList<Integer>();
  private ArrayList<Integer> variableInputMap = new ArrayList<Integer>();

  public void putInputSignalIndex(int index, int signalIndex)
  {
    while (index >= fixedInputMap.size())
      fixedInputMap.add(-1);
    fixedInputMap.set(index, signalIndex);
  }

  public void addInputSignalIndex(int signalIndex)
  {
    variableInputMap.add(signalIndex);
  }

  public List<Integer> getFixedInputMap()
  {
    return fixedInputMap;
  }

  public List<Integer> getVariableInputMap()
  {
    return variableInputMap;
  }

  public void setVariableInputStartIndex(int index)
  {
    if (variableInputMap.size() == 0)
    {
      return;
    }
    else if (fixedInputMap.size() > index)
    {
      // TODO indexes colision
    }
    while (fixedInputMap.size() < index)
    {
      fixedInputMap.add(-1);
      inputArray.add(null);
    }
    for (int i = 0; i < variableInputMap.size(); i++)
    {
      fixedInputMap.add(variableInputMap.get(i));
      inputArray.add(variableInput.get(i));
    }
    variableInputMap.clear();
    variableInput.clear();
  }

  /*
   *
   *     Output Map.
   *
   *     There are two kinds of output. Output with explicitly given
   *     index, and output without it.
   *
   */

  private ArrayList<Integer> fixedOutputMap = new ArrayList<Integer>();
  private ArrayList<Integer> variableOutputMap = new ArrayList<Integer>();

  public void putOutputSignalIndex(int index, int signalIndex)
  {
    while (index >= fixedOutputMap.size())
      fixedOutputMap.add(-1);
    fixedOutputMap.set(index, signalIndex);
  }

  public void addOutputSignalIndex(int signalIndex)
  {
    variableOutputMap.add(signalIndex);
  }

  public List<Integer> getFixedOutputMap()
  {
    return fixedOutputMap;
  }

  public List<Integer> getVariableOutputMap()
  {
    return variableOutputMap;
  }

  public void setVariableOutputStartIndex(int index)
  {
    if (variableOutputMap.size() == 0)
    {
      return;
    }
    else if (fixedOutputMap.size() > index)
    {
      // TODO indexes colision
    }
    while (fixedOutputMap.size() < index)
    {
      fixedOutputMap.add(-1);
      outputArray.add(null);
    }
    for (int i = 0; i < variableOutputMap.size(); i++)
    {
      fixedOutputMap.add(variableOutputMap.get(i));
      outputArray.add(variableOutput.get(i));
    }
    variableOutputMap.clear();
    variableOutput.clear();
  }

  /*
   *
   *     To String.
   *
   */

  @Override
  public void toString(ToStringBuilder builder)
  {
    super.toString(builder);
    builder.append("className", className)
        .append("inputArray", inputArray)
        .append("variableInput", variableInput)
        .append("outputArray", outputArray)
        .append("variableOutput", variableOutput)
        .append("resources", resources)
        .append("resourceRefs", resourceRefs)
        .append("inputTags", inputTags)
        .append("outputTags", outputTags)
        .append("fixedInputMap", fixedInputMap)
        .append("variableInputMap", variableInputMap)
        .append("fixedOutputMap", fixedOutputMap)
        .append("variableOutputMap", variableOutputMap);
  }

}
