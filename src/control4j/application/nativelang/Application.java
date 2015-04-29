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
import control4j.application.ILoader;
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
implements ILoader
{

  protected IAdapter adapter;

  /**
   *
   */
  public Application()
  {
  }

  public void setDestination(Object destination)
  {
    C4jToControlAdapter adapter = new C4jToControlAdapter();
    adapter.setDestination(destination);
    this.adapter = adapter;
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

  @XmlStartElement(localName="application", parent="*",
      namespace="http://control4j.lidinsky.cz/application")
  private void startApplication(Attributes attributes)
  {
    adapter.startLevel();
  }

  @XmlEndElement(localName="application", parent="*",
      namespace="http://control4j.lidinsky.cz/application")
  private void endApplication()
  {
    adapter.endLevel();
  }

  @XmlStartElement(localName="property", parent="application")
  private void startApplicationProperty(Attributes attributes)
  {
    Property property = new Property(adapter);
    reader.addHandler(property);
  }

  @XmlStartElement(localName="resource", parent="application")
  private void startApplicationResource(Attributes attributes)
  {
    ResourceDeclaration resource = new ResourceDeclaration(adapter);
    reader.addHandler(resource);
  }

  @XmlStartElement(localName="signal", parent="application")
  private void startApplicationSignal(Attributes attributes)
  {
    Signal signal = new Signal(adapter);
    reader.addHandler(signal);
  }

  @XmlStartElement(localName="use", parent="application")
  private void startApplicationUse(Attributes attributes)
  {
    Use use = new Use(adapter);
    reader.addHandler(use);
  }

  @XmlStartElement(localName="module", parent="application")
  private void startApplicationModule(Attributes attributes)
  {
    Module module = new Module(adapter);
    reader.addHandler(module);
  }

  @XmlStartElement(localName="block", parent="application")
  private void startApplicationBlock(Attributes attributes)
  {
    Block block = new Block(adapter);
    reader.addHandler(block);
  }

  @XmlStartElement(localName="define", parent="application")
  private void startApplicationDefine(Attributes attributes)
  {
    Define define = new Define(adapter);
    reader.addHandler(define);
  }

  /**
   *  For debug purposes.
   */
  public static void main(String[] args) throws Exception
  {
    java.io.File file = new java.io.File(args[0]);
    java.io.InputStream inputStream = new java.io.FileInputStream(file);
    XmlReader reader = new XmlReader();
    Application application = new Application();
    application.adapter = new PrintAdapter();
    reader.addHandler(application);
    reader.load(inputStream);
  }

}
