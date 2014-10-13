package control4j.tools;

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

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import static control4j.tools.Logger.*;

/**
 *
 *  Receives events from the SAX parser and transmits them
 *  to object which provide processing. This object must implement
 *  ISaxStatus interface. This mechanism allows realization of the
 *  state machin for xml document processing.
 *
 */
public class SaxHandler extends DefaultHandler
{

  /** Current status which receives events */
  private ISaxStatus status;

  /** Location inside the document */
  private Locator locator;

  /** 
   *  Declaration reference to the file from which the xml is loaded,
   *  may contain null.
   */
  private DeclarationReference fileReference;

  /**
   *  Initialize status.
   *
   *  @param firstStatus
   *             object which will receive the SAX events
   */
  public SaxHandler(ISaxStatus firstStatus)
  {
    status = firstStatus;
  }

  public void setDeclarationReference(DeclarationReference file)
  {
    fileReference = file;
  }

  /**
   *  Forward the event to the status object.
   */
  @Override
  public void startDocument()
  {
    status.setDocumentLocator(locator);
    status.setDeclarationReference(fileReference);
    status = status.startDocument();
  }

  /**
   *  Forward the event to the current status object.
   */
  @Override
  public void endDocument()
  {
    status.setDocumentLocator(locator);
    status.setDeclarationReference(fileReference);
    status = status.endDocument();
  }

  /**
   *  Forward the event to the current status object.
   */
  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes)
  throws SAXException
  {
    try
    {
      status.setDocumentLocator(locator);
      status.setDeclarationReference(fileReference);
      status = status.startElement(uri, localName, qName, attributes);
    }
    catch (SAXException e)
    {
      StringBuilder message = new StringBuilder();
      message.append(e.getClass().getName());
      message.append(':');
      message.append('\n');
      message.append(e.getMessage());
      message.append('\n');
      message.append("SAX event: start element, uri: ");
      message.append(uri);
      message.append(", local name: ");
      message.append(localName);
      message.append(", qualified name: ");
      message.append(qName);
      message.append('\n');
      message.append("Line number: ");
      message.append(locator.getLineNumber());
      message.append(", column number: ");
      message.append(locator.getColumnNumber());
      throw new SAXException(message.toString());
    }
  }

  /**
   *  Forward the event to the current status object.
   */
  @Override
  public void endElement(String uri, String localName, String qName)
  throws SAXException
  {
    try
    {
      status.setDocumentLocator(locator);
      status.setDeclarationReference(fileReference);
      status = status.endElement(uri, localName, qName);
    }
    catch (SAXException e)
    {
      StringBuilder message = new StringBuilder();
      message.append(e.getClass().getName());
      message.append(':');
      message.append('\n');
      message.append(e.getMessage());
      message.append('\n');
      message.append("SAX event: end element, uri: ");
      message.append(uri);
      message.append(", local name: ");
      message.append(localName);
      message.append(", qualified name: ");
      message.append(qName);
      message.append('\n');
      message.append("Line number: ");
      message.append(locator.getLineNumber());
      message.append(", column number: ");
      message.append(locator.getColumnNumber());
      throw new SAXException(message.toString());
    }
  }

  /**
   *  Forward the event to the current status object.
   */
  @Override
  public void setDocumentLocator(Locator locator)
  {
    this.locator = locator;
    status.setDocumentLocator(locator);
  }
  
  /**
   *  Forward the event to the current status object.
   */
  @Override
  public void characters(char[] ch, int start, int length) throws SAXException
  {
    status.setDocumentLocator(locator);
    status.setDeclarationReference(fileReference);
    status = status.characters(ch, start, length);
  }
  
  /**
   *  Throws the exception
   */
  @Override
  public void error(SAXParseException e) throws SAXException
  {
    finest("error event: SAXParseException catched: " + e.getMessage());
    throw e;
  }
  
  /**
   *  Throws the exception
   */
  @Override
  public void fatalError(SAXParseException e) throws SAXException
  {
    finest("fatal error event: " + e.getMessage());
    throw e;
  }
  
  /**
   *  Throws the exception
   */
  @Override
  public void warning(SAXParseException e) throws SAXException
  {
    finest("warning event: " + e.getMessage());
    throw e;
  }

}
