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

import static cz.lidinsky.tools.chain.Factory.getInstantiator;
import static org.apache.commons.collections4.PredicateUtils.notNullPredicate;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import control4j.tools.ParseException;
import org.xml.sax.Attributes;

import control4j.application.ITranslatable;
import control4j.application.Scope;
import control4j.application.ILoader;
import control4j.tools.DuplicateElementException;
import cz.lidinsky.tools.xml.IXMLHandler;
import cz.lidinsky.tools.xml.XMLReader;
import cz.lidinsky.tools.xml.AXMLStartElement;
import cz.lidinsky.tools.xml.AXMLEndElement;
import cz.lidinsky.tools.xml.AXMLDefaultUri;
import cz.lidinsky.tools.chain.Factory;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.awt.Color;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.Predicate;

/**
 *
 *  The root object of the whole application, it holds instances of
 *  other object declarations which were loaded. It contains only
 *  objects that belong to the native c4j language.
 *
 */
@AXMLDefaultUri("http://control4j.lidinsky.cz/application")
public class XMLHandler implements IXMLHandler
{

  protected static Factory<Object, AbstractAdapter> adapterFactory;

  {
    adapterFactory = new Factory<Object, AbstractAdapter>();
    adapterFactory.add(getInstantiator(C4jToControlAdapter.class));
  }

  private AbstractAdapter adapter;

  protected XMLReader reader;

  public void setXMLReader(XMLReader reader) {
    this.reader = notNull(reader);
  }

  /**
   *
   */
  public XMLHandler() { }

  public void setDestination(Object destination) {
    Predicate<AbstractAdapter> filter = notNullPredicate();
    adapter = adapterFactory.findFirst(destination, filter);
  }

  /*
   *
   *    Implementation of IXMLHandler
   *
   */

  public void startProcessing() { }

  /**
   *  Cleen up.
   */
  public void endProcessing() { }

  /*
   *
   *     Application
   *
   */

  @AXMLStartElement("/application")
  public boolean startApplication(Attributes attributes) {
    adapter.startLevel();
    return true;
  }

  @AXMLStartElement("application/application")
  public boolean startApplicationApplication(Attributes attributes) {
    adapter.startLevel();
    return true;
  }

  @AXMLEndElement("application")
  public boolean endApplication() {
    adapter.endLevel();
    return true;
  }

  @AXMLStartElement("application/property")
  public boolean startApplicationProperty(Attributes attributes) {
    Property property = getProperty(attributes);
    adapter.put(property);
    return true;
  }

  /*
   *
   *      Define
   *
   */

  private Define define;

  @AXMLStartElement("application/define")
  public boolean startApplicationDefine(Attributes attributes) {
    try {
      define = new Define();
      setDeclarationReference(define);
      define.setName(attributes.getValue("name"));
      define.setScope(Parser.parseScope2(attributes.getValue("scope")));
      define.setValue(attributes.getValue("value"));
    } catch (control4j.tools.ParseException e) {
      // TODO:
    }
    return true;
  }

  @AXMLEndElement("define")
  public boolean endDefine() {
    adapter.put(define);
    define = null;
    return true;
  }

  /*
   *
   *     Signal
   *
   */

  private Signal signal;

  @AXMLStartElement("application/signal")
  public boolean startApplicationSignal(Attributes attributes) {
    try {
      signal = new Signal();
      setDeclarationReference(signal);
      signal.setName(attributes.getValue("name"));
      signal.setScope(Parser.parseScope2(attributes.getValue("scope")));
    } catch (control4j.tools.ParseException e) {
      // TODO:
    }
    return true;
  }

  @AXMLEndElement("application/signal")
  public boolean endSignal() {
    adapter.put(signal);
    signal = null;
    return true;
  }

  @AXMLStartElement("signal/property")
  public boolean startSignalProperty(Attributes attributes) {
    Property property = getProperty(attributes);
    signal.put(property);
    return true;
  }

  @AXMLEndElement("property")
  public boolean endProperty() {
    return true;
  }

  @AXMLStartElement("signal/value-t-1")
  public boolean startSignalValue(Attributes attributes) {
    return true;
  }

  @AXMLEndElement("value-t-1")
  public boolean endValue() {
    return true;
  }

  @AXMLStartElement("value-t-1/invalid")
  public boolean startValueInvalid(Attributes attributes) {
    signal.setInvalidDefaultValue();
    return true;
  }

