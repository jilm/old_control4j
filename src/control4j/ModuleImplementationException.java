package control4j;

/*
 *  Copyright 2013 Jiri Lidinsky
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

/**
 *  Is thrown in situation whre you use a module that doesn't
 *  satisfies all of the condition to the module. It means,
 *  it doesn't implement module interface, or it implements
 *  process interface together with input or output interfaces.
 *  Or it doesn't implement a constructor with Map parameter.
 */
public class ModuleImplementationException extends RuntimeException
{
  /**
   *  @param message that tells which implementation condition
   *                 is not satisfied.
   *  @param moduleName name of the module.
   */
  public ModuleImplementationException(String message, String moduleName)
  {
    super("Module: " + moduleName + " is not properly implemented. " + message);
  }

  public ModuleImplementationException(String message)
  {
    super(message);
  }
}
