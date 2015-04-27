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
 *  Represents an input element
 *
 */
public class Input extends Configurable implements IXmlHandler, IAdapter
{

  private String index;

  public String getIndex()
  {
    return index;
  }

  private String href;

  public String getHref()
  {
    return href;
  }

  private int scope;

  public int getScope()
  {
    return scope;
  }

  /**
   *  Returns a string which contains fields of this object in
   *  the human readable form.
   */
  @Override
  public String toString()
  {
    return java.text.MessageFormat.format(
        "Input; index: {0}, href: {1}, scope: {2}",
        index, href, Parser.formatScope(scope));
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
  Input()
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
  @XmlStartElement(localName="input", parent="", 
      namespace="http://control4j.lidinsky.cz/application")
  private void startInput(Attributes attributes)
  {
    index = Parser.parseToken(attributes.getValue("index"));
    if (index == null) {} // TODO

    href = Parser.parseToken(attributes.getValue("href"));
    if (href == null) {} // TODO

    try
    {
      scope = Parser.parseScope3(attributes.getValue("scope"));
    }
    catch (ParseException e)
    {
      // TODO
    }
  }

  /**
   *  Does nothing.
   */
  @XmlEndElement(localName="input", parent="", 
      namespace="http://control4j.lidinsky.cz/application")
  private void endProperty()
  { }

  @XmlStartElement(localName="property", parent="input")
  private void startInputProperty(Attributes attributes)
  {
    Property property = new Property(this);
    reader.addHandler(property);
  }

}
