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
 *  Represents a use element.
 *
 */
public class Use extends Configurable implements IXmlHandler
{

  private String href;

  private int scope;

  private ArrayList<Input> input = new ArrayList<Input>();

  private ArrayList<Output> output = new ArrayList<Output>();

  /**
   *  Returns a string which contains fields of this object in
   *  the human readable form.
   */
  @Override
  public String toString()
  {
    return java.text.MessageFormat.format(
	"Use; href: {0}, scope: {1}", href, Parser.formatScope(scope));
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
  Use()
  { }

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
  @XmlStartElement(localName="use", parent="", 
      namespace="http://control4j.lidinsky.cz/application")
  private void startUse(Attributes attributes)
  {
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
  @XmlEndElement(localName="use", parent="", 
      namespace="http://control4j.lidinsky.cz/application")
  private void endUse()
  { }

  @XmlStartElement(localName="property", parent="use")
  private void startUseProperty(Attributes attributes)
  {
    Property property = new Property();
    addProperty(property);
    reader.addHandler(property);
  }

  @XmlStartElement(localName="input", parent="use")
  private void startUseInput(Attributes attributes)
  {
    Input input = new Input();
    this.input.add(input);
    reader.addHandler(input);
  }

  @XmlStartElement(localName="output", parent="use")
  private void startUseOutput(Attributes attributes)
  {
    Output output = new Output();
    this.output.add(output);
    reader.addHandler(output);
  }

}