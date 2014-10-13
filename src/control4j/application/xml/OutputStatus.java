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
import control4j.application.Output;
import control4j.application.Property;
import static control4j.tools.LogMessages.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *  Inside the output declaration status.
 */
class OutputStatus extends SaxStatusTemplate
{

  private ModuleStatus parentStatus;
  private Output output;

  OutputStatus(ModuleStatus parentStatus, Output output)
  {
    this.parentStatus = parentStatus;
    this.output = output;
  }

  /**
   *  The only element which may appear inside the output element
   *  is meta. If it appears something else, the SyntaxErrorException
   *  is thrown.
   */
  @Override
  public ISaxStatus startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
  {
    if (localName.equals("property") && uri.equals(Reader.namespace))
    {
      String key = attributes.getValue("key");
      if (key == null) throw new SyntaxErrorException(getMessage("appxmlh"));
      String value = attributes.getValue("value");
      if (value == null) throw new SyntaxErrorException(getMessage("appxmli"));
      output.putProperty(new Property(key, value));
      return new EndElementStatus(this);
    }
    else
    {
      throw new SyntaxErrorException(getMessage("appxmls"));
    }
  }  

  @Override
  public ISaxStatus endElement(String uri, String localName, String qName) 
  throws SAXException
  {
    if (localName.equals("output") && uri.equals(Reader.namespace))
    {
      return parentStatus;
    }
    else
    {
      throw new SyntaxErrorException(getMessage("appxmlu"));
    }
  }

}
