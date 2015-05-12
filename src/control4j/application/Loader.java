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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.NoSuchElementException;
import org.xml.sax.Attributes;

import control4j.tools.IXmlHandler;
import control4j.tools.XmlReader;
import control4j.tools.XmlStartElement;
import static control4j.tools.Logger.*;

/**
 *
 *  Loads and returns the whole application from a given source.
 *  The application may be composed of pars that are written in
 *  various languages. The loader uses appropriate loader for
 *  each part. The language is recongnized on the basis of a
 *  namespace. The map which assign loader to the namespace is
 *  read from a ...
 *
 */
public class Loader implements IXmlHandler
{

  private XmlReader reader;

  private ILoader loader;

  private LoaderFactory loaderFactory;

  private Application application = new Application();

  /**
   *  Reads a map: loader to namespace form a ...
   *
   *  @throws IOException
   *             if something went wrong with the configuration
   *             reading
   */
  public Loader() throws IOException
  {
    loaderFactory = LoaderFactory.getInstance();
  }

  /**
   *  Loads and returns an application from a given file.
   *
   *  @param file
   *             a file from which the application will be loaded
   *
   *  @throws IOException
   *             if something is wrong with the file
   */
  public Application load(File file)
  throws IOException
  {
    InputStream inputStream = new FileInputStream(file);
    return load(inputStream);
  }

  public Application load(InputStream inputStream)
  throws IOException
  {
    reader = new XmlReader();
    reader.addHandler(this);
    reader.load(inputStream);
    return application;
  }

  public static void main(String[] args) throws Exception
  {
    File file = new File(args[0]);
    Loader loader = new Loader();
    Application application = loader.load(file);
    System.out.println(application.toString());
  }

  /*
   *
   *  Implementation of IXmlHandler intarface
   *
   */

  /**
   *  Does nothing.
   */
  public void startProcessing(XmlReader reader)
  {
  }

  /**
   *  Does nothing.
   */
  public void endProcessing()
  {
  }

  public void missingHandler(XmlReader reader)
  {
    try
    {
      ILoader handler = LoaderFactory.getInstance().getHandler(reader);
      handler.setDestination(application);
      reader.addHandler(handler);
    }
    catch (NoSuchElementException e)
    {
      System.out.println("Didn't find appropriate handler");
      System.exit(1);
    }
    catch (Exception e)
    {
      catched(getClass().getName(), "startElement", e);
    }
  }

}
