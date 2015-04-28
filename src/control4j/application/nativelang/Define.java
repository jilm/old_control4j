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

import cz.lidinsky.tools.ToStringBuilder;

/**
 *
 *  Represents define element
 *
 */
public class Define extends DeclarationBase implements IXmlHandler
{

  private String name;

  private String value;

  private int scope;

  /*
   *
   *   Access Methods 
   *
   */

  public String getName()
  {
    return name;
  }

  public int getScope()
  {
    return scope;
  }

  public String getValue()
  {
    return value;
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
  Define(IAdapter adapter)
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
  @XmlStartElement(localName="define", parent="", 
      namespace="http://control4j.lidinsky.cz/application")
  private void startDefine(Attributes attributes)
  {
    name = Parser.parseToken(attributes.getValue("name"));
    if (name == null) {} // TODO

    value = attributes.getValue("value");
    if (value == null) {} // TODO

    try
    {
      scope = Parser.parseScope2(attributes.getValue("scope"));
    }
    catch (ParseException e)
    {
      // TODO
    }
  }

  /**
   */
  @XmlEndElement(localName="define", parent="", 
      namespace="http://control4j.lidinsky.cz/application")
  private void endDefine()
  {
    adapter.put(this);
  }

  @Override
  public void toString(ToStringBuilder builder)
  {
    super.toString(builder);
    builder.append("name", name)
        .append("value", value)
        .append("scope", scope);
  }

}
