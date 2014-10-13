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
 *  Provides unified interface for various application readers. Instance
 *  of the class that implements this interface should be able to load
 *  the application repeatedly. It means that it should not be necessary
 *  to create new instance of the reader before reading new file with
 *  the application.
 */
public interface IApplicationReader
{
  /**
   *  Loads an application from an input stream given as the argument.
   *  Loaded application is returned.
   *
   *  @param inputStream
   *             required application is read from this input stream
   *
   *  @param application
   *             the read application will be stored here
   *
   *  @param fileReference
   *             the reference to the file which will be loaded
   *
   *  @throws IOException 
   *             if something went wrong
   */
  void load(java.io.InputStream inputStream, 
            Application application,
	    control4j.tools.DeclarationReference fileReference) 
  throws java.io.IOException;
}
