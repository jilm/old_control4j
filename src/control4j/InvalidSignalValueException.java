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
 *  Exception is thrown in case when method that is supposed to return
 *  value is called on invalid Signal object.
 *
 *  @see Signal
 */
public class InvalidSignalValueException extends RuntimeException
{
  public InvalidSignalValueException()
  {
    super();
  }

  public InvalidSignalValueException(String message)
  {
    super(message);
  }

  public InvalidSignalValueException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public InvalidSignalValueException(Throwable cause)
  {
    super(cause);
  }
}
