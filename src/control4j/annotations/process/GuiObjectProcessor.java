package control4j.annotations.process;

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

import java.util.Set;
import java.io.FileOutputStream;
import java.io.OutputStream;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.annotation.processing.Messager;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import control4j.annotations.AGuiObject;

/**
 *
 *  Collect information about classes annotated with AGuiObject annotation
 *  and write these information into the file with name guicomponents.xml.
 *  These informations are collected during the compile time. Created file
 *  is used by the gui editor factory class.
 *
 */
@SupportedAnnotationTypes ( { "control4j.annotations.AGuiObject" } )
public class GuiObjectProcessor extends AbstractProcessor
{

  /** xml writer */
  private XMLStreamWriter writer;

  /** messager */
  private Messager messager;

  /**
   *  Open a new xml file and write the root element to it.
   */
  @Override
  public void init(ProcessingEnvironment processingEnv)
  {
    super.init(processingEnv);
    messager = processingEnv.getMessager();
    try
    {
      // create xml file
      OutputStream os = new FileOutputStream("guicomponents.xml");
      writer = XMLOutputFactory.newFactory().createXMLStreamWriter(os);
      // write root element
      writer.writeStartDocument();
      writer.writeStartElement("gui-components");
    }
    catch (javax.xml.stream.XMLStreamException e)
    {
      messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
    }
    catch (java.io.IOException e)
    {
      messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
    }
  }

  /**
   *  Write given informations into the xml file.
   */
  public boolean process (Set<? extends TypeElement> annotations, 
    RoundEnvironment env)
  {
    try
    {
      // iterate over all of the elements
      for (TypeElement annotation : annotations)
        for (Element element : env.getElementsAnnotatedWith(annotation))
        {
	  String className = element.toString();
	  AGuiObject guiAnnotation = element.getAnnotation(AGuiObject.class);
	  String name = guiAnnotation.name();
	  String[] tags = guiAnnotation.tags();
	  // write element to the xml file
	  writer.writeStartElement("component");
	  writer.writeAttribute("class", className);
	  writer.writeAttribute("name", name);
	  for (String tag : tags)
	  {
	    writer.writeStartElement("tag");
	    writer.writeCharacters(tag);
	    writer.writeEndElement();
	  }
	  writer.writeEndElement();
        }
      if (env.processingOver())
      {
        // close XML file
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.close();
      }
    }
    catch (javax.xml.stream.XMLStreamException e)
    {
      messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
    }
    return true;
  }

  /**
   *
   */
  @Override
  public SourceVersion getSupportedSourceVersion()
  {
    return SourceVersion.latestSupported();
  }

}
