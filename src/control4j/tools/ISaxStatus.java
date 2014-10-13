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
public interface ISaxStatus
{

  ISaxStatus startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException;
  
  ISaxStatus endElement(String uri, String localName, String qName) throws SAXException;
  
  ISaxStatus startDocument();
  
  ISaxStatus endDocument();
  
  ISaxStatus characters(char[] ch, int start, int length) throws SAXException;

  void setDocumentLocator(Locator locator);

  void setDeclarationReference(DeclarationReference reference);
}

