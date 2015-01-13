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

import java.util.List;
import java.util.ArrayList;
import control4j.tools.DeclarationReference;

/**
 *  Crate class for module declaration. Contains all the information needed
 *  to create a class which implements the functionality of the module
 *  and to connect it to other modules.
 */
public class ModuleDeclaration extends DeclarationBase
{

  /** 
   *  Name of the class which implements funcionality of that module.
   *  It may not contain empty string or null value. 
   */
  private String className;

  /** 
   *  Collection of the input declarations. This list is kept ordered
   *  in ascending order according to input index value. Inputs with 
   *  negative indexes are kept at the end of the list. These indexes 
   *  are reasigned by complete method. Index of the list is not identical 
   *  with the input index! The list doesn't contain null values.
   */
  private ArrayList<Input> inputs;

  /** 
   *  Collection of the output declarations. For more information
   *  take a look at the inputs property.
   */
  private ArrayList<Output> outputs;

  /** 
   *  Collection of the module settings. It may not contain two elements
   *  with the same key. 
   */
  private ConfigBuffer configuration;

  /**
   *  This flag indicates that the complete method was called. After
   *  that any changes of the content of this object will be banned.
   */
  private boolean completed = false;

  /**
   *  @param className
   *             name of the class that implements the functionality
   *             of the module. May not be null or empty string
   *
   *  @throws IllegalArgumentException
   *             if className is null or an empty string
   */
  public ModuleDeclaration(String className)
  {
    if (className == null || className.length() == 0)
      throw new IllegalArgumentException();
    else
      this.className = className;
    inputs = new ArrayList<Input>();
    outputs = new ArrayList<Output>();
    configuration = new ConfigBuffer();
  }

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
   *  Adds an input object into the internal buffer of inputs. It will 
   *  keep buffer ordered according to the input index. Inputs with 
   *  negative indexes will be placed at the end of the buffer.
   *
   *  @param input
   *             an input declaration object that shoud be added.
   *             This parameter may not contain null value.
   *
   *  @throws IllegalArgumentException
   *             if parameter intput is null value
   *
   *  @throws SyntaxErrorException
   *             if thre is another input with the same index
   *
   *  @see #completed
   *  @see #getInput
   *  @see #getInputsSize
   *  
   */
  public void addInput(Input input)
  {
    assert !completed : "Completed flag was already set";
    if (input == null)
      throw new IllegalArgumentException();
    if (input.getIndex() < 0)
      inputs.add(input);
    else
    {
      // find a place where the input should be placed to keep the list sorted
      int listIndex = findInputIndex(input.getIndex());
      // if it shoud be placed at the end of the list
      if (listIndex == inputs.size())
      {
        inputs.add(input);
        return;
      }
      // check if there is no input with the same index
      if (input.getIndex() == inputs.get(listIndex).getIndex())
      {
        throw new SyntaxErrorException();
      }
      // else insert new input
      inputs.add(listIndex, input);
    }
  }

  /**
   *  Adds an output object into the internal buffer of outputs. 
   *  For more information see the comment of the {@link #addInput} method.
   *
   *  @param output
   *             an output declaration object to add.
   *             This parameter may not contain null value.
   *
   *  @throws IllegalArgumentException
   *             if parameter output contains null value
   *
   *  @throws SyntaxErrorException
   *             if thre is another output with the same index
   *
   *  @see #completed
   *  @see #getOutput
   *  @see #getOutputsSize
   *  
   */
  public void addOutput(Output output)
  {
    assert !completed : "Completed flag was already set";
    if (output == null)
      throw new IllegalArgumentException();
    if (output.getIndex() < 0)
      outputs.add(output);
    else
    {
      // find a place where the output should be placed to keep the list sorted
      int listIndex = findOutputIndex(output.getIndex());
      // if it shoud be placed at the end of the list
      if (listIndex == outputs.size())
      {
        outputs.add(output);
        return;
      }
      // check if there is no output with the same index
      if (output.getIndex() == outputs.get(listIndex).getIndex())
      {
        throw new SyntaxErrorException();
      }
      // else insert new output
      outputs.add(listIndex, output);
    }
  }

  /**
   *  Returns object with configuration.
   *
   *  @return configuration of the object
   *
   *  @see #setConfigItem
   */
  public ConfigBuffer getConfiguration()
  {
    return configuration;
  }

  /**
   *  Sets the one configuration item.
   *
   *  @param property
   *             configuration to be set. May not contain null value
   *
   */
  public void setConfigItem(Property property)
  {
    configuration.put(property);
  }

