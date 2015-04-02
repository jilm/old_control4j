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
import java.util.Set;
import java.util.Map;
import org.xml.sax.Attributes;

import control4j.application.Scope;
import control4j.tools.IXmlHandler;
import control4j.tools.XmlReader;
import control4j.tools.XmlStartElement;
import control4j.tools.XmlEndElement;

/**
 *
 *  Stands for a module element.
 *
 */
public class Module extends DescriptionBase implements IXmlHandler
{

  private String className;

  public String getClassName()
  {
    return className;
  }

  private ArrayList<Resource> resources = new ArrayList<Resource>();

  private ArrayList<Input> input = new ArrayList<Input>();

  private ArrayList<Output> output = new ArrayList<Output>();

  private ArrayList<String> inputTags = new ArrayList<String>();

  private ArrayList<String> outputTags = new ArrayList<String>();

  /**
   *  Creates an empty module object.
   *
   *  @param className
   *             a name of the class that implements functionality
   *             of a module
   */
  public Module(String className)
  {
    set(className);
  }

  protected void set(String className)
  {
    if (className == null || className.trim().length() == 0)
      throw new IllegalArgumentException(); // TODO
    this.className = className;
  }

  /**
   *  Translate this module object to the given one.
   *
   *  @param destination
   *             an object where translated information will be stored
   *
   *  @param localScope
   *             a scope under which the module object was defined.
   *
   *  @param inputSubstitution
   *             a map alias - Input which is used for block expansion.
   *             Null value is accepted.
   *
   *  @param outputSubstitution
   *             a map alias - Ouput that is used for block expansion.
   *             Null value is accepted.
   */
  public void translate(
      control4j.application.Module destination, Scope localScope,
      Map<String, control4j.application.Input> inputSubstitution,
      Map<String, control4j.application.Output> outputSubstitution)
  {
    // translate configuration
    super.translate(destination, localScope);

    // translate resource definitions
    for (Resource resource : resources)
    {
      if (resource.isReference())
      {
	destination.putResource(resource.getKey(), resource.getHref(), 
	    resolveScope(resource.getScope(), localScope));
      }
      else
      {
        control4j.application.Resource translated 
	    = new control4j.application.Resource(resource.getClassName());
        resource.translate(translated, localScope);
	destination.putResource(resource.getKey(), translated);
      }
    }

    // translate input
    for (Input inp : input)
    {
      control4j.application.Input translated;
      // if the scope is local and the name is present in the
      // input substitution map, use the input from that map
      if (inp.getScope() == Parser.LOCAL_SCOPE_CODE 
	  && inputSubstitution != null 
	  && inputSubstitution.containsKey(inp.getHref()))
      {
	translated = inputSubstitution.get(inp.getHref());
      }
      // if this input is not a block input
      else
      {
	translated = new control4j.application.Input(
	    resolveScope(inp.getScope(), localScope), inp.getHref());
	inp.translate(translated, localScope);
      }
      // store translated input into the module object
      String strIndex = inp.getIndex();
      if (strIndex == null)
      {
	// an input with variable index
	destination.putInput(translated);
      }
      else
      {
	// an input with fixed index
	try
	{
	  int index = Integer.parseInt(strIndex);
	  destination.putInput(index, translated);
	}
	catch (NumberFormatException e)
	{
	  // TODO
	}
      }
    }

    // translate output
    for (Output out : output)
    {
      control4j.application.Output translated;
      // if the scope is local and the name is present in the
      // output substitution map, use the output from that map
      if (out.getScope() == Parser.LOCAL_SCOPE_CODE 
	  && outputSubstitution != null 
	  && outputSubstitution.containsKey(out.getHref()))
      {
	translated = outputSubstitution.get(out.getHref());
      }
      // if this output is not a block output
      else
      {
	translated = new control4j.application.Output(
	    resolveScope(out.getScope(), localScope), out.getHref());
	out.translate(translated, localScope);
      }
      // store translated output into the module object
      String strIndex = out.getIndex();
      if (strIndex == null)
      {
	// an output with variable index
	destination.putOutput(translated);
      }
      else
      {
	// an output with fixed index
	try
	{
	  int index = Integer.parseInt(strIndex);
	  destination.putOutput(index, translated);
	}
	catch (NumberFormatException e)
	{
	  // TODO
	}
      }
    }

    // translate tagged input
    for (String tag : inputTags)
      destination.addInputTag(tag);

    // translate tagged output
    for (String tag : outputTags)
      destination.addOutputTag(tag);

  }

  /*
   *
   *    SAX parser methods
   *
   */

  private XmlReader reader;

  /**
   *  An empty constructor which may be used if the content
   *  of the module will be loaded from XML document.
   */
  Module()
  { }

  public void startProcessing(XmlReader reader)
  {
    this.reader = reader;
  }

  public void endProcessing()
  {
    this.reader = null;
  }

  @XmlStartElement(localName="module", parent="", 
      namespace="http://control4j.lidinsky.cz/application")
  private void startModule(Attributes attributes)
  {
    className = Parser.parseToken(attributes.getValue("class"));
    if (className == null) {} // TODO
  }

  @XmlEndElement(localName="module", parent="",
      namespace="http://control4j.lidinsky.cz/application")
  private void endModule()
  { }

  @XmlStartElement(localName="property", parent="module")
  private void startModuleProperty(Attributes attributes)
  {
    Property property = new Property();
    addProperty(property);
    reader.addHandler(property);
  }

  @XmlStartElement(localName="resource", parent="module")
  private void startModuleResource(Attributes attributes)
  {
    Resource resource = new Resource();
    resources.add(resource);
    reader.addHandler(resource);
  }

  @XmlStartElement(localName="input", parent="module")
  private void startModuleInput(Attributes attributes)
  {
    Input input = new Input();
    this.input.add(input);
    reader.addHandler(input);
  }

  @XmlStartElement(localName="output", parent="module")
  private void startModuleOutput(Attributes attributes)
  {
    Output output = new Output();
    this.output.add(output);
    reader.addHandler(output);
  }

  @XmlStartElement(localName="input-tag", parent="module")
  private void startModuleInputTag(Attributes attributes)
  {
    String tag = Parser.parseToken(attributes.getValue("tag"));
    if (tag == null) {} // TODO
    else
      inputTags.add(tag);
  }

  @XmlEndElement(localName="input-tag", parent="module")
  private void endModuleInputTag()
  { }

  @XmlStartElement(localName="output-tag", parent="module")
  private void startModuleOutputTag(Attributes attributes)
  {
    String tag = Parser.parseToken(attributes.getValue("tag"));
    if (tag == null) {} // TODO
    else
      outputTags.add(tag);
  }

  @XmlEndElement(localName="output-tag", parent="module")
  private void endModuleOutputTag()
  { }

}
