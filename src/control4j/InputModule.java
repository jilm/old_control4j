package control4j;

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

import java.util.NoSuchElementException;

import control4j.application.Input;

/**
 *
 *  Represents an input module, it is the module which takes input but 
 *  doesn't provide any output for further processing. Typicaly it may 
 *  be a module which writes values into the output hardware. Abstract 
 *  class which must be extended by each input module.
 *
 */
public abstract class InputModule extends Module
{

  /**
   *  Method, that must implement module functionality. This method
   *  must be overwritten.
   *
   *  @param input
   *             input signal values. Inputs that were not assigned
   *             contains null value. This array may be lager than
   *             the inputLength.
   *
   *  @param inputLength
   *             how many elements in the input array contains
   *             an input for this function. Don't use elements
   *             behind.
   */
  protected abstract void put(Signal[] input, int inputLength);

  /**
   *  This method may be used to get configuration of the input
   *  if neccessary. This method is called during the instantiation
   *  phase of the application loading. It is called for each 
   *  assigned input. Method does nothing, override it method, 
   *  to get the input configuration.
   *
   *  @param index
   *             input index of the input
   *
   *  @param configuration
   *             configuration of the input with given index
   */
  public void setInputConfiguration(int index, IConfigBuffer configuration)
  { }

  @Override
  public void dump(java.io.PrintWriter writer)
  {
    super.dump(writer);
  }

}
