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

import java.io.OutputStream;
import java.awt.Color;

import java.util.Map;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import control4j.scanner.Scanner;
import control4j.scanner.Item2;
import control4j.gui.components.Screen;

/**
 *
 *  Writes given gui into the output streem in XML format.
 *
 */
public class Writer
{

  /**
   *
   */
  public static final String NS = "http://gui.control4j.cz";

  private XMLStreamWriter writer;

  /**
   *  Writes given gui into the given output stream in XML format.
   */
  public void write(Screens gui, OutputStream outputStream)
  throws XMLStreamException
  {
    try
    {
      // create XML strem writer
      writer = javax.xml.stream.XMLOutputFactory.newFactory()
	.createXMLStreamWriter(outputStream);

      // insert root element
      writer.writeStartDocument();
      writer.writeStartElement("gui", "gui", NS);
      writer.writeNamespace("gui", NS);
      writePreferences(gui);

      // write children
      for (int i=0; i<gui.getVisualObjectCount(); i++)
	writeChild((Screen)gui.getVisualObject(i));
      for (int i=0; i<gui.getChangerCount(); i++)
        writeChild(gui.getChanger(i));

      // finish the document
      writer.writeEndDocument();
      writer.close();

    }
    catch(XMLStreamException e)
    {
      System.out.println(e.getMessage());
    }
  }

  /**
   *
   */
  private void writeChild(Screen screen)
  throws XMLStreamException
  {
    writer.writeStartElement("gui", "screen", NS);
    writer.writeAttribute("class", screen.getClass().getName());
    writePreferences(screen);
    for (int i=0; i<screen.getVisualObjectCount(); i++)
      if (screen.getVisualObject(i).isVisualContainer())
	writeChild((VisualContainer)screen.getVisualObject(i));
      else
        writeChild(screen.getVisualObject(i));
    for (int i=0; i<screen.getChangerCount(); i++)
      writeChild(screen.getChanger(i));
    writer.writeEndElement();
  }

  /**
   *
   */
  private void writeChild(Changer changer)
  throws XMLStreamException
  {
    writer.writeStartElement("gui", "changer", NS);
    writer.writeAttribute("class", changer.getClass().getName());
    writePreferences(changer);
    writer.writeEndElement();
  }

  /**
   *
   */
  private void writeChild(VisualObject object)
  throws XMLStreamException
  {
    writer.writeStartElement("gui", "component", NS);
    writer.writeAttribute("class", object.getClass().getName());
    writePreferences(object);
    for (int i=0; i<object.getChangerCount(); i++)
      writeChild(object.getChanger(i));
    writer.writeEndElement();
  }

  /**
   *
   */
  private void writeChild(VisualContainer container)
  throws XMLStreamException
  {
    writer.writeStartElement("gui", "panel", NS);
    writer.writeAttribute("class", container.getClass().getName());
    writePreferences(container);
    for (int i=0; i<container.getVisualObjectCount(); i++)
      if (container.getVisualObject(i).isVisualContainer())
	writeChild((VisualContainer)container.getVisualObject(i));
      else
        writeChild(container.getVisualObject(i));
    for (int i=0; i<container.getChangerCount(); i++)
      writeChild(container.getChanger(i));
    writer.writeEndElement();
  }

  /**
   *
   */
  private void writePreferences(GuiObject object)
  throws XMLStreamException
  {
    // find all of the getters for a given object class
    Map<String, Item2> preferences = Scanner.scanClass(object.getClass());
    for (Item2 preference : preferences.values())
      try
      {
        // get key and value
        String key = preference.getKey();
        Object value = preference.getValue(object);
	// convert value into string
	String strValue = null;
	if (value instanceof control4j.gui.Color)
	  strValue = ((control4j.gui.Color)value).getKey();
	else if (value instanceof java.awt.Color)
	  strValue = Integer.toString(((Color)value).getRGB());
	else
	  strValue = value.toString();
        // write preference
        writePreference(key, strValue);
      }
      catch (java.lang.reflect.InvocationTargetException e)
      {
      }
      catch (java.lang.IllegalAccessException e)
      {
      }
  }

  /**
   *
   */
  private void writePreference(String key, String value) 
  throws XMLStreamException
  {
    writer.writeEmptyElement("gui", "preference", NS);
    writer.writeAttribute("key", key);
    writer.writeAttribute("value", value);
    //writer.writeEndElement();
  }
}