  @AXMLEndElement("invalid")
  public boolean endInvalid() {
    return true;
  }

  @AXMLStartElement("value-t-1/signal")
  public boolean startValueSignal(Attributes attributes) {
    signal.setDefaultValue(attributes.getValue("value"));
    return true;
  }

  @AXMLEndElement("value-t-1/signal")
  public boolean endValueSignal() {
    return true;
  }

  private Tag tag;

  @AXMLStartElement("signal/tag")
  public boolean startSignalTag(Attributes attributes) {
    tag = new Tag();
    setDeclarationReference(tag);
    // TODO:
    return true;
  }

  @AXMLEndElement("signal/tag")
  public boolean endSignalTag() {
    signal.add(tag);
    tag = null;
    return true;
  }

  /*
   *
   *     Resource
   *
   */

  private ResourceDef resource;

  @AXMLStartElement("application/resource")
  public boolean startApplicationResource(Attributes attributes) {
    try {
      resource = new ResourceDef();
      setDeclarationReference(resource);
      resource.setClassName(attributes.getValue("class"))
          .setName(attributes.getValue("name"))
          .setScope(Parser.parseScope2(attributes.getValue("scope")));
    } catch (ParseException e) {
      // TODO:
    }
    return true;
  }

  @AXMLEndElement("application/resource")
  public boolean endApplicationResource() {
    adapter.put(resource);
    resource = null;
    return true;
  }

  @AXMLStartElement("application/resource/property")
  public boolean startResourceProperty(Attributes attributes) {
    resource.put(getProperty(attributes));
    return true;
  }

  /*
   *
   *     Block
   *
   */

  private Block block;

  @AXMLStartElement("application/block")
  public boolean startApplicationBlock(Attributes attributes) {
    try {
      block = new Block();
      setDeclarationReference(block);
      block.setName(attributes.getValue("name"))
           .setScope(Parser.parseScope2(attributes.getValue("scope")));
    } catch (ParseException e) {
      // TODO:
    }
    return true;
  }

  @AXMLEndElement("block")
  public boolean endBlock() {
    adapter.put(block);
    block = null;
    return true;
  }

  @AXMLStartElement("block/input")
  public boolean startBlockInput(Attributes attributes) {
    block.addInput(attributes.getValue("name"));
    return true;
  }

  @AXMLEndElement("block/input")
  public boolean endBlockInput() {
    return true;
  }

  @AXMLStartElement("block/output")
  public boolean startBlockOutput(Attributes attributes) {
    block.addOutput(attributes.getValue("name"));
    return true;
  }

  @AXMLEndElement("block/output")
  public boolean endBlockOutput() {
    return true;
  }

  @AXMLEndElement("block/module")
  public boolean endBlockModule() {
    block.add(module);
    module = null;
    return true;
  }

  @AXMLEndElement("block/signal")
  public boolean endBlockSignal() {
    block.add(signal);
    signal = null;
    return true;
  }

  @AXMLEndElement("block/use")
  public boolean endBlockUse() {
    block.add(use);
    use = null;
    return true;
  }

  /*
   *
   *     Use
   *
   */

  private Use use;

  @AXMLStartElement("use")
  public boolean startUse(Attributes attributes)
  {
    try {
      use = new Use();
      setDeclarationReference(use);
      use.setHref(attributes.getValue("href"))
          .setScope(Parser.parseScope3(attributes.getValue("scope")));
    } catch (ParseException e) {
      // TODO:
    }
    return true;
  }

  @AXMLEndElement("application/use")
  public boolean endApplicationUse() {
    adapter.put(use);
    use = null;
    return true;
  }

  @AXMLStartElement("use/property")
  public boolean startUseProperty(Attributes attributes) {
    use.put(getProperty(attributes));
    return true;
  }

  @AXMLStartElement("use/input")
  public boolean startUseInput(Attributes attributes) {
    try {
      use.add(
          new Input()
              .setIndex(attributes.getValue("index"))
              .setHref(attributes.getValue("href"))
              .setScope(Parser.parseScope3(attributes.getValue("scope")))
      );
    } catch (ParseException e) {
      // TODO:
    }
    return true;
  }

  @AXMLStartElement("use/output")
  public boolean startUseOutput(Attributes attributes) {
    try {
      use.add(
          new Output()
              .setIndex(attributes.getValue("index"))
              .setHref(attributes.getValue("href"))
              .setScope(Parser.parseScope3(attributes.getValue("scope")))
      );
    } catch (ParseException e) {
      // TODO:
    }
    return true;
  }

