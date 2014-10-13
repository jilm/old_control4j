package control4j.application.xml;

/*
 *  Copyright 2013, 2014 Jiri Lidinsky
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

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import control4j.application.Application;
import control4j.tools.SaxHandler;
import control4j.tools.DeclarationReference;

/**
 *  Loads a control4j application in the xml format.
 */
public class Reader implements control4j.application.IApplicationReader
{

  public static final String namespace = "http://application.control4j.cz";

  public static void main(String[] args) throws Exception
  {
    String filename = args[0];
    DeclarationReference fileReference = new DeclarationReference();
    fileReference.setFile(filename);
    InputStream is = new FileInputStream(filename);
    Application app = new Application();
    Reader reader = new Reader();
    reader.load(is, app, fileReference);
    is.close();
  }

  /**
   *  Loads a control4j application in xml format from given input
   *  stream, and stores it into the given application object.
   *
   *  @param inputStream
   *             an stream from which the xml form of application
   *             is loaded.
   *  
   *  @param application
   *             an object whre the loaded application will be stored
   *
   *  @param fileReference
   *             a reference to the file from which the application
   *             is loaded. May be null.
   *
   *  @throws IOException
   *             if something goes wrong with the input stream
   *             or if the input stream is not in the proper xml form
   */
  public void load(InputStream inputStream, 
                   Application application, 
		   DeclarationReference fileReference)
  throws IOException
  {
    try
    {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(true);
      SAXParser parser = factory.newSAXParser();
      SaxHandler handler = new SaxHandler(new DocumentStatus(application));
      handler.setDeclarationReference(fileReference);
      parser.parse(inputStream, handler);
    }
    catch (ParserConfigurationException e)
    {
      throw new RuntimeException("Configuration of the SAX parser is not valid", e);
    }
    catch (SAXException e)
    {
      throw new IOException(e.getMessage());
    }
  }
}
