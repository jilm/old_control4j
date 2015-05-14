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
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

//import control4j.tools.SaxReader;
import control4j.application.ILoader;
import control4j.scanner.Scanner;
import control4j.gui.components.Screen;

import control4j.gui.Screens;
import control4j.gui.Changer;
import control4j.gui.VisualObject;
import control4j.gui.GuiObject;
import control4j.gui.VisualContainer;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.Predicate;

import cz.lidinsky.tools.xml.XMLReader;
import cz.lidinsky.tools.xml.IXMLHandler;
import cz.lidinsky.tools.xml.AXMLStartElement;
import cz.lidinsky.tools.xml.AXMLEndElement;
import cz.lidinsky.tools.xml.AXMLDefaultUri;
import cz.lidinsky.tools.chain.Factory;

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

  public void endProcessing() {}

  public void startProcessing() { }

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
  private GuiObject gui = null;

  /**
   *  Returns object structure which was reconstructed from the given
   *  XML document. The output is available, after the load method
   *  was successfuly called (no exception was thrown).
   */
  public Screens getScreens()
  {
    return (Screens)gui;
  }

  /**
   *
   */
  @AXMLStartElement("{http://control4j.lidinsky.cz/application}application/gui")
  public boolean applicationGui(Attributes attributes) {
    gui = new Screens();
    return true;
  }

  @AXMLStartElement("/gui")
  public boolean gui(Attributes attributes) {
    gui = new Screens();
    return true;
  }

  /**
   *
   */
  @AXMLStartElement("gui/screen")
  public boolean screen(Attributes attributes) {
    Screen screen = new Screen();
    ((Screens)gui).add(screen);
    gui = screen;
    return true;
  }

  /**
   *
   */
  @AXMLStartElement("panel")
  public boolean panel(Attributes attributes) {
    try
    {
      String className = attributes.getValue("class");
      VisualContainer panel = (VisualContainer)createInstance(className);
      ((VisualContainer)gui).add(panel);
      gui = panel;
    }
    catch (ClassNotFoundException e)
    {
      MissingComponent missingComponent = new MissingComponent();
      ((VisualContainer)gui).add(missingComponent);
      gui = missingComponent;
    }
    return true;
  }

  /**
   *
   */
  @AXMLStartElement("component")
  public boolean component(Attributes attributes) {
    try
    {
      String className = attributes.getValue("class");
      VisualObject component = (VisualObject)createInstance(className);
      ((VisualContainer)gui).add(component);
      gui = component;
    }
    catch (ClassNotFoundException e)
    {
      MissingComponent missingComponent = new MissingComponent();
      ((VisualContainer)gui).add(missingComponent);
      gui = missingComponent;
    }
    return true;
  }

  /**
   *
   */
  @AXMLStartElement("changer")
  public boolean changer(Attributes attributes) {
    try
    {
      String className = attributes.getValue("class");
      Changer changer = (Changer)createInstance(className);
      ((VisualObject)gui).add(changer);
      gui = changer;
    }
    catch (ClassNotFoundException e)
    {
      MissingComponent missingComponent = new MissingComponent();
      ((VisualContainer)gui).add(missingComponent);
      gui = missingComponent;
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

  /**
   *
   */
  @AXMLEndElement("gui")
  public boolean endGui() {
    if (adapter != null) {
      adapter.put((Screens)gui);
    }
    return true;
  }

  /**
   *
   */
  @AXMLEndElement("screen")
  public boolean endScreen() {
    finest("/screen");
    if (adapter != null) {
      adapter.put((VisualContainer)gui);
    }
    gui = gui.getParent();
    return true;
  }

  /**
   *
   */
  @AXMLEndElement("panel")
  public boolean endPanel() {
    finest("/panel");
    if (adapter != null) {
      adapter.put((VisualContainer)gui);
    }
    gui = gui.getParent();
    return true;
  }

  /**
   *
   */
  @AXMLEndElement("component")
  public boolean endComponent() {
    finest("/component");
    if (adapter != null) {
      adapter.put((VisualObject)gui);
    }
    gui = gui.getParent();
    return true;
  }

  /**
   *
   */
  @AXMLEndElement("changer")
  public boolean endChanger() {
    finest("/changer");
    if (adapter != null) {
      adapter.put((Changer)gui);
    }
    gui = gui.getParent();
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
  private GuiObject createInstance(String className)
  throws ClassNotFoundException
  {
    try
    {
      Class _class = Class.forName(className);
      return (GuiObject)_class.newInstance();
    }
    catch (ClassNotFoundException e)
    {
      catched(getClass().getName(), "createInstance", e);
      throw e;
    }
    catch (java.lang.InstantiationException e)
    {
      // TODO:
      catched(getClass().getName(), "createInstance", e);
    }
    catch (java.lang.IllegalAccessException e)
    {
      // TODO:
      catched(getClass().getName(), "createInstance", e);
    }
    return null;
  }

  /**
   *
   */
  private void setPreference(String key, String value)
  {
    try
    {
      Method setter = Scanner.getSetter(gui, key);
      Class[] parameters = setter.getParameterTypes();
      if (parameters[0] == String.class)
        setter.invoke(gui, value);
      else if (parameters[0] == java.awt.Color.class)
        try
        {
          setter.invoke(gui, new Color(Integer.parseInt(value)));
        }
        catch (NumberFormatException e)
        {
          setter.invoke(gui, control4j.gui.Color.getColor(value));
        }
      else if (parameters[0] == int.class)
        setter.invoke(gui, Integer.parseInt(value));
      else if (parameters[0] == double.class)
        setter.invoke(gui, Double.parseDouble(value));
      else if (parameters[0] == boolean.class)
        setter.invoke(gui, Boolean.parseBoolean(value));
      else
        control4j.tools.Logger.warning("undefined type");
    }
    catch (IllegalAccessException e)
    {
    }
    catch (java.lang.reflect.InvocationTargetException e)
    {
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
