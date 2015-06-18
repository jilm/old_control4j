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

import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static control4j.tools.LogMessages.getMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import control4j.SyntaxErrorException;
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
public class Module extends Configurable {

  /**
   *  @param className
   *             name of the class that implements the functionality
   *             of the module. May not be null or empty string
   *
   *  @throws IllegalArgumentException
   *             if className is null or an empty string
   */
  public Module(String className) {
    this.className = trim(notBlank(className,
        getMessage("msg004", "className", getDeclarationReferenceText())));
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
  public String getClassName() {
    return className;
  }

  /**
   *  Array of the input references with fixed index.
   *  This array may contain null values!
   */
  private ArrayList<Input> inputArray = new ArrayList<Input>();

  /**
   *  Puts given input reference to the specified index.
   */
  public void putInput(int index, Input input) {
    notNull(input,
        getMessage("msg006", "input", getDeclarationReferenceText()));
    if (index < 0) {
      throw new IndexOutOfBoundsException(
          "Input index may not be a negative number: " + index);
    }
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

  public void putInput(Input input) {
    // TODO:
  }

  public void setVariableInputStartIndex(int index) {
    // TODO:
  }

  public void setVariableOutputStartIndex(int index) {
    // TODO:
  }

  /**
   *  Returns the highest assigned index plus one.
   */
  public int getInputSize() {
    if (inputArray == null)
      return 0;
    else
      return inputArray.size();
  }

  /**
   *  Returns input with given index.
   */
  public Input getInput(int index) {
    if (inputArray == null) {} // TODO
    return inputArray.get(index);
  }

  /**
   *  An array of the output definitions.
   */
  private ArrayList<Output> outputArray = new ArrayList<Output>();

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

  public void putOutput(Output output) {
    // TODO:
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

  private Resource singleResource;

  /**
   *  Puts a resource definition.
   */
  public void putResource(String key, Resource resource) {
    if (isBlank(key) && singleResource == null && resources.size() == 0) {
      this.singleResource = notNull(resource);
    } else if (!isBlank(key) && singleResource == null) {
      resources.put(trim(key), notNull(resource));
    } else {
      throw new SyntaxErrorException("The Resource Key property may be blank only if it is the only resource of the module!\n"); // TODO:
    }
  }

  /**
   *  Returns all of the resouce keys.
   */
  public Set<String> getResourceKeys() {
    if (singleResource != null) {
      return java.util.Collections.singleton(null);
    } else {
      return resources.keySet();
    }
  }

  public Resource getResource(String key) {
    if (isBlank(key) || singleResource != null) {
      return getResource();
    } else {
      Resource result = resources.get(key); // TODO
      if (result == null) {
        throw new NoSuchElementException();
      } else {
        return result;
      }
    }
  }

  public int getResourceSize() {
    if (singleResource != null) {
      return 1;
    } else {
      return resources.size();
    }
  }

  public Resource getResource() {
    if (singleResource != null) {
      return singleResource;
    } else if (resources.size() == 1) {
      return resources.values().iterator().next();
    } else {
      throw new IllegalStateException(); // TODO:
    }
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
        .append("outputArray", outputArray)
        .append("resources", resources);
  }

}
