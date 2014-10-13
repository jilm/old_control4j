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
 *  It is thrown if some semantic error in config file
 *  is detected. For example, there is circular dependency
 *  between modules, outputs of two or more modules
 *  are conected into one signal or an input to some
 *  module is not produced by any output.
 */
public class SemanticErrorException extends RuntimeException
{
  public SemanticErrorException(String message)
  {
    super(message);
  }
}
