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
 *  Represents an input module, it is the module which takes input but
 *  doesn't provide any output for further processing. Typicaly it may
 *  be a module which writes values into the output hardware. Abstract
 *  class which must be extended by each input module.
 *
 *  <p>The {@link #put} method is dedicated for module functionality.
 *  Override this method and put the algorithm of the module there.
 */
public abstract class InputModule extends Module
{

  /**
   *  Method, that must implement module functionality. This method
   *  must be overwritten. It is regularily colled by the
   *  {@link ControlLoop} during the loop processing.
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
   *  Returns false.
   */
  @Override
  public final boolean isVariableOutputSupported() {
    return false;
  }
}
