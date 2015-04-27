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

import java.text.MessageFormat;
import java.io.IOException;
import org.xml.sax.Attributes;

import control4j.application.ILoader;
import control4j.tools.DeclarationReference;
import control4j.tools.IXmlHandler;
import control4j.tools.XmlReader;
import control4j.tools.XmlStartElement;
import control4j.tools.XmlEndElement;

/**
 *
 *  Loads an application description in a native c4j language.
 *
 */
public class Loader implements ILoader, IXmlHandler
{

  private Application application;

  protected IAdapter adapter;

  public Loader()
  {
  }

  public Application get() throws IOException
  {
    return application;
  }

  public void setDestination(Object destination)
  {
  }

  /*
   *
   *   IXmlHandler implementation
   *
   */

  private XmlReader reader;

  public void startProcessing(XmlReader reader)
  {
    this.reader = reader;
  }

  public void endProcessing()
  {
    this.reader = null;
  }

  @XmlStartElement(localName="application", 
      namespace="http://control4j.lidinsky.cz/application", 
      parent="", parentNamespace="*")
  private void startApplication(Attributes attributes)
  {
    application = new Application(adapter);
    reader.addHandler(application);
  }

}
