package control4j.application.nativelang;

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

import java.util.ArrayList;
import org.xml.sax.Attributes;

import control4j.tools.IXmlHandler;
import control4j.tools.XmlReader;
import control4j.tools.XmlStartElement;
import control4j.tools.XmlEndElement;

/**
 *
 *  Represents a tag of the signal.
 *
 */
public class Tag extends DeclarationBase implements IXmlHandler
{

  private String name;

  private ArrayList<Property> properties = new ArrayList<Property>();

  /**
   *  Returns a string which contains fields of this object in
   *  the human readable form.
   */
  @Override
  public String toString()
  {
    return java.text.MessageFormat.format("Tag; name: {0}", name);
  }

  /*
   *
   *    Getters
   *
   */

  /**
   *  Returns the value of the property.
   */
  public String getName()
  {
    return name;
  }

  /*
   *
   *    SAX handler implementation.
   *
   */

  private XmlReader reader;

  /**
   *  An empty constructor for objects that will be loaded
   *  from a XML document.
   */
  Tag()
  {
  }

  public void startProcessing(XmlReader reader)
  {
    this.reader = reader;
  }

  public void endProcessing()
  {
    this.reader = null;
  }

  /**
   *  Initialize fields according to the elements attributes.
   */
  @XmlStartElement(localName="tag", parent="", 
      namespace="http://control4j.lidinsky.cz/application")
  private void startTag(Attributes attributes)
  {
    name = Parser.parseToken(attributes.getValue("name"));
    if (name == null) {} // TODO
  }

  /**
   *  Does nothing.
   */
  @XmlEndElement(localName="tag", parent="", 
      namespace="http://control4j.lidinsky.cz/application")
  private void endProperty()
  { }

  @XmlStartElement(localName="property", parent="tag")
  private void startTagProperty(Attributes attributes)
  {
    Property property = new Property();
    properties.add(property);
    reader.addHandler(property);
  }

}
