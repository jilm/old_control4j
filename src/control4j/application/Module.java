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
import control4j.ExceptionCode;
import control4j.tools.DeclarationReference;
import static control4j.tools.Logger.*;

import cz.lidinsky.tools.ToStringBuilder;

/**
 *
 *  Crate class for a module definition. Contains all the information needed
 *  to create and configure the instance of the class which implements the
 *  functionality of the module.
 *
 */
public class Module extends Configurable {

  /**
   *  @param className
   *             name of the class that implements the functionality
   *             of the module. May not be null nor empty string
   *
   *  @throws IllegalArgumentException
   *             if className is null or an empty string
   */
  public Module(String className) {
    if (isBlank(className)) {
      throw new SyntaxErrorException()
        .setCode(ExceptionCode.ILLEGAL_ARGUMENT)
        .set("message", "Blank className")
        .set("method", "constructor")
        .set("class", getClass().getName());
    } else {
      this.className = trim(className);
    }
  }

  /**
   *  Name of the class which implements functionality of that module.
   *  It may not contain a blank value.
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

  //--------------------------------------------------------------------- Input

  /**
   *  Array of the input objects. Each input is placed on specifie index,
   *  so, the array may contain null values. Indexes of some input may not
   *  be resolved until the module instantiation. Such input objects are
   *  stored at the end of the buffer.
   */
  private ArrayList<Input> inputArray;

  /**
   *  A pointer into the inputArray buffer. It contains index of the first
   *  element of which index is not resolved yet.
   */
  private int variableInputIndex = 0;

  /**
   *  Adds given input reference to the specified index.
   *
   *  @param index
   *             index of the input
   *
   *  @param input
   *             an object to be added
   */
  public void putInput(int index, Input input) {
    // lazy buffer
    if (inputArray == null) {
      inputArray = new ArrayList<Input>();
    }
    // add input
    try {
      variableInputIndex = put(inputArray, index, input, variableInputIndex);
    } catch (SyntaxErrorException se) {
      throw new SyntaxErrorException()
        .setCode(ExceptionCode.DUPLICATE_ELEMENT)
        .setCause(se)
        .set("method", "putInput")
        .set("module", toString());
    }
  }

  /**
   *  Returns true if the definition contains at least one unresolved variable
   *  input.
   */
  public boolean hasVariableInput() {
    if (inputArray == null) {
      return false;
    } else {
      return variableInputIndex < inputArray.size();
    }
  }

  /**
   *  Adds an input reference with no index attached yet.
   *
   *  @param input
   *             an object to be added
   *
   *  @throws SyntaxErrorException
   *             if the input argument is null
   */
  public void putInput(Input input) {
    // param check
    if (input == null) {
      throw new SyntaxErrorException()
        .setCode(ExceptionCode.ILLEGAL_ARGUMENT)
        .set("message", "Null argument")
        .set("method", "putInput")
        .set("module", toString());
    }
    // lazy buffer
    if (inputArray == null) {
      inputArray = new ArrayList<Input>();
    }
    // add object
    inputArray.add(input);
  }

  /**
   *  Shifts the variable input elements inside the buffer to start
   *  on the given index. If there are no input elements or if there
   *  are no variable input elements, nothig happens.
   *
   *  @param index
   *             the desired index of the first variable input element
   *
   */
  public void setVariableInputStartIndex(int index) {
    try {
      if (inputArray == null) {
        // if there is no input at all, do nothing
      } else if (inputArray.size() == variableInputIndex) {
        // if there is no variable input, do nothing
      } else if (index < 0) {
        throw new SyntaxErrorException()
          .setCode(ExceptionCode.INDEX_OUT_OF_BOUNDS)
          .set("message", "Negative index")
          .set("index", index);
      } else if (index < variableInputIndex) {
        throw new SyntaxErrorException()
          .set("message", "Module input index colision")
          .set("input array size", inputArray.size())
          .set("variable index", variableInputIndex)
          .set("desired index", index);
      } else {
        shift(inputArray, variableInputIndex, index - variableInputIndex);
        variableInputIndex = inputArray.size();
      }
    } catch (SyntaxErrorException se) {
      se.set("method", "setVariableInputStartIndex")
        .set("module", toString());
      throw se;
    }
  }

