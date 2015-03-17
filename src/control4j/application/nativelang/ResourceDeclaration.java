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
import control4j.tools.ParseException;
import control4j.tools.XmlReader;
import control4j.tools.XmlStartElement;
import control4j.tools.XmlEndElement;

/**
 *
 *  Stands for a resource element.
 *
 */
public class ResourceDeclaration implements IXmlHandler
{

  private String name;
  private String className;
  private int scope;

  /** Property of the resource. */
  private ArrayList<Property> properties = new ArrayList<Property>();

  public ResourceDeclaration(String name, String className, int scope)
  {
  }

  /*
   *
   *    SAX parser methods
   *
   */

  private XmlReader reader;

  /**
   *  An empty constructor which may be used if the content
   *  of the signal will be loaded from XML document.
   */
  ResourceDeclaration()
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

  @XmlStartElement(localName="resource", parent="",
      namespace="http://control4j.lidinsky.cz/application")
  private void startResource(Attributes attributes)
  {
    this.name = Parser.parseToken(attributes.getValue("name"));
    if (name == null) {} // TODO

    this.className = Parser.parseToken(attributes.getValue("class"));
    if (className == null) {} // TODO

    try 
    { 
      this.scope = Parser.parseScope2(attributes.getValue("scope")); 
    }
    catch (ParseException e)
    {
      // TODO
    }
  }

  @XmlStartElement(localName="property", parent="resource") 
  private void startResourceProperty(Attributes attributes)
  {
    Property property = new Property();
    properties.add(property);
    reader.addHandler(property);
  }

  @XmlEndElement(localName="resource", parent="",
      namespace="http://control4j.lidinsky.cz/application")
  private void endResource()
  {
  }

}
