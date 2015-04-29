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

import static control4j.tools.Logger.warning;
import static org.apache.commons.lang3.Validate.notNull;

import control4j.application.ILoader;
import control4j.tools.IXmlHandler;
import control4j.tools.XmlReader;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 *
 *  Returns appropriate handler
 *
 */
public class LoaderFactory
{

  protected ArrayList<Class<ILoader>> handlers
      = new ArrayList<Class<ILoader>>();

  protected static final String mapFilename
      = "control4j/application/xmlhandlers.conf";

  private static LoaderFactory instance;

  private LoaderFactory() { }

  public static LoaderFactory getInstance()
  {
    if (instance == null)
    {
      instance = new LoaderFactory();
      instance.loadMap();
    }
    return instance;
  }

  public void add(Class<? extends ILoader> handler)
  {
    notNull(handler);
    if (!handlers.contains(handler))
      handlers.add((Class<ILoader>)handler);
  }

  public void remove(Class handler)
  {
    notNull(handler);
    handlers.remove(handler);
  }

  public Class<ILoader> get(XmlReader reader)
  {
    for (Class<ILoader> handler : handlers)
    {
      try
      {
        reader.findHandlerMethod(handler);
	return handler;
      }
      catch (NoSuchElementException e) {} // just doesn't support this XML
    }
    throw new NoSuchElementException();
  }

  public ILoader getHandler(XmlReader reader)
  {
    while (true)
    {
      Class<ILoader> handlerClass = get(reader);
      try
      {
        return handlerClass.newInstance();
      }
      catch (InstantiationException e)
      {
        warning(java.text.MessageFormat.format(
	    "Failed to make instance of XML handler {0}. "
	    + "The InstantiationException was catched:\n{1}",
	    handlerClass.getName(), e.getMessage()));
        remove(handlerClass);
      }
      catch (IllegalAccessException e)
      {
        warning(java.text.MessageFormat.format(
	    "Failed to make instance of XML handler {0}. "
	    + "Becouse of Illegal Access:\n{1}",
	    handlerClass.getName(), e.getMessage()));
        remove(handlerClass);
      }
    }
  }

  /**
   *  Reads the map namespace loader from a configuration file.
   */
  private void loadMap()
  {
    // open the resource file
    InputStream stream
        = getClass().getClassLoader().getResourceAsStream(mapFilename);
    if (stream == null)
    {
      warning(java.text.MessageFormat.format(
	  "Cannot find a file with XML handlers: {0}!", mapFilename));
      return;
    }
    java.io.Reader reader = new java.io.InputStreamReader(stream);
    java.io.BufferedReader lineReader = new java.io.BufferedReader(reader);
    try
    {
      // load content
      String line = lineReader.readLine();
      while (line != null)
      {
	line = line.trim();
	try
	{
	  if (line.length() > 0)
	  {
	    Class handler = Class.forName(line);
	    add((Class<ILoader>)handler);
	  }
	}
        catch (ClassNotFoundException e)
        {
	  warning(java.text.MessageFormat.format(
	      "A handler class {0} was not found!", line));
        }
        catch (ClassCastException e)
        {
	  warning(java.text.MessageFormat.format(
	      "A class {0} is not an XML handler!", line));
        }
	finally
	{
          // get next line
	  line = lineReader.readLine();
	}
      }
    }
    catch (IOException e)
    {
      warning(java.text.MessageFormat.format(
	  "An IO exception was catched while reading the file with XML"
	  + " handlers:\n{0}", e.getMessage()));
    }
    finally
    {
      control4j.tools.Tools.close(lineReader);
    }
  }

  public static void main(String[] args) throws Exception
  {
    LoaderFactory factory = getInstance();
    System.out.println(factory.handlers);
  }

}
