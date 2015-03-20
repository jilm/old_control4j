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
 *  Represents a resource element inside the module.
 *  This object has two variants.
 *  <ol>
 *    <li>Resource fully described inside the module.
 *    <li>Resource which refers to some resource definition.
 *  </ol>
 *
 */
public class Resource extends Configurable implements IXmlHandler
{

  private String key;

  private String className;

  private String href;

  private int scope;

  private boolean isReference;

  /**
   *  Returns a string which contains fields of this object in
   *  the human readable form.
   */
  @Override
  public String toString()
  {
    if (isReference)
      return java.text.MessageFormat.format(
	  "Resource; key: {0}, href: {1}, scope: {2}",
	  key, href, Parser.formatScope(scope));
    else
      return java.text.MessageFormat.format(
	  "Resource; key: {0}, class: {1}", key, className);
  }

  /*
   *
   *    Getters
   *
   */

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
  Resource()
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
  @XmlStartElement(localName="resource", parent="", 
      namespace="http://control4j.lidinsky.cz/application")
  private void startProperty(Attributes attributes)
  {
    key = Parser.parseToken(attributes.getValue("key"));
    if (key == null) {} // TODO

    href = Parser.parseToken(attributes.getValue("href"));
    className = Parser.parseToken(attributes.getValue("class"));

    try
    {
      scope = Parser.parseScope3(attributes.getValue("scope"));
    }
    catch (ParseException e)
    {
      // TODO
    }

    if (className != null && href == null)
    {
      isReference = false;
    }
    else if (className == null && href != null)
    {
      isReference = true;
    }
    else if (className == null && href == null)
    {
      // TODO
    }
    else
    {
      // TODO
    }
  }


  /**
   *  Does nothing.
   */
  @XmlEndElement(localName="resource", parent="", 
      namespace="http://control4j.lidinsky.cz/application")
  private void endResource()
  { }

  @XmlStartElement(localName="property", parent="resource")
  private void startResourceProperty(Attributes attributes)
  {
    if (isReference) {} // TODO
    else
    {
      Property property = new Property();
      addProperty(property);
      reader.addHandler(property);
    }
  }

}
