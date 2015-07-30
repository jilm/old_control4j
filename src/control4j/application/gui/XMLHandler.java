package control4j.application.gui;

/*
 *  Copyright 2013, 2014, 2015 Jiri Lidinsky
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

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.awt.Color;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Deque;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import control4j.application.ILoader;
import control4j.gui.components.Screen;
import control4j.gui.Screens;
import control4j.gui.Changer;
import control4j.gui.VisualObject;
import control4j.gui.GuiObject;
import control4j.gui.VisualContainer;
import control4j.gui.ColorParser;
import control4j.SyntaxErrorException;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.Predicate;

import cz.lidinsky.tools.xml.XMLReader;
import cz.lidinsky.tools.xml.IXMLHandler;
import cz.lidinsky.tools.xml.AXMLStartElement;
import cz.lidinsky.tools.xml.AXMLEndElement;
import cz.lidinsky.tools.xml.AXMLDefaultUri;
import cz.lidinsky.tools.chain.Factory;
import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.FunctorUtils;
import cz.lidinsky.tools.reflect.ObjectMapDecorator;
import cz.lidinsky.tools.reflect.ObjectMapUtils;
import cz.lidinsky.tools.reflect.Setter;
import cz.lidinsky.tools.reflect.Getter;

import static control4j.tools.Logger.*;

/**
 *
 *  Reads the gui object tree from the XML file.
 *
 */
@AXMLDefaultUri("http://control4j.lidinsky.cz/gui")
public class XMLHandler implements IXMLHandler
{

  protected static Factory<Object, AbstractAdapter> adapterFactory;

  {
    adapterFactory = new Factory<Object, AbstractAdapter>();
    adapterFactory.add(getInstantiator(GuiToControlAdapter.class));
  }

  public XMLHandler() {}

  public XMLHandler(AbstractAdapter adapter) {
    this.adapter = adapter;
    // set object map decorator
    objectMap = new ObjectMapDecorator<String>(String.class)
      .setGetterFilter(
          ObjectMapUtils.hasAnnotationPredicate(Getter.class))
      .setSetterFilter(
          ObjectMapUtils.hasAnnotationPredicate(Setter.class))
      .setGetterKeyTransformer(
          ObjectMapUtils.getGetterValueTransformer())
      .setSetterKeyTransformer(
          ObjectMapUtils.getSetterValueTransformer())
      .setSetterFactory(
          FunctorUtils.chainedTransformer(
            ColorParser.string2ColorClosureFactory(false),
            ObjectMapUtils.stringSetterClosureFactory(false)));
  }

  public void endProcessing() {}

  public void startProcessing() {}

  protected AbstractAdapter adapter;

  public void setDestination(Object destination) {
    Predicate<AbstractAdapter> filter = notNullPredicate();
    adapter = adapterFactory.findFirst(destination, filter);
  }

  /**
   *  The pointer to the last process object of the gui tree structure.
   *  After the whole XML was successfuly loaded, it should point to
   *  the root object. If no XML file was successfuly loaded yet, it
   *  contains null value.
   */
  private Deque<GuiObject> guiStack = new java.util.ArrayDeque<GuiObject>();

  /**
   *  Returns object structure which was reconstructed from the given
   *  XML document. The output is available, after the load method
   *  was successfuly called (no exception was thrown).
   */
  public Screens getScreens() {
    return (Screens)guiStack.peekLast();
  }

  /**
   *
   */
  @AXMLStartElement("{http://control4j.lidinsky.cz/application}application/gui")
  public boolean applicationGui(Attributes attributes) {
    guiStack.push(new Screens());
    if (adapter != null) {
      adapter.open();
    }
    return true;
  }

  @AXMLStartElement("/gui")
  public boolean gui(Attributes attributes) {
    guiStack.push(new Screens());
    if (adapter != null) {
      adapter.open();
    }
    return true;
  }

  /**
   *
   */
  @AXMLStartElement("gui/screen")
  public boolean screen(Attributes attributes) {
    guiStack.push(new Screen());
    if (adapter != null) {
      adapter.open();
    }
    //((Screens)gui).add(screen);
    return true;
  }