  /**
   *  Returns an input with given index. It may return null if input with
   *  such index was not declared. This method may be called after the
   *  complete method was called. Otherwise the exception will be thrown.
   *
   *  @param index
   *             an index of desired input
   *
   *  @return input with given index or null
   *
   *  @throws IndexOutOfBoundsException
   *             if index is less than zero or greater than the highest
   *             index of all inputs
   *             
   *  @see #complete
   *  @see #addInput
   *  @see #getInputsSize
   *
   */
  public Input getInput(int index)
  {
    assert completed : "completed flag has not been already set!";
    if (index < 0 || index >= getInputsSize())
      throw new IndexOutOfBoundsException();
    int listIndex = findInputIndex(index);
    Input input = inputs.get(listIndex);
    if (input.getIndex() == index)
      return input;
    else
      return null;
  }

  /**
   *  Returns output with given index. For more info, see comment of
   *  {@link #getInput} method.
   *
   *  @param index
   *             an index of desired output
   *
   *  @return output with given index or null
   *
   *  @throws IndexOutOfBoundsException
   *             if index is less than zero or greater than the highest
   *             index of all outputs
   *             
   *  @see #complete
   *  @see #addOutput
   *  @see #getOutputsSize
   *
   **/
  public Output getOutput(int index)
  {
    assert completed : "completed flag has not been already set!";
    if (index < 0 || index >= getOutputsSize())
      throw new IndexOutOfBoundsException();
    int listIndex = findOutputIndex(index);
    Output output = outputs.get(listIndex);
    if (output.getIndex() == index)
      return output;
    else
      return null;
  }
  /**
   *  Returns number of inputs. It means it will find and input
   *  with the highest index and returns that index + 1. You can call
   *  this method after the complete method was called.
   *
   *  @return highest index of all the inputs plus one
   *
   */
  public int getInputsSize()
  {
    assert completed : "completed flag has not been already set!";
    if (inputs.size() > 0)
    {
      Input input = inputs.get(inputs.size()-1);
      return input.getIndex() + 1;
    }
    else
      return 0;
  }

  /**
   *  Returns the number of outputs. For more info see comment of
   *  {@link #getInputsSize}.
   *
   *  @return highest index of all the outputs plus one
   *
   */
  public int getOutputsSize()
  {
    assert completed : "completed flag has not been already set!";
    if (outputs.size() > 0)
    {
      Output output = outputs.get(outputs.size()-1);
      return output.getIndex() + 1;
    }
    else
      return 0;
  }

  /**
   *  Call after the object has all of the information and there will be no
   *  need to add another. This means to call set or add methods. This
   *  method rearange internal buffers and unlock get methods. Set and
   *  add methods will be locked. 
   *
   *  <p>This method assign a positive number to all of the inputs and
   *  outputs with negative indexes.
   *
   *  <p>If this method has already been called, it will do nothing.
   */
  public void complete()
  {
    if (!completed)
    {
      // comple inputs
      int index = 0;
      for (Input input : inputs)
      {
        if (input.getIndex() > 0)
	  index = input.getIndex() + 1;
        else
	  input.setIndex(index++);
      }
      // complete outputs
      index = 0;
      for (Output output : outputs)
      {
        if (output.getIndex() > 0)
	  index = output.getIndex() + 1;
        else
	  output.setIndex(index++);
      }
      completed = true;
    }
  }

  /**
   *  It takes an index of the input as an argument and returns an index
   *  into the inputs list where the input is. If there is no input with
   *  such an index, it returns an index where such input should be placed.
   *  It doesn't accept negative argument, but there could be inputs with
   *  negative indexes at the end of the list (before the completed was
   *  done). It uses binary search algorithm, so it expects that inputs
   *  list is sorted.
   *
   *  @param index
   *
   *  @return index to the inputs list
   */
  private int findInputIndex(int index)
  {
    int left = 0;
    int right = inputs.size();
    while (left < right)
    {
      int center = (left + right) / 2;
      int centerIndex = inputs.get(center).getIndex();
      if (centerIndex == index)
        return center;
      if (centerIndex > index || centerIndex < 0)
        right = center - 1;
      else
        left = center + 1;
    }
    return left;
  }

  /**
   *  For more info, see comment of findInputIndex.
   */
  private int findOutputIndex(int index)
  {
    int left = 0;
    int right = outputs.size();
    while (left < right)
    {
      int center = (left + right) / 2;
      int centerIndex = outputs.get(center).getIndex();
      if (centerIndex == index)
        return center;
      if (centerIndex > index || centerIndex < 0)
        right = center - 1;
      else
        left = center + 1;
    }
    return left;
  }

  @Override
  protected DeclarationReference getThisObjectIdentification()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("module (class name: ")
      .append(className)
      .append(')');
    return new DeclarationReference(sb.toString());
  }

}
