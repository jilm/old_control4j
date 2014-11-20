package control4j.gui;

/*
 *  Copyright 2013, 2014 Jiri Lidinsky
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

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.awt.Color;
import java.lang.reflect.Method;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import control4j.tools.SaxReader;
import control4j.tools.XmlStartElement;
import control4j.tools.XmlEndElement;
import control4j.scanner.Scanner;
import control4j.gui.components.Screen;

import static control4j.tools.Logger.*;

/**
 *
 *  Reads the gui object tree from the XML file.
 *
 */
public class Reader extends SaxReader
{

  /**
   *  The pointer to the last process object of the gui tree structure.
   *  After the whole XML was successfuly loaded, it should point to
   *  the root object. If no XML file was successfuly loaded yet, it
   *  contains null value.
   */
  private GuiObject gui = null;

  /**
   *  Loads the whole XML from given intput stream.
   */
  public void load(InputStream inputStream) throws IOException
  {
    super.load(inputStream);
  }

  /**
   *  Returns object structure which was reconstructed from the given
   *  XML document. The output is available, after the load method
   *  was successfuly called (no exception was thrown).
   */
  public Screens get()
  {
    return (Screens)gui;
  }

  /**
   *
   */
  @XmlStartElement(parent="", localName="gui")
  private void gui(Attributes attributes)
  {
    finest("gui");
    gui = new Screens();
  }

  /**
   *
   */
  @XmlStartElement(parent="gui", localName="screen")
  private void screen(Attributes attributes)
  {
    finest("screen");
    Screen screen = new Screen();
    ((Screens)gui).add(screen);
    gui = screen;
  }

  /**
   *
   */
  @XmlStartElement(parent="*", localName="panel")
  private void panel(Attributes attributes)
  {
    finest("panel");
    String className = attributes.getValue("class");
    VisualContainer panel = (VisualContainer)createInstance(className);
    ((VisualContainer)gui).add(panel);
    gui = panel;
  }

  /**
   *
   */
  @XmlStartElement(parent="*", localName="component")
  private void component(Attributes attributes)
  {
    finest("component");
    String className = attributes.getValue("class");
    VisualObject component = (VisualObject)createInstance(className);
    ((VisualContainer)gui).add(component);
    gui = component;
  }

  /**
   *
   */
  @XmlStartElement(parent="*", localName="changer")
  private void changer(Attributes attributes)
  {
    finest("changer");
    String className = attributes.getValue("class");
    Changer changer = (Changer)createInstance(className);
    ((VisualObject)gui).add(changer);
    gui = changer;
  }

  /**
   *
   */
  @XmlStartElement(parent="*", localName="preference")
  private void preference(Attributes attributes)
  {
    String key = attributes.getValue("key");
    String value = attributes.getValue("value");
    finest("preference; key=" + key + "; value=" + value);
    setPreference(key, value);
  }

  /**
   *
   */
  @XmlEndElement(parent="", localName="gui")
  private void endGui()
  {
    finest("/gui");
    //gui = gui.getParent();
  }

  /**
   *
   */
  @XmlEndElement(parent="*", localName="screen")
  private void endScreen()
  {
    finest("/screen");
    gui = gui.getParent();
  }

  /**
   *
   */
  @XmlEndElement(parent="*", localName="panel")
  private void endPanel()
  {
    finest("/panel");
    gui = gui.getParent();
  }

  /**
   *
   */
  @XmlEndElement(parent="*", localName="component")
  private void endComponent()
  {
    finest("/component");
    gui = gui.getParent();
  }

  /**
   *
   */
  private GuiObject createInstance(String className)
  {
    try
    {
      Class _class = Class.forName(className);
      return (GuiObject)_class.newInstance();
    }
    catch (ClassNotFoundException e)
    {
    }
    catch (java.lang.InstantiationException e)
    {
    }
    catch (java.lang.IllegalAccessException e)
    {
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

}
