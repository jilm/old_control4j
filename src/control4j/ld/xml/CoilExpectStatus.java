package control4j.ld.xml;

/*
 *  Copyright 2013 Jiri Lidinsky
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
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import control4j.ld.Coil;

/**
 *  
 */
public class CoilExpectStatus extends SaxStatusTemplate
{

  RungStatus parent;

  /**
   *  @param parent
   */
  CoilExpectStatus(RungStatus parent)
  {
    this.parent = parent;
  }

  @Override
  public ISaxStatus startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
  {
    if (qName.equals("coil"))
    {
      String name = attributes.getValue("name");
      if (name == null) throw new SAXException("A coil without name");
      String type = attributes.getValue("type");
      Coil child = new Coil(type, name);
      parent.rung.addCoil(child);
      ISaxStatus nextStatus = new CoilStatus(parent);
      return nextStatus;
    }
    else
    {
      throw new SAXException("Contact or parallel element expected");
    }
  }

}
