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

import control4j.application.Scope;
import control4j.tools.IXmlHandler;
import control4j.tools.ParseException;
import control4j.tools.XmlReader;
import control4j.tools.XmlStartElement;
import control4j.tools.XmlEndElement;

/**
 *
 *  Resource definition.
 *
 */
public class ResourceDeclaration extends DescriptionBase
implements IXmlHandler, IAdapter
{

  /** Name of the java class that implements functionality of
      the resource */
  private String className;

  /** Identification of this resource definition to be referenced. */
  private String name;
  private int scope;

  /**
   *  Initialize fields of this object.
   */
  public ResourceDeclaration(
       String className, String name, int scope)
  {
    this.className = className;
    this.name = name;
    this.scope = scope;
  }

  /**
   *  Transfer all of the settings into the given object.
   */
  public void translate(
      control4j.application.Resource resource, Scope localScope)
  {
    super.translate(resource, localScope);
  }

  /*
   *
   *    Access Methods
   *
   */

  /**
   *  Returns the name of the java class that implements functionality
   *  of the resource.
   */
  public String getClassName()
  {
    return className;
  }

  public String getName()
  {
    return name;
  }

  public int getScope()
  {
    return scope;
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
  ResourceDeclaration(IAdapter adapter)
  {
    this.adapter = adapter;
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
    Property property = new Property(this);
    reader.addHandler(property);
  }

  @XmlEndElement(localName="resource", parent="",
      namespace="http://control4j.lidinsky.cz/application")
  private void endResource()
  {
    adapter.put(this);
  }

  @Override
  public void put(Property property)
  {
    addProperty(property);
  }

}
