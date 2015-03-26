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

import control4j.application.ITranslatable;
import control4j.application.Scope;
import control4j.tools.DuplicateElementException;
import control4j.tools.IXmlHandler;
import control4j.tools.XmlReader;
import control4j.tools.XmlStartElement;
import control4j.tools.XmlEndElement;

/**
 *
 *  The root object of the whole application, it holds instances of
 *  other object declarations which were loaded. It contains only
 *  objects that belong to the native c4j language.
 *
 */
public class Application extends DescriptionBase 
implements ITranslatable, IXmlHandler
{

  /** definitions */
  private ArrayList<Define> definitions;

  /** resource definitions */
  private ArrayList<ResourceDeclaration> resources;

  /** block definitions */
  private ArrayList<Block> blocks;

  /** signal definitions */
  private ArrayList<Signal> signals;

  /** nested applications ??? */
  private ArrayList<Application> applications;

  /** modules */
  private ArrayList<Module> modules;

  /** block placements */
  private ArrayList<Use> uses;

  /**
   *  Creates an empty object.
   */
  public Application()
  {
  }

  /**
   *
   */
  public void translate(control4j.application.Application application)
  {

    Scope localScope = application.getScopePointer();

    // translate all of the definitions
    if (definitions != null)
      for (Define definition : definitions)
      {
	try
	{
          Scope scope = 
              definition.getScope() == 0 ? Scope.getGlobal() : localScope;
          application.putDefinition(
              definition.getName(), scope, definition.getValue());
	}
	catch (DuplicateElementException e)
	{
	  // TODO
	}
      }

    // translate all of the properties
    super.translate(application, localScope);

    // translate all of the resource definitions
    if (resources != null)
      for (ResourceDeclaration resource : resources)
      {
	try
	{
	  control4j.application.Resource destination =
	      new control4j.application.Resource(resource.getClassName());
	  resource.translate(destination, localScope);
	  application.putResource(resource.getName(), 
	      resolveScope(resource.getScope(), localScope), destination);
	}
	catch (DuplicateElementException e)
	{
	  // TODO
	}
      }

    // translate all of the modules
    if (modules != null)
      for (Module module : modules)
      {
	control4j.application.Module destination =
	    new control4j.application.Module(module.getClassName());
	module.translate(destination, localScope);
	application.addModule(destination);
      }

    // translate all of the blocks
    if (blocks != null)
      for (Block block : blocks)
      {
	control4j.application.Block destination =
	    new control4j.application.Block();
	block.translate(destination, localScope);
	try
	{
	  application.putBlock(block.getName(), 
	      resolveScope(block.getScope(), localScope), destination);
	}
	catch (DuplicateElementException e)
	{
	  // TODO
	}
      }

    // translate all of the signals
    if (signals != null)
      for (Signal signal : signals)
      {
	control4j.application.Signal destination
	    = new control4j.application.Signal();
	signal.translate(destination, localScope);
	try
	{
	  application.putSignal(signal.getName(),
	      resolveScope(signal.getScope(), localScope), destination);
	}
	catch (DuplicateElementException e)
	{
	  // TODO
	}
      }

    // translate all of the nested applications
    if (applications != null)
      for (Application nested : applications)
      {
	application.startScope();
	nested.translate(application);
        application.endScope();
      }

    // translate all of the use objects
    if (uses != null)
      for (Use use : uses)
      {
        control4j.application.Use destination
            = new control4j.application.Use(use.getHref(), 
	    resolveScope(use.getScope(), localScope));
	use.translate(destination, localScope);
	application.addUse(destination);
      }
  }

  /*
   *
   *    Access Methods
   *
   */

  public void add(Define define)
  {
    if (define == null) throw new IllegalArgumentException();
    if (definitions == null) definitions = new ArrayList<Define>();
    definitions.add(define);
  }

  public void add(ResourceDeclaration resource)
  {
    if (resource == null) throw new IllegalArgumentException();
    if (resources == null) resources = new ArrayList<ResourceDeclaration>();
    resources.add(resource);
  }

  public void add(Block block)
  {
    if (block == null) throw new IllegalArgumentException();
    if (blocks == null) blocks = new ArrayList<Block>();
    blocks.add(block);
  }

  public void add(Signal signal)
  {
    if (signal == null) throw new IllegalArgumentException();
    if (signals == null) signals = new ArrayList<Signal>();
    signals.add(signal);
  }

  public void add(Application application)
  {
    if (application == null) throw new IllegalArgumentException();
    if (applications == null) applications = new ArrayList<Application>();
    applications.add(application);
  }

  public void add(Module module)
  {
    if (module == null) throw new IllegalArgumentException();
    if (modules == null) modules = new ArrayList<Module>();
    modules.add(module);
  }

  public void add(Use use)
  {
    if (use == null) throw new IllegalArgumentException();
    if (uses == null) uses = new ArrayList<Use>();
    uses.add(use);
  }

  /*
   *
   *    Implementation of IXMLHandler
   *
   */

  private XmlReader reader;

  public void startProcessing(XmlReader reader)
  {
    this.reader = reader;
  }

  /**
   *  Cleen up.
   */
  public void endProcessing()
  {
    this.reader = null;
  }

  @XmlStartElement(localName="application", parent="",
      namespace="http://control4j.lidinsky.cz/application")
  private void startApplication(Attributes attributes)
  { }

  @XmlEndElement(localName="application", parent="",
      namespace="http://control4j.lidinsky.cz/application")
  private void endApplication()
  { }

  @XmlStartElement(localName="property", parent="application")
  private void startApplicationProperty(Attributes attributes)
  {
    Property property = new Property();
    addProperty(property);
    reader.addHandler(property);
  }

  @XmlStartElement(localName="resource", parent="application")
  private void startApplicationResource(Attributes attributes)
  {
    ResourceDeclaration resource = new ResourceDeclaration();
    add(resource);
    reader.addHandler(resource);
  }

  @XmlStartElement(localName="signal", parent="application")
  private void startApplicationSignal(Attributes attributes)
  {
    Signal signal = new Signal();
    add(signal);
    reader.addHandler(signal);
  }

  @XmlStartElement(localName="use", parent="application")
  private void startApplicationUse(Attributes attributes)
  {
    Use use = new Use();
    add(use);
    reader.addHandler(use);
  }

  @XmlStartElement(localName="module", parent="application")
  private void startApplicationModule(Attributes attributes)
  {
    Module module = new Module();
    add(module);
    reader.addHandler(module);
  }

  @XmlStartElement(localName="block", parent="application")
  private void startApplicationBlock(Attributes attributes)
  {
    Block block = new Block();
    add(block);
    reader.addHandler(block);
  }

  @XmlStartElement(localName="application", parent="application")
  private void startApplicationApplication(Attributes attributes)
  {
    Application application = new Application();
    add(application);
    reader.addHandler(application);
  }

  @XmlStartElement(localName="define", parent="application")
  private void startApplicationDefine(Attributes attributes)
  {
    Define define = new Define();
    add(define);
    reader.addHandler(define);
  }

}
