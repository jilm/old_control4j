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
 *  Is thrown by module during the initialization process if
 *  the module doesn't provide sufficient number of outputs,
 *  or if it doesn't except so many inputs.
 */
public class UnsupportedNumberOfIOException extends RuntimeException
{
  public UnsupportedNumberOfIOException()
  {
    super();
  }

  public UnsupportedNumberOfIOException(String message)
  {
    super(message);
  }

  public UnsupportedNumberOfIOException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public UnsupportedNumberOfIOException(Throwable cause)
  {
    super(cause);
  }
}