  /*
   *
   *     Module
   *
   */

  Module module;

  @AXMLStartElement("module")
  public boolean startModule(Attributes attributes)
  {
    module = new Module()
        .setClassName(attributes.getValue("class"));
    return true;
  }

  @AXMLEndElement("application/module")
  public boolean endApplicationModule() {
    adapter.put(module);
    module = null;
    return true;
  }

  @AXMLStartElement("module/property")
  public boolean startModuleProperty(Attributes attributes) {
    module.put(getProperty(attributes));
    return true;
  }

  private Input input;

  @AXMLStartElement("module/input")
  public boolean startModuleInput(Attributes attributes) {
    try {
      input = new Input()
          .setIndex(attributes.getValue("index"))
          .setHref(attributes.getValue("href"))
          .setScope(Parser.parseScope3(attributes.getValue("scope")));
    } catch (ParseException e) {
      // TODO:
    }
    return true;
  }

  @AXMLEndElement("module/input")
  public boolean endModuleInput() {
    module.add(input);
    input = null;
    return true;
  }

  @AXMLStartElement("module/input/property")
  public boolean startModuleInputProperty(Attributes attributes) {
    input.put(getProperty(attributes));
    return true;
  }

  private Output output;

  @AXMLStartElement("module/output")
  public boolean startModuleOutput(Attributes attributes) {
    try {
      output = new Output()
          .setIndex(attributes.getValue("index"))
          .setHref(attributes.getValue("href"))
          .setScope(Parser.parseScope3(attributes.getValue("scope")));
    } catch (ParseException e) {
      // TODO:
    }
    return true;
  }

  @AXMLEndElement("module/output")
  public boolean endModuleOutput() {
    module.add(output);
    output = null;
    return true;
  }

  @AXMLStartElement("module/output/property")
  public boolean startModuleOutputProperty(Attributes attributes) {
    output.put(getProperty(attributes));
    return true;
  }

  private Resource moduleResource;

  @AXMLStartElement("module/resource")
  public boolean startModuleResource(Attributes attributes) {
    try {
      moduleResource = new Resource()
          .setKey(attributes.getValue("key"))
          .setHref(attributes.getValue("href"))
          .setScope(Parser.parseScope3(attributes.getValue("scope")))
          .setClassName(attributes.getValue("class"));
    } catch (ParseException e) {
      // TODO:
    }
    return true;
  }

  @AXMLEndElement("module/resource")
  public boolean endModuleResource() {
    module.add(moduleResource);
    moduleResource = null;
    return true;
  }

  @AXMLStartElement("module/resource/property")
  public boolean startModuleResourceProperty(Attributes attributes) {
    moduleResource.put(getProperty(attributes));
    return true;
  }

  @AXMLStartElement("module/input-tag")
  public boolean startModuleInputTag(Attributes attributes) {
    module.addInputTag(attributes.getValue("tag"));
    return true;
  }

  @AXMLEndElement("input-tag")
  public boolean endInputTag() {
    return true;
  }

  @AXMLStartElement("module/output-tag")
  public boolean startModuleOutputTag(Attributes attributes) {
    module.addOutputTag(attributes.getValue("tag"));
    return true;
  }

  @AXMLEndElement("output-tag")
  public boolean endOutputTag() {
    return true;
  }

  private Property getProperty(Attributes attributes) {

    try {
      return new Property()
          .setKey(Parser.parseToken(attributes.getValue("key")))
          .setValue(attributes.getValue("value"))
          .setHref(Parser.parseToken(attributes.getValue("href")))
          .setScope(Parser.parseScope3(attributes.getValue("scope")));
    } catch (ParseException e) {
      // TODO:
    }
    return null;
  }

  protected void setDeclarationReference(DeclarationBase object) {
    if (reader != null) {
      object.setDeclarationReference(reader.getLocation());
    }
  }

  /**
   *  For debug purposes.
   */
  public static void main(String[] args) throws Exception
  {
    java.io.File file = new java.io.File(args[0]);
    java.io.InputStream inputStream = new java.io.FileInputStream(file);
    XMLHandler handler = new XMLHandler();
    handler.adapter = new PrintAdapter();
    XMLReader reader = new XMLReader();
    reader.addHandler(handler);
    reader.load(inputStream);
  }

}
