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
import java.util.LinkedList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import control4j.application.gui.SaxReader;
import control4j.application.gui.XmlStartElement;
import control4j.application.gui.XmlEndElement;
import control4j.gui.Screens;
import control4j.gui.components.Screen;
import java.awt.Container;
import java.awt.Component;
import java.awt.Color;
import java.lang.reflect.Method;
import control4j.scanner.Setter;
import control4j.gui.changers.Changer;
import control4j.gui.changers.IChangeable;

public class Reader extends SaxReader
{
  private Screens screens;
  private Screen screen;
  private Container component;
  private Changer changer;

  public void load(InputStream inputStream) throws IOException
  {
    super.load(inputStream);
  }

  public Screens get()
  {
    return screens;
  }

  @XmlStartElement(parent="", localName="gui")
  public void gui(Attributes attributes)
  {
    System.out.println("gui");
    screens = new Screens();
  }

  @XmlStartElement(parent="gui", localName="screen")
  public void screen(Attributes attributes)
  {
    System.out.println("screen");
    screen = screens.addScreen();
  }

  @XmlStartElement(parent="screen", localName="panel")
  public void panel(Attributes attributes)
  {
    String className = attributes.getValue("class");
    component = (Container)createInstance(className);
    screen.add(component);
  }

  @XmlStartElement(parent="screen", localName="component")
  public void screenComponent(Attributes attributes)
  {
    String className = attributes.getValue("class");
    component = (Container)createInstance(className);
    screen.add(component);
  }

  @XmlStartElement(parent="panel", localName="panel")
  public void panelContent(Attributes attributes)
  {
    String className = attributes.getValue("class");
    Container component = (Container)createInstance(className);
    this.component.add(component);
    this.component = component;
  }

  @XmlStartElement(parent="panel", localName="component")
  public void component(Attributes attributes)
  {
    String className = attributes.getValue("class");
    Container component = (Container)createInstance(className);
    this.component.add(component);
    this.component = component;
  }

  @XmlStartElement(parent="component", localName="changer")
  public void componentChanger(Attributes attributes)
  {
    String className = attributes.getValue("class");
    changer = (Changer)createInstance(className);
    ((IChangeable)component).addChanger(changer);
  }

  @XmlStartElement(parent="changer", localName="preference")
  public void changerPreference(Attributes attributes)
  {
    String key = attributes.getValue("key");
    String value = attributes.getValue("value");
    setPreference(changer, key, value);
  }

  @XmlStartElement(parent="panel", localName="preference")
  public void panelPreference(Attributes attributes)
  {
    String key = attributes.getValue("key");
    String value = attributes.getValue("value");
    setPreference(component, key, value);
  }

  @XmlStartElement(parent="component", localName="preference")
  public void componentPreference(Attributes attributes)
  {
    String key = attributes.getValue("key");
    String value = attributes.getValue("value");
    setPreference(component, key, value);
  }

  @XmlStartElement(parent="gui", localName="preference")
  public void guiPreference(Attributes attributes)
  {
    String key = attributes.getValue("key");
    String value = attributes.getValue("value");
    setPreference(screens, key, value);
  }

  @XmlStartElement(parent="screen", localName="preference")
  public void screenPreference(Attributes attributes)
  {
    String key = attributes.getValue("key");
    String value = attributes.getValue("value");
    setPreference(screen, key, value);
  }

  @XmlStartElement(parent="component", localName="signal")
  public void componentSignal(Attributes attributes)
  {
    String key = attributes.getValue("key");
    String name = attributes.getValue("name");
  }

  @XmlEndElement(parent="", localName="gui")
  public void endGui()
  {
  }

  @XmlEndElement(parent="gui", localName="screens")
  public void endScreens()
  {
  }

  @XmlEndElement(parent="screens", localName="screen")
  public void endScreen()
  {
  }

  @XmlEndElement(parent="screen", localName="panel")
  public void endPanel()
  {
  }

  @XmlEndElement(parent="panel", localName="panel")
  public void endPanelPanel()
  {
    component = component.getParent();
  }

  @XmlEndElement(parent="panel", localName="component")
  public void endPanelComponent()
  {
    component = component.getParent();
  }

  private Object createInstance(String className)
  {
    try
    {
      Class _class = Class.forName(className);
      return _class.newInstance();
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

  private void setPreference(Object object, String key, String value)
  {
    try
    {
      Method[] methods = object.getClass().getMethods();
      for (Method method : methods)
      {
        Setter annotation = method.getAnnotation(Setter.class);
	if (annotation != null && annotation.key().equals(key))
	{
	  Class[] parameters = method.getParameterTypes();
	  if (parameters[0] == String.class)
	    method.invoke(object, value);
	  else if (parameters[0] == java.awt.Color.class)
	    try
	    {
	      method.invoke(object, new Color(Integer.parseInt(value)));
	    }
	    catch (NumberFormatException e)
	    {
	      method.invoke(object, control4j.gui.Color.getColor(value));
	    }
	  else if (parameters[0] == int.class)
	    method.invoke(object, Integer.parseInt(value));
	  else if (parameters[0] == double.class)
	    method.invoke(object, Double.parseDouble(value));
	  else if (parameters[0] == boolean.class)
	    method.invoke(object, Boolean.parseBoolean(value));
	  else if (parameters[0] == Method.class)
	  {
	    Object parent = component;
	    Method setter = control4j.scanner.Scanner.getSetter(parent, value);
	    System.out.println(setter);
	    method.invoke(object, setter);
	  }
	  else
	    System.out.println("undefined type");
	}
      }
    }
    catch (IllegalAccessException e)
    {
    }
    catch (java.lang.reflect.InvocationTargetException e)
    {
    }
  }

}
