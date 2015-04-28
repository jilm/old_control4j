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

import cz.lidinsky.tools.ToStringBuilder;

/**
 *
 *  Stands for a signal element.
 *
 */
public class Signal extends DescriptionBase implements IXmlHandler, IAdapter
{

  private String name;

  protected IAdapter adapter;

  public String getName()
  {
    return name;
  }

  private int scope;

  public int getScope()
  {
    return scope;
  }

  /**
   *
   */
  public void translate(
      control4j.application.Signal destination, Scope localScope)
  {
    // translate configuration
    super.translate(destination, localScope);

    // translate value for the time t-1
    if (isValueT_1Specified)
    {
      if (isValueT_1Valid)
        destination.setValueT_1(valueT_1);
      else
        destination.setValueT_1Invalid();
    }

    // translate tag objects
    if (tags != null)
      for (Tag tag : tags)
      {
        control4j.application.Tag destTag = new control4j.application.Tag();
        tag.translate(destTag, localScope);
        destination.putTag(tag.getName(), destTag);
      }

  }

  /*
   *
   *    Value for time t-1 specification
   *
   */

  private boolean isValueT_1Specified = false;
  private boolean isValueT_1Valid = false;
  private String valueT_1;

  /*
   *
   *     Tags
   *
   */

  private ArrayList<Tag> tags = new ArrayList<Tag>();

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
  Signal(IAdapter adapter)
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

  @XmlStartElement(localName="signal", parent="", 
      namespace="http://control4j.lidinsky.cz/application")
  private void startSignal(Attributes attributes)
  {
    name = Parser.parseToken(attributes.getValue("name"));
    if (name == null) {} // TODO

    try
    {
      scope = Parser.parseScope2(attributes.getValue("scope"));
    }
    catch (ParseException e)
    {
      // TODO
    }
  }

  @XmlEndElement(localName="signal", parent="",
      namespace="http://control4j.lidinsky.cz/application")
  private void endSignal()
  {
    adapter.put(this);
  }

  @XmlStartElement(localName="property", parent="signal")
  private void startSignalProperty(Attributes attributes)
  {
    Property property = new Property(this);
    reader.addHandler(property);
  }

  @XmlStartElement(localName="value-t-1", parent="signal")
  private void startSignalValue(Attributes attributes)
  {
    if (isValueT_1Specified) {} // TODO
  }

  @XmlEndElement(localName="value-t-1", parent="signal")
  private void endSignalValue()
  { }

  @XmlStartElement(localName="invalid", parent="value-t-1")
  private void startValueInvalid(Attributes attributes)
  {
    isValueT_1Specified = true;
    isValueT_1Valid = false;
    valueT_1 = null;
  }

  @XmlEndElement(localName="invalid", parent="value-t-1")
  private void endValueInvalid()
  { }

  @XmlStartElement(localName="signal", parent="value-t-1")
  private void startValueSignal(Attributes attributes)
  {
    isValueT_1Specified = true;
    isValueT_1Valid = true;
    valueT_1 = Parser.parseToken(attributes.getValue("value"));
    if (valueT_1 == null) {} // TODO
  }

  @XmlEndElement(localName="signal", parent="value-t-1")
  private void endValueSignal()
  { }

  @XmlStartElement(localName="tag", parent="signal")
  private void startSignalTag(Attributes attributes)
  {
    Tag tag = new Tag();
    tags.add(tag);
    reader.addHandler(tag);
  }

  @Override
  public void toString(ToStringBuilder builder)
  {
    super.toString(builder);
    builder.append("name", name)
        .append("scope", scope);
  }

}
