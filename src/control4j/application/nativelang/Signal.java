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
 *  Stands for a signal element.
 *
 */
public class Signal extends DescriptionBase implements IXmlHandler
{

  private String name;

  private int scope;

  private ArrayList<Property> properties = new ArrayList<Property>();

  /*
   *
   *    Value t-1 specification
   *
   */

  private boolean isValueT_1Specified = false;
  private boolean isValueT_1Valid = false;
  private String valueT_1;

  private ArrayList<Tag> tags = new ArrayList<Tag>();

  /**
   *  Creates an empty signal object.
   *
   *  @param name
   *             a name of the signal
   *
   *  @param scope
   */
  public Signal(String name, int scope)
  {
    set(name, scope);
  }

  protected void set(String name, int scope)
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
  Signal()
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
  { }

  @XmlStartElement(localName="property", parent="signal")
  private void startSignalProperty(Attributes attributes)
  {
    Property property = new Property();
    properties.add(property);
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

}
