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
 *  Represents a use element.
 *
 */
public class Use extends Configurable implements IXmlHandler, IAdapter
{

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

  private ArrayList<Input> input = new ArrayList<Input>();

  private ArrayList<Output> output = new ArrayList<Output>();

  public void translate(
      control4j.application.Use destination, Scope localScope)
  {
    // translate configuration
    super.translate(destination, localScope);

    // translate input
    if (input != null)
      for (Input inp : input)
      {
        control4j.application.Input destInput
            = new control4j.application.Input(
            resolveScope(inp.getScope(), localScope), inp.getHref());
        inp.translate(destInput, localScope);
        destination.putInput(inp.getIndex(), destInput);
      }

    // translate output
    if (output != null)
      for (Output out : output)
      {
        control4j.application.Output destOutput
            = new control4j.application.Output(
            resolveScope(out.getScope(), localScope), out.getHref());
        out.translate(destOutput, localScope);
        destination.putOutput(out.getIndex(), destOutput);
      }
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

  protected IAdapter adapter;

  /**
   *  An empty constructor for objects that will be loaded
   *  from a XML document.
   */
  Use(IAdapter adapter)
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
   */
  @XmlEndElement(localName="use", parent="", 
      namespace="http://control4j.lidinsky.cz/application")
  private void endUse()
  {
    adapter.put(this);
  }

  @XmlStartElement(localName="property", parent="use")
  private void startUseProperty(Attributes attributes)
  {
    Property property = new Property(this);
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

  @Override
  public void toString(ToStringBuilder builder)
  {
    super.toString(builder);
    builder.append("href", href)
        .append("scope", scope)
        .append("input", input)
        .append("output", output);
  }

}
