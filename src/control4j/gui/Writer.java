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

import javax.swing.JTabbedPane;
import java.io.OutputStream;
import java.awt.Component;
import java.awt.Container;
import java.awt.Color;
import java.lang.reflect.Method;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import control4j.scanner.Getter;
import control4j.gui.changers.IChangeable;
import control4j.gui.changers.Changer;

/**
 *  Writes given gui into the output streem in XML format.
 */
public class Writer
{

  private static final String NS = "http://gui.control4j.cz";
  private Document document;

  /**
   *  Writes given gui into the given output stream in XML format.
   */
  public void write(JTabbedPane gui, OutputStream outputStream)
  {
    try
    {
      // get DOM object
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder documentBuilder = factory.newDocumentBuilder();
      document = documentBuilder.newDocument();

      // insert root element
      Element root = document.createElementNS(NS, "gui");
      document.appendChild(root);
      writePreferences(root, gui);

      // write screens
      writeScreens(root, gui);
      System.out.println("written");

      // save document into the output stream
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.transform(new DOMSource(document), new StreamResult(outputStream));
    }
    catch(javax.xml.parsers.ParserConfigurationException e)
    {
	  System.out.println(e.getMessage());
    }
    catch(javax.xml.transform.TransformerException e)
    {
	  System.out.println(e.getMessage());
    }
  }

  /**
   *
   */
  private void writeScreens(Element parent, JTabbedPane screens)
  {
    // write all the screens
    for (int i=0; i<screens.getTabCount(); i++)
    {
      // create screen element with label attribute
      Element screen = document.createElementNS(NS, "screen");
      Attr label = document.createAttribute("label");
      label.setValue(screens.getTitleAt(i));
      screen.setAttributeNode(label);
      parent.appendChild(screen);
      // write preferences of the screen
      Container container = (Container)screens.getComponentAt(i);
      writePreferences(screen, container);
      // write all the components of the screen
      for (int j=0; j<container.getComponentCount(); j++)
        writeChild(screen, container.getComponent(j));
    }
  }

  /**
   *
   */
  private void writeChild(Element parent, Component component)
  {
      System.out.println(component);
    if (component instanceof Container)
    {
      Container container = (Container)component;
      if (container.getComponentCount() > 0)
      {
        writeContainer(parent, container);
        return;
      }
    }
    writeComponent(parent, component);
  }

  /**
   *
   */
  private void writeContainer(Element parent, Container container)
  {
    Element element = document.createElementNS(NS, "panel");
    Attr classAttr = document.createAttribute("class");
    classAttr.setValue(container.getClass().getName());
    element.setAttributeNode(classAttr);
    writePreferences(element, container);
    parent.appendChild(element);
    for (int i=0; i<container.getComponentCount(); i++)
      writeChild(element, container.getComponent(i));
    if (container instanceof IChangeable)
      writeChangers(element, (IChangeable)container);
  }

  /**
   *
   */
  private void writeComponent(Element parent, Component component)
  {
    Element element = document.createElementNS(NS, "component");
    Attr classAttr = document.createAttribute("class");
    classAttr.setValue(component.getClass().getName());
    element.setAttributeNode(classAttr);
    writePreferences(element, component);
    parent.appendChild(element);
    if (component instanceof IChangeable)
      writeChangers(element, (IChangeable)component);
    System.out.println("component written");
  }

  /**
   *
   */
  private void writeChangers(Element parent, IChangeable component)
  {
    for (int i=0; i<component.getChangerCount(); i++)
    {
      System.out.println(i);
      Element element = document.createElementNS(NS, "changer");
      Attr classAttr = document.createAttribute("class");
      classAttr.setValue(component.getChanger(i).getClass().getName());
      element.setAttributeNode(classAttr);
      writePreferences(element, component.getChanger(i));
      parent.appendChild(element);
    }
  }

  /**
   *
   */
  private void writePreferences(Element parent, Object object)
  {
    Method[] methods = object.getClass().getMethods();
    for (Method method : methods)
    {
      Getter annotation = method.getAnnotation(Getter.class);
      if (annotation != null)
      {
	try
	{
          String key = annotation.key();
          Object value = method.invoke(object);
	  if (value != null)
	  {
	    if (value instanceof control4j.gui.Color)
	      writePreference(parent, key
	        , ((control4j.gui.Color)value).getKey());
	    else if (value instanceof java.awt.Color)
	      writePreference(parent, key
	        , Integer.toString(((Color)value).getRGB()));
	    else if (value instanceof Method)
	      writePreference(parent, key
	        , control4j.scanner.Scanner.getKey((Method)value));
	    else
	      writePreference(parent, key, value.toString());
	  }
        }
	catch (java.lang.IllegalAccessException e)
	{
	  System.out.println(e.getMessage());
	}
	catch (java.lang.reflect.InvocationTargetException e)
	{
	  System.out.println(e.getMessage());
	}
      }
    }
  }

  /**
   *
   */
  private void writePreference(Element parent, String key, String value)
  {
    Element element = document.createElementNS(NS, "preference");
    Attr keyAttr = document.createAttribute("key");
    keyAttr.setValue(key);
    Attr valueAttr = document.createAttribute("value");
    valueAttr.setValue(value);
    element.setAttributeNode(keyAttr);
    element.setAttributeNode(valueAttr);
    parent.appendChild(element);
  }
}
