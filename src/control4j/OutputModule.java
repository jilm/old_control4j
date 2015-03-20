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

import control4j.application.Output;

/**
 *  Represents a module which provides only output for another processing.
 */
public abstract class OutputModule extends Module
{

  /**
   *  Method that provides output of the module for the further prcessing.
   *  This is the only method that must be overwritten by the module
   *  developer. This method will be called repeatedly by the execute,
   *  method during the processing phase.
   *
   *  @param output
   *             an array which serves for output transfer.
   *
   *  @param outputLength
   *             how may elements may be used for this method
   *
   *  @throws RuntimeModuleException
   *             if something went wrong and the whole control loop
   *             should not be finished. But sometimes it is sufficient
   *             just return invalid signals.
   *
   */
  protected abstract void get(Signal[] output, int outputLength)
      throws RuntimeModuleException;

  /**
   *  This method is used for custom output configuration. This method
   *  does nothing. If you (the module developer) want to provide
   *  custom configuration of particular output, just override this
   *  method.
   *
   *  <p>This method is called only once during the phase of application
   *  building by the method for each output.
   *
   *  <p>If parameters contain configuration that is not supported by
   *  this module or output, throws SyntaxErrorException.
   *
   *  @param index
   *             index of module output
   *
   *  @param configuration
   *             contains custom configuration for an output with given
   *             index. May contain null value if there is no custom
   *             configuration for the output.
   */
  public void setOutputConfiguration(int index, IConfigBuffer configuration)
  { }

  @Override
  public void dump(java.io.PrintWriter writer)
  {
    super.dump(writer);
  }

}
