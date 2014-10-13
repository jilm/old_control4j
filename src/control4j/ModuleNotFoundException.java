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

import control4j.tools.LogMessages;

/**
 *  This exception is thrown when you specify some
 *  module, in the config file, which implementation
 *  doesn't exist or was not found.
 */
public class ModuleNotFoundException extends Exception
{
  /**
   *  @param className class name of the module
   */
  public ModuleNotFoundException(String className)
  {
    super(LogMessages.getMessage("modulenotfound", className));
  }

}
