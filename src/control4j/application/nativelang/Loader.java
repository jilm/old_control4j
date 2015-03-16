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

  private XmlReader reader;

  public Loader()
  {
  }

  public Application get() throws IOException
  {
    return null;
  }

  /*
   *
   *   IXmlHandler implementation
   *
   */

  public void startProcessing(XmlReader reader)
  {
    this.reader = reader;
  }

  public void endProcessing()
  {
  }

  /*
   *
   *  Root elements
   *
   */

  @XmlStartElement(localName="application", 
      namespace="http://control4j.lidinsky.cz/application", 
      parent="", parentNamespace="*")
  private void startApplication(Attributes attributes)
  {
    application = new Application();
    workingApplication = application;
  }

  /*
   *
   *  Application descendants
   *
   */

  protected Application workingApplication;

  @XmlStartElement(localName="property", parent="application")
  private void startApplicationProperty(Attributes attributes)
  {
    // create new property object
    String key = attributes.getValue("key");
    String value = attributes.getValue("value");
    Property property = new Property(value);
    // set declaration reference informations
    DeclarationReference reference = new DeclarationReference(
	MessageFormat.format("Application Property; key: {0}; value: {1}",
	key, value));
    appendLocation(reference);
    property.setDeclarationReference(reference);
    // stores it
    workingApplication.addProperty(key, property);
    System.out.println(property.getDeclarationReferenceText());
  }

  @XmlStartElement(localName="define", parent="application")
  private void startApplicationDefine(Attributes attributes)
  {
    // create new define object
    String name = attributes.getValue("name");
    String value = attributes.getValue("value");
    String scope = attributes.getValue("scope");
    Define define = new Define(name, value, scope);
    // set declaration reference informations
    // stores it
    workingApplication.addDefine(define);
    System.out.println(define.getDeclarationReferenceText());
  }

  /*
   *
   *  Auxiliary methods.
   *
   */

  protected void appendLocation(DeclarationReference reference)
  {
    //reference.addLineColumn(line, column);
  }

}
