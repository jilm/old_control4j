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
 *  Stands for a block element.
 *
 */
public class Block implements IXmlHandler
{

  private String name;

  private int scope;

  private ArrayList<String> input;

  private ArrayList<String> output;

  private ArrayList<Module> modules;

  private ArrayList<Signal> signals;

  private ArrayList<Use> uses;

  /**
   *  Create an empty block object.
   *
   *  @param name
   *             a name of the block. Serves as a link
   *
   *  @param scope
   *             a scope, valid values are: 0 and 1
   */
  public Block(String name, int scope)
  {
    set(name, scope);
  }

  protected void set(String name, int scope)
  {
    if (name == null || name.trim().length() == 0)
      throw new IllegalArgumentException(); // TODO
    if (scope < 0 || scope > 1)
      throw new IllegalArgumentException(); // TODO
    this.name = name;
    this.scope = scope;
  }

  /*
   *
   *    Access Methods
   *
   */

  public void addInput(String name)
  {
    if (name == null) throw new IllegalArgumentException();
    if (input == null) input = new ArrayList<String>();
    input.add(name);
  }

  public void addOutput(String name)
  {
    if (name == null) throw new IllegalArgumentException();
    if (output == null) output = new ArrayList<String>();
    output.add(name);
  }

  public void add(Module module)
  {
    if (module == null) throw new IllegalArgumentException();
    if (modules == null) modules = new ArrayList<Module>();
    modules.add(module);
  }

  public void add(Signal signal)
  {
    if (signal == null) throw new IllegalArgumentException();
    if (signals == null) signals = new ArrayList<Signal>();
    signals.add(signal);
  }

  public void add(Use use)
  {
    if (use == null) throw new IllegalArgumentException();
    if (uses == null) uses = new ArrayList<Use>();
    uses.add(use);
  }

  /*
   *
   *    SAX parser methods
   *
   */

  private XmlReader reader;

  /**
   *  An empty constructor which may be used if the content
   *  of the block will be loaded from XML document.
   */
  Block()
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

  @XmlStartElement(localName="block", parent="", 
      namespace="http://control4j.lidinsky.cz/application")
  private void startBlock(Attributes attributes)
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

  @XmlEndElement(localName="block", parent="",
      namespace="http://control4j.lidinsky.cz/application")
  private void endBlock()
  { }

  @XmlStartElement(localName="input", parent="block")
  private void startBlockInput(Attributes attributes)
  {
    String name = Parser.parseToken(attributes.getValue("", "name"));
    if (name == null) {} // TODO
    addInput(name);
  }

  @XmlEndElement(localName="input", parent="block")
  private void endBlockInput()
  { }

  @XmlStartElement(localName="output", parent="block")
  private void startBlockOutput(Attributes attributes)
  {
    String name = Parser.parseToken(attributes.getValue("", "name"));
    if (name == null) {} // TODO
    addOutput(name);
  }

  @XmlStartElement(localName="module", parent="block")
  private void startBlockModule(Attributes attributes)
  {
    Module module = new Module();
    add(module);
    reader.addHandler(module);
  }

  @XmlStartElement(localName="signal", parent="block")
  private void startBlockSignal(Attributes attributes)
  {
    Signal signal = new Signal();
    add(signal);
    reader.addHandler(signal);
  }

  @XmlStartElement(localName="use", parent="block")
  private void startBlockUse(Attributes attributes)
  {
    Use use = new Use();
    add(use);
    reader.addHandler(use);
  }

}
