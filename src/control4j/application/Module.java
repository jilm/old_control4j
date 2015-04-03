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

import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import control4j.tools.DeclarationReference;
import static control4j.tools.Logger.*;

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
   */

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

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    toString("", sb);
    return sb.toString();
  }

  @Override
  void toString(String indent, StringBuilder sb)
  {
    sb.append(indent)
      .append("Module class=")
      .append(className)
      .append("\n");

    String indent2 = indent + "  ";
    String indent3 = indent2 + "  ";

    // write configuration
    super.toString(indent2, sb);

    // write resources
    if ((resources != null && resources.size() > 0)
	|| (resourceRefs != null && resourceRefs.size() > 0))
    {
      sb.append(indent2).append("Resources\n");
      if (resources != null)
	Helper.objectToString(resources, indent3, sb);
      if (resourceRefs != null)
	sb.append(resourceRefs.toString());
        {}//Helper.toString(resourceRefs, indent3, sb); // TODO
    }

    // write input
    if ((inputArray != null && inputArray.size() > 0)
	|| (variableInput != null && variableInput.size() > 0)
	|| (inputTags != null && inputTags.size() > 0))
    {
      sb.append(indent2).append("Module Input\n");
      if (inputArray != null)
        for (int i=0; i<inputArray.size(); i++)
	  if (inputArray.get(i) != null)
	  {
	    sb.append(indent3).append("Input[").append(i).append("]=");
	    inputArray.get(i).toString(indent3, sb);
	  }
      if (variableInput != null)
        for (Input input : variableInput)
        {
	  sb.append(indent3).append("Input[-]=");
	  input.toString(indent3, sb);
        }
      if (inputTags != null)
        sb.append(indent3)
	  .append("Input Tags")
	  .append(inputTags.toString())
	  .append("\n");
    }

    // write output
    if ((outputArray != null && outputArray.size() > 0)
	|| (variableOutput != null && variableOutput.size() > 0)
	|| (outputTags != null && outputTags.size() > 0))
    {
      sb.append(indent2).append("Module Output\n");
      if (outputArray != null)
        for (int i=0; i<outputArray.size(); i++)
	  if (outputArray.get(i) != null)
	  {
	    sb.append(indent3).append("Output[").append(i).append("]=");
	    outputArray.get(i).toString(indent3, sb);
	  }
      if (variableOutput != null)
        for (Output output : variableOutput)
        {
	  sb.append(indent3).append("Output[-]=");
	  output.toString(indent3, sb);
        }
      if (outputTags != null)
        sb.append(indent3)
	  .append("Output Tags")
	  .append(outputTags.toString())
	  .append("\n");
    }
  }

}
