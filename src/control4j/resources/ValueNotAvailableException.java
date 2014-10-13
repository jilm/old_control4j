package control4j.resources;

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
 *  This exception indicates that requested value is not available
 *  at that moment. The reason may be hardware failure, lost
 *  communication, ...
 *  It is used by classes that comunicate with measuring hardware.
 */
public class ValueNotAvailableException extends Exception
{
  public ValueNotAvailableException()
  {
    super();
  }

  public ValueNotAvailableException(String message)
  {
    super(message);
  }

  public ValueNotAvailableException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public ValueNotAvailableException(Throwable cause)
  {
    super(cause);
  }
}
