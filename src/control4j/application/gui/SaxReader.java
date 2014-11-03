package control4j.application.gui;

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

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *  @deprecated see {@link control4j.tools.SaxReader} instead
 */
@Deprecated
public class SaxReader extends DefaultHandler
{
  private LinkedList<String> elementStack = new LinkedList<String>();

  public SaxReader()
  {
  }

  public void load(InputStream inputStream) throws IOException
  {
    try
    {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(true);
    SAXParser parser = factory.newSAXParser();
    parser.parse(inputStream, this);
    }
    catch (SAXException e)
    {
    }
    catch (ParserConfigurationException e)
    {
    }
  }

  /**
   *
   */
  @Override
  public void startElement(String uri, 
                           String localName, 
			   String qName, 
			   Attributes attributes) throws SAXException
  {
    // find a method that implemnts the work
    Method[] methods = this.getClass().getDeclaredMethods();
    for (Method method : methods)
    {
      XmlStartElement annotation = method.getAnnotation(XmlStartElement.class);
      if (annotation != null)
        if (annotation.localName().equals(localName) 
	    && annotation.parent().equals(elementStack.getLast()))
	{
	  try
	  {
	    method.invoke(this, attributes);
	  } 
	  catch (Exception e) 
	  {
	    System.out.println(e.getMessage());
	  }
	}
    }
    elementStack.add(localName);
  }

  @Override
  public void endElement(String uri, String localName, String qName)
  {
    String lastElement = elementStack.removeLast();
    // find a method that implemnts the work
    Method[] methods = this.getClass().getDeclaredMethods();
    for (Method method : methods)
    {
      XmlEndElement annotation = method.getAnnotation(XmlEndElement.class);
      if (annotation != null)
        if (annotation.localName().equals(lastElement) 
	    && annotation.parent().equals(elementStack.getLast()))
	{
	  try
	  {
	    method.invoke(this);
	  } 
	  catch (Exception e) 
	  {
	    System.out.println(e.getMessage());
	  }
	}
    }
  }

  @Override
  public void startDocument()
  {
    elementStack.clear();
    elementStack.add("");
  }

  @Override
  public void endDocument()
  {
  }

}