  /**
   *  Returns the highest assigned index plus one.
   */
  public int getInputSize() {
    if (inputArray == null) {
      return 0;
    } else {
      return inputArray.size();
    }
  }

  /**
   *  Returns input with given index.
   */
  public Input getInput(int index) {
    try {
      if (inputArray == null) {
        throw new IndexOutOfBoundsException();
      } else {
        return inputArray.get(index);
      }
    } catch (IndexOutOfBoundsException e) {
      throw new SyntaxErrorException()
        .setCause(e)
        .set("method", "getInput")
        .set("index", index)
        .set("module", toString());
    }
  }

  //-------------------------------------------------------------------- Output

  /**
   *  Array of the output objects. Each output is placed on specifie index,
   *  so, the array may contain null values. Indexes of some output may not
   *  be resolved until the module instantiation. Such output objects are
   *  stored at the end of the buffer.
   */
  private ArrayList<Output> outputArray;

  /**
   *  A pointer into the outputArray buffer. It contains index of the first
   *  element of which index is not resolved yet.
   */
  private int variableOutputIndex = 0;

  /**
   *  Adds given output reference to the specified index.
   *
   *  @param index
   *             index of the output
   *
   *  @param output
   *             an object to be added
   */
  public void putOutput(int index, Output output) {
    // lazy buffer
    if (outputArray == null) {
      outputArray = new ArrayList<Output>();
    }
    // add output
    try {
      variableOutputIndex
        = put(outputArray, index, output, variableOutputIndex);
    } catch (SyntaxErrorException se) {
      throw new SyntaxErrorException()
        .setCause(se)
        .set("method", "putOutput")
        .set("module", toString());
    }
  }

  /**
   *  Returns true if the definition contains at least one unresolved variable
   *  output.
   */
  public boolean hasVariableOutput() {
    if (outputArray == null) {
      return false;
    } else {
      return variableOutputIndex < outputArray.size();
    }
  }

  /**
   *  Adds an output reference with no index attached yet.
   *
   *  @param output
   *             an object to be added
   *
   *  @throws SyntaxErrorException
   *             if the output argument is null
   */
  public void putOutput(Output output) {
    // param check
    if (output == null) {
      throw new SyntaxErrorException()
        .setCode(ExceptionCode.ILLEGAL_ARGUMENT)
        .set("message", "Null argument")
        .set("method", "putOutput")
        .set("module", toString());
    }
    // lazy buffer
    if (outputArray == null) {
      outputArray = new ArrayList<Output>();
    }
    // add object
    outputArray.add(output);
  }

  /**
   *  Shifts the variable output elements inside the buffer to start
   *  on the given index. If there are no output elements or if there
   *  are no variable output elements, nothig happens.
   *
   *  @param index
   *             the desired index of the first variable output element
   *
   */
  public void setVariableOutputStartIndex(int index) {
    try {
      if (outputArray == null) {
        // if there is no output at all, do nothing
      } else if (outputArray.size() == variableOutputIndex) {
        // if there is no variable output, do nothing
      } else if (index < 0) {
        throw new SyntaxErrorException()
          .setCode(ExceptionCode.INDEX_OUT_OF_BOUNDS)
          .set("message", "Negative index")
          .set("index", index);
      } else if (index < variableOutputIndex) {
        throw new SyntaxErrorException()
          .set("message", "Module output index colision")
          .set("output array size", outputArray.size())
          .set("variable index", variableOutputIndex)
          .set("desired index", index);
      } else {
        shift(outputArray, variableOutputIndex, index - variableOutputIndex);
        variableOutputIndex = outputArray.size();
      }
    } catch (SyntaxErrorException se) {
      se.set("method", "setVariableOutputStartIndex")
        .set("module", toString());
      throw se;
    }
  }

  /**
   *  Returns the highest assigned index plus one.
   */
  public int getOutputSize() {
    if (outputArray == null) {
      return 0;
    } else {
      return outputArray.size();
    }
  }

  /**
   *  Returns output with given index.
   */
  public Output getOutput(int index) {
    try {
      if (outputArray == null) {
        throw new IndexOutOfBoundsException();
      } else {
        return outputArray.get(index);
      }
    } catch (IndexOutOfBoundsException e) {
      throw new SyntaxErrorException()
        .setCause(e)
        .set("index", index)
        .set("method", "getOutput")
        .set("module", toString());
    }
  }

