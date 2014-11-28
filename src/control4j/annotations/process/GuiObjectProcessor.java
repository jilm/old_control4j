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
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.annotation.processing.Messager;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.tools.Diagnostic;

import java.io.File;
import java.io.FileWriter;

@SupportedAnnotationTypes ( { "control4j.annotations.AGuiObject" } )
public class GuiObjectProcessor extends AbstractProcessor
{

  private FileWriter fileWriter;

  public GuiObjectProcessor()
  {
    try
    {
    File file = new File("/home/lidinj1/", "pokus");
    file.createNewFile();
    fileWriter = new FileWriter(file);
    }
    catch (Exception e)
    {
    }
  }
  
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env)
  {
    Messager messager = processingEnv.getMessager();
    try
    {
    for (TypeElement te : annotations)
      for (Element e : env.getElementsAnnotatedWith(te))
      {
	String className = e.toString();
        fileWriter.write(className, 0, className.length());
      }
      fileWriter.close();
    }
    catch (Exception e) 
    {
      messager.printMessage(Diagnostic.Kind.NOTE, e.getMessage());
    }
    return true;
  }

  @Override
  public SourceVersion getSupportedSourceVersion()
  {
    return SourceVersion.latestSupported();
  }

}
