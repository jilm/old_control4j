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

import control4j.tools.SaxStatusTemplate;
import control4j.tools.ISaxStatus;
import static control4j.tools.LogMessages.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *  End element is expected.
 */
class EndElementStatus extends SaxStatusTemplate
{

  private ISaxStatus parentStatus;

  EndElementStatus(ISaxStatus parentStatus)
  {
    this.parentStatus = parentStatus;
  }

  @Override
  public ISaxStatus startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
  {
    throw new SyntaxErrorException(getMessage("appxmlv"));
  }

  @Override
  public ISaxStatus endElement(String uri, String localName, String qName) 
  throws SAXException
  {
    return parentStatus;
  }

}
