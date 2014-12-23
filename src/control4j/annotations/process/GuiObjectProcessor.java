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
import java.io.FileWriter;
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

  /** writer */
  private FileWriter writer;

  /** messager */
  private Messager messager;

  private static final String filename = "guicomponents.csv";

  private static final char delimiter = ',';

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
      writer = new FileWriter(filename);
    }
    catch (java.io.IOException e)
    {
      messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
    }
  }

  /**
   *  Write given informations into the file.
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
	  // write element to the file
          writer.write(name);
	  writer.write(delimiter);
	  writer.write(className);
	  for (String tag : tags)
	  {
            writer.write(delimiter);
	    writer.write(tag);
          }
	  writer.write("\n");
        }
      if (env.processingOver())
      {
        // close file
        writer.close();
      }
    }
    catch (java.io.IOException e)
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
