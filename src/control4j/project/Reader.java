package control4j.project;

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

import java.io.InputStream;
import java.io.IOException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import control4j.tools.ISaxStatus;
import control4j.tools.SaxHandler;
import control4j.tools.SaxStatusTemplate;
import control4j.application.Property;

/**
 *  Reads project file in xml format.
 */
public class Reader
{

  private Project project;

  /**
   *  Reads the given project file in the xml format.
   */
  public Project load(InputStream inputStream) throws IOException
  {
    try
    {
      project = new Project();
      SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
      parser.parse(inputStream, new SaxHandler(new DocumentStatus()));
      return project;
    }
    catch (SAXException e)
    {
      throw new IOException(e.getMessage());
    }
    catch (ParserConfigurationException e)
    {
      throw new UnsupportedOperationException("SAX parser could not be created", e);
    }
  }

  /**
   *  Expects root element.
   */
  private class DocumentStatus extends SaxStatusTemplate
  {
    @Override
    public ISaxStatus startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
      if (qName.equals("c4j-project"))
        return new RootElementStatus();
      else
        throw new SAXException("c4j-project element expected");
    }
  }

  /**
   *  Expects property or files elements
   */
  private class RootElementStatus extends SaxStatusTemplate
  {

    @Override
    public ISaxStatus startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
      if (qName.equals("property"))
      {
        String key = attributes.getValue("key");
	String value = attributes.getValue("value");
	Property property = new Property(key, value);
	project.putProperty(property);
        return new EndElementStatus(this);
      }
      else if (qName.equals("files"))
      {
        return new FilesStatus();
      }
      else
      {
        throw new SAXException("Files or property element expected here");
      }
    }
    
  }

  /**
   *  Inside the files element, waits for file elements or end of files
   *  element
   */
  private class FilesStatus extends SaxStatusTemplate
  {
    @Override
    public ISaxStatus startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
      if (qName.equals("file"))
      {
        String filename = attributes.getValue("filename");
	String type = attributes.getValue("type");
	ApplicationFilename app = new ApplicationFilename(filename, type);
	project.addFilename(app);
	return new EndElementStatus(this);
      }
      else
      {
        throw new SAXException("file element expected here");
      }
    }
    
    @Override
    public ISaxStatus endElement(String uri, String localName, String qName)
    throws SAXException
    {
      if (qName.equals("files"))
      {
        return new EndDocumentStatus();
      }
      else
      {
        throw new SAXException("files end element expected here");
      }
    }
  }

  /**
   *   Status is just waiting for the end element.
   */
  private class EndElementStatus extends SaxStatusTemplate
  {
    private ISaxStatus parent;

    /**
     *  @param parent
     *             status which shoud be returned when the end element is got
     */
    EndElementStatus(ISaxStatus parent)
    {
      this.parent = parent;
    }

    /**
     *  @return the parent object
     */
    @Override
    public ISaxStatus endElement(String uri, String localName, String qName)
    throws SAXException
    {
      return parent;
    }
  }

  /**
   *  It just waits for root end element
   */
  private class EndDocumentStatus extends SaxStatusTemplate
  {
    @Override
    public ISaxStatus endElement(String uri, String localName, String qName)
    throws SAXException
    {
      if (qName.equals("c4j-project"))
      {
        return this;
      }
      else
      {
        throw new SAXException("c4j-project end element expected here");
      }
    }
  }

}

