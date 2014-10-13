package control4j.application;

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
 *  Class that creates and returns an appropriate application reader which
 *  can read a required file with the application. This class is singleton,
 *  to get the instance call getInstance method.
 */
public class ReaderFactory
{
  /** The only instance of this class or null */
  private static ReaderFactory instance;

  /**
   *  Returns instance of this class.
   *
   *  @return instance of this class
   */
  public static ReaderFactory getInstance()
  {
    if (instance == null) instance = new ReaderFactory();
    return instance;
  }

  /**
   *  This class is singleton.
   */
  private ReaderFactory()
  {
  }

  /**
   *  Returns the appropriate application reader. The returned class
   *  instance depends entirely on the parameter type. If the type
   *  is unknown it throws exception.
   *
   *  @param type
   *             identifies format of the file with application which
   *             shoud be read by the returned reader
   *
   *  @return the reader class instance
   *
   *  @throws IllegalArgumentException
   *             if the type is unknown or unsupported
   */
  public IApplicationReader getReader(String type)
  {
    if (type.equals("text"))
      return null;
    if (type.equals("xml"))
      return new control4j.application.xml.Reader();
    if (type.equals("ld"))
      return null;
    if (type.equals("gui"))
      return new control4j.application.gui.Reader();
    throw new IllegalArgumentException("Unsupported application type");
  }
}