  //----------------------------------------------------------------- Resources

  /** Resource definitions. */
  private HashMap<String, Resource> resources
      = new HashMap<String, Resource>();

  private Resource singleResource;

  /**
   *  Puts a resource definition.
   */
  public void putResource(String key, Resource resource) {
    try {
      if (isBlank(key) && getResourceSize() == 0) {
        // key not specified -> it is the only resource
        singleResource = notNull(resource);
      } else if (!isBlank(key) && singleResource == null) {
        // key specified -> multiple resources
        resources.put(trim(key), notNull(resource));
      } else if (isBlank(key)) {
        // key not specified
        throw new SyntaxErrorException()
          .setCode(ExceptionCode.ILLEGAL_ARGUMENT)
          .set("message", "Module multiple resources, key not specified")
          .set("key", key)
          .set("resource", resource);
      } else {
        // key specified, but single resource has been already put
        throw new SyntaxErrorException()
          .setCode(ExceptionCode.ILLEGAL_ARGUMENT)
          .set("message",
              "Module multiple resources, there is a keylees resource");
      }
    } catch (SyntaxErrorException se) {
      se.set("method", "putResource")
        .set("class", getClass().getName())
        .set("module", toString());
      throw se;
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
        throw new SyntaxErrorException()
          .setCode(ExceptionCode.NO_SUCH_ELEMENT)
          .set("key", key)
          .set("method", "getResource")
          .set("class", getClass().getName())
          .set("module", toString());
      } else {
        return result;
      }
    }
  }

  /**
   *  Returns total number of attached resources.
   */
  public int getResourceSize() {
    if (singleResource != null) {
      return 1;
    } else if (resources == null) {
      return 0;
    } else {
      return resources.size();
    }
  }

  /**
   *  Returns an attached resource if it is the only resource.
   */
  public Resource getResource() {
    if (singleResource != null) {
      return singleResource;
    } else if (resources.size() == 1) {
      return resources.values().iterator().next();
    } else {
      throw new SyntaxErrorException()
        .setCode(ExceptionCode.ILLEGAL_STATE)
        .set("message", "Not only resource of the module")
        .set("method", "getResource()")
        .set("class", getClass().getName())
        .set("module", toString());
    }
  }

  //----------------------------------------------------------- Private Methods

  /**
   *  Place given element into the given list at the specified index.
   *
   */
  private static <T> int put(
      ArrayList<T> list,
      int index,
      T element,
      int variableIndex) {

    try {
      // argument check
      if (element == null) {
        throw new SyntaxErrorException()
          .setCode(ExceptionCode.ILLEGAL_ARGUMENT)
          .set("message", "Null argument")
          .set("element", element);
      }
      if (index < 0) {
        throw new SyntaxErrorException()
          .setCode(ExceptionCode.INDEX_OUT_OF_BOUNDS)
          .set("message", "Negative index")
          .set("index", index);
      }
      // if the list is not big enough
      shift(list, variableIndex, index - variableIndex + 1);
      variableIndex = index + 1;
      // place the element
      list.set(index, element);
      return variableIndex;
    } catch (SyntaxErrorException se) {
      se.set("method", "put")
        .set("class", "application.Module");
      throw se;
    }
  }

  /**
   *  Enlarge given list.
   *
   *  @param list
   *             a list to enlarge
   *
   *  @param index
   *             where the null values shoud be placed. May not be
   *             negative number and may not be greater than size
   *             of the list.
   *
   *  @param len
   *             how many null values shoud be inserted. May be zero
   *             or negative number, in such a case nothing happens
   */
  private static <T> void shift(ArrayList<T> list, int index, int len) {
    for (int i = 0; i < len; i++) {
      list.add(index, null);
    }
  }

  //--------------------------------------------------------------------- Other

  @Override
  public void toString(ToStringBuilder builder) {
    super.toString(builder);
    builder.append("className", className)
        .append("inputArray", inputArray)
        .append("variableInputIndex", variableInputIndex)
        .append("outputArray", outputArray)
        .append("variableOutputIndex", variableOutputIndex)
        .append("singleResource", singleResource)
        .append("resources", resources);
  }

}
