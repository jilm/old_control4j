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

import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 */
public class SaxStatusTemplate implements ISaxStatus
{

  /** 
   *  Current location inside the document. Locator is available
   *  before the firs event occurs.
   */
  protected Locator locator;

  /**
   *  Declaration reference to the file from which the xml is loaded.
   *  May contain null value. The value is available before the first
   *  event occurs.
   */
  protected DeclarationReference fileReference;

  public ISaxStatus startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
  {
    throw new SAXException("start element is not expected here");
  }
  
  public ISaxStatus endElement(String uri, String localName, String qName) throws SAXException
  {
    throw new SAXException("end element is not expected here");
  }
  
  public ISaxStatus startDocument()
  {
    return this;
  }
  
  public ISaxStatus endDocument()
  {
    return null;
  }
  
  public ISaxStatus characters(char[] ch, int start, int length) throws SAXException
  {
    return this;
    //for (char c : ch)
    //  if (!Character.isWhitespace(c))
    //    throw new SAXException("Text is not expected here");
    //return this;
  }

  public void setDocumentLocator(Locator locator)
  {
    this.locator = locator;
  }

  public void setDeclarationReference(DeclarationReference file)
  {
    fileReference = file;
  }

}

