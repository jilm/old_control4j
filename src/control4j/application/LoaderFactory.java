package control4j.application;

/*
 *  Copyright 2015 Jiri Lidinsky
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

import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 *
 *  Returns appropriate loader based on a namespace.
 *
 */
public class LoaderFactory
{

  protected final String mapFilename  
      = "control4j/application/namespaceloadermap.conf";

  protected HashMap<String, String> map = new HashMap<String, String>();

  private static LoaderFactory instance;

  private LoaderFactory()
  {
  }

  public static LoaderFactory getInstance() throws IOException
  {
    if (instance == null)
    {
      instance = new LoaderFactory();
      instance.loadMap();
    }
    return instance;
  }

  /**
   *  Reads the map namespace loader from a configuration file.
   */
  private void loadMap() throws IOException
  {
    // open the resource file
    InputStream stream
        = getClass().getClassLoader().getResourceAsStream(mapFilename);
    java.io.Reader reader = new java.io.InputStreamReader(stream);
    java.io.BufferedReader lineReader = new java.io.BufferedReader(reader);
    try
    {
      // load content
      String line = lineReader.readLine();
      while (line != null)
      {
	// split the line
        String[] tokens = line.split(" ");
	map.put(tokens[0], tokens[1]);
        // get next line
	line = lineReader.readLine();
      }
    }
    finally
    {
      lineReader.close();
    }
  }

  public String getLoaderClassName(String namespace) 
  throws NoSuchElementException
  {
    String className = map.get(namespace);
    if (className == null) throw new NoSuchElementException();
    return className;
  }

  public ILoader getLoader(String namespace)
  throws Exception
  {
      String className = getLoaderClassName(namespace);
      Class loaderClass = Class.forName(className);
      Object loaderInstance = loaderClass.newInstance();
      return (ILoader)loaderInstance;
  }

  public static void main(String[] args) throws Exception
  {
    LoaderFactory factory = new LoaderFactory();
    System.out.println(factory.map);
  }

}
