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

import org.xml.sax.Attributes;

import control4j.tools.IXmlHandler;
import control4j.tools.ParseException;
import control4j.tools.XmlReader;
import control4j.tools.XmlStartElement;
import control4j.tools.XmlEndElement;

/**
 *
 *  Represents a property of some higher level object.
 *  This object has two variants.
 *  <ol>
 *    <li>Property which directly contains a value.
 *    <li>Property which refers to some define object.
 *  </ol>
 *
 */
public class Property extends DeclarationBase implements IXmlHandler
{

  private String key;

  /** Value of the property. */
  private String value;

  private String href;

  private int scope;

  private boolean isReference;

  /**
   *  This constructor is dedicated to the property which directly
   *  contains a value.
   */
  public Property(String key, String value)
  {
    this.key = key;
    this.value = value;
    this.isReference = false;
  }

  /**
   *  This constructor is used for the property which refers
   *  to some define object.
   */
  public Property(String key, String href, int scope)
  {
    this.key = key;
    this.href = href;
    this.scope = scope;
    this.isReference = true;
  }

  /**
   *  Returns a string which contains fields of this object in
   *  the human readable form.
   */
  @Override
  public String toString()
  {
    if (isReference)
      return java.text.MessageFormat.format(
          "Property; key: {0}, href: {1}, scope: {2}",
          key, href, Parser.formatScope(scope));
    else
      return java.text.MessageFormat.format(
          "Property; key: {0}, value: {1}", key, value);
  }

  /*
   *
   *    Access Methods
   *
   */

  public String getKey()
  {
    return key;
  }

  /**
   *  Returns the value of the property.
   */
  public String getValue()
  {
    return value;
  }

  public String getHref()
  {
    return href;
  }

  public int getScope()
  {
    return scope;
  }

  public boolean isReference()
  {
    return isReference;
  }

  /*
   *
   *    SAX handler implementation.
   *
   */

  private XmlReader reader;

  protected IAdapter adapter;

  /**
   *  An empty constructor for objects that will be loaded
   *  from a XML document.
   */
  Property(IAdapter adapter)
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

  /**
   *  Initialize fields according to the elements attributes.
   */
  @XmlStartElement(localName="property", parent="", 
      namespace="http://control4j.lidinsky.cz/application")
  private void startProperty(Attributes attributes)
  {
    String key = Parser.parseToken(attributes.getValue("key"));
    String value = attributes.getValue("value");
    String href = Parser.parseToken(attributes.getValue("href"));
    String strScope = attributes.getValue("scope");
    
    if (key == null) {} // TODO
    else
      this.key = key;

    if (value != null && href == null)
    {
      isReference = false;
      this.value = value;
    }
    else if (value == null && href != null)
    {
      isReference = true;
      this.href = href;
      try
      {
        this.scope = Parser.parseScope3(strScope);
      }
      catch (ParseException e)
      {
        // TODO
      }
    }
    else if (value == null && href == null)
    {
      // TODO
    }
    else
    {
      // TODO
    }
  }

  /**
   *  Elements inside the property element are not allowed.
   */
  @XmlStartElement(localName="*", parent="property")
  private void startElement(Attributes attributes)
  {
    // TODO
  }

  /**
   *  Does nothing.
   */
  @XmlEndElement(localName="property", parent="", 
      namespace="http://control4j.lidinsky.cz/application")
  private void endProperty()
  {
    adapter.put(this);
  }

}