  /**
   *
   */
  @AXMLEndElement("screen")
  public boolean endScreen() {
    finest("/screen");
    if (adapter != null) {
      //adapter.put((Screen)guiStack.pop());
      adapter.close(guiStack.pop());
    }
    return true;
  }

  /**
   *
   */
  @AXMLStartElement("panel")
    public boolean panel(Attributes attributes) {
      String className = attributes.getValue("class");
      VisualContainer panel = (VisualContainer)createInstance(className);
      //((VisualContainer)gui).add(panel);
      guiStack.push(panel);
      if (adapter != null) {
        adapter.open();
      }
      return true;
    }

  /**
   *
   */
  @AXMLEndElement("panel")
  public boolean endPanel() {
    finest("/panel");
    if (adapter != null) {
      //adapter.put((VisualContainer)guiStack.pop());
      adapter.close(guiStack.pop());
    }
    return true;
  }

  /**
   *
   */
  @AXMLStartElement("component")
    public boolean component(Attributes attributes) {
      String className = attributes.getValue("class");
      VisualObject component = (VisualObject)createInstance(className);
      //((VisualContainer)gui).add(component);
      guiStack.push(component);
      if (adapter != null) {
        adapter.open();
      }
      return true;
    }

  /**
   *
   */
  @AXMLEndElement("component")
  public boolean endComponent() {
    finest("/component");
    if (adapter != null) {
      //adapter.put((VisualObject)guiStack.pop());
      adapter.close(guiStack.pop());
    }
    return true;
  }

  /**
   *
   */
  @AXMLStartElement("changer")
    public boolean changer(Attributes attributes) {
      String className = attributes.getValue("class");
      Changer changer = (Changer)createInstance(className);
      //((VisualObject)gui).add(changer);
      guiStack.push(changer);
      if (adapter != null) {
        adapter.open();
      }
      return true;
    }

  /**
   *
   */
  @AXMLEndElement("changer")
  public boolean endChanger() {
    finest("/changer");
    if (adapter != null) {
      //adapter.put((Changer)guiStack.pop());
      adapter.close(guiStack.pop());
    }
    return true;
  }

  /**
   *
   */
  @AXMLStartElement("preference")
  public boolean preference(Attributes attributes) {
    String key = attributes.getValue("key");
    String value = attributes.getValue("value");
    finest("preference; key=" + key + "; value=" + value);
    setPreference(key, value);
    return true;
  }

  @AXMLEndElement("preference")
  public boolean endPreference() {
    return true;
  }

  /**
   *
   */
  @AXMLEndElement("gui")
  public boolean endGui() {
    if (adapter != null) {
      //adapter.put((Screens)guiStack.pop());
      adapter.close(guiStack.pop());
    }
    return true;
  }

  /**
   *  Create and instance of class with given name.
   *
   *  @param className
   *             a fully qualified neme of the class to be instantiated
   *
   *  @return an instance of required class
   *
   *  @throws ClassNotFoundException
   *             if class with given name was not found
   */
  private GuiObject createInstance(String className) {
    try {
      Class _class = Class.forName(className);
      return (GuiObject)_class.newInstance();
    } catch (Exception e) {
      throw new CommonException()
        .setCause(e)
        .set("message", "An exception while instantiation a gui object!")
        .set("class name", className);
    }
  }

  private ObjectMapDecorator<String> objectMap;

  /**
   *
   */
  private void setPreference(String key, String value) {
    try {
      objectMap.setDecorated(guiStack.peek());
      objectMap.put(key, value);
    } catch (Exception e) {
      throw new CommonException()
        .setCause(e)
        .set("message", "Exception while setting object preference!")
        .set("key", key)
        .set("value", value)
        .set("object", objectMap.getDecorated());
    }
  }

  /**
   *  An object which is used instead of a component which is missing.
   */
  private class MissingComponent extends VisualContainer
  {
    protected javax.swing.JComponent createSwingComponent()
    {
      throw new UnsupportedOperationException();
    }
  }

}
