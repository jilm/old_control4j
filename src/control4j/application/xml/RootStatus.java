package control4j.application.xml;

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

import control4j.tools.SaxStatusTemplate;
import control4j.tools.ISaxStatus;
import control4j.tools.DeclarationReference;
import control4j.application.Scope;
import control4j.application.Application;
import control4j.application.SignalDeclaration;
import control4j.application.ModuleDeclaration;
import control4j.application.ResourceDeclaration;
import static control4j.tools.LogMessages.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *  Status inside the root c4j-application element. It waits for module,
 *  resource, signal, scope or end-scope elements.
 */
class RootStatus extends SaxStatusTemplate
{

  private ISaxStatus parentStatus;
  private Scope fileScope = new Scope(Scope.getGlobal());
  private Scope localScope = fileScope;
  private Application application;

  /**
   *  @param application
   *             an object where the loaded application will be stored
   */
  RootStatus(ISaxStatus parent, Application application)
  {
    this.application = application;
    this.parentStatus = parent;
  }

  /**
   *  Expects module, resource, signal or scope elements.
   *
   *  @throws SyntaxErrorException
   *              if there is unexpected element
   */
  @Override
  public ISaxStatus startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
  {
    DeclarationReference reference = new DeclarationReference(fileReference);
    reference.setLineColumn(locator.getLineNumber(), locator.getColumnNumber());
    if (localName.equals("module") && uri.equals(Reader.namespace))
    {
      String className = attributes.getValue("class");
      if (className == null)
        throw new SyntaxErrorException(getMessage("appxmla"));
      ModuleDeclaration module = new ModuleDeclaration(className);
      module.setDeclarationReference(reference);
      application.addModule(module);
      return new ModuleStatus(this, module);
    }
    else if (localName.equals("resource") && uri.equals(Reader.namespace))
    {
      String className = attributes.getValue("class");
      if (className == null)
        throw new SyntaxErrorException(getMessage("appxmlb"));
      String name = attributes.getValue("name");
      if (name == null)
        throw new SyntaxErrorException(getMessage("appxmlc"));
      ResourceDeclaration resource = new ResourceDeclaration(className, name);
      resource.setDeclarationReference(reference);
      application.addResource(resource);
      return new ResourceStatus(this, resource);
    }
    else if (localName.equals("signal") && uri.equals(Reader.namespace))
    {
      String name = attributes.getValue("name");
      if (name == null)
        throw new SyntaxErrorException(getMessage("appxmld"));
      String unit = attributes.getValue("unit");
      String scope = attributes.getValue("scope");
      Scope signalScope;
      if (scope == null || scope.equals("local"))
        signalScope = localScope;
      else if (scope.equals("file"))
        signalScope = fileScope;
      else if (scope.equals("global"))
        signalScope = Scope.getGlobal();
      else
        throw new SyntaxErrorException(getMessage("appxmle", scope));
      SignalDeclaration signal = new SignalDeclaration(signalScope, name);
      signal.setDeclarationReference(reference);
      if (unit != null) signal.setUnit(unit);
      application.addSignal(signal);
      return new EndElementStatus(this);
    }
    else if (localName.equals("scope") && uri.equals(Reader.namespace))
    {
      localScope = new Scope(localScope);
      return this;
    }
    else
    {
      throw new SyntaxErrorException(getMessage("appxmlf"));
    }
  }

  /**
   *  Expects the end of scope element or the end of the root element.
   *
   *  @throws SyntaxErrorException
   *              if unexpected end of element appears here
   */
  @Override
  public ISaxStatus endElement(String uri, String localName, String qName) 
  throws SAXException
  {
    if (localName.equals("scope") && uri.equals(Reader.namespace))
    {
      localScope = localScope.getParent();
      return this;
    }
    else if (localName.equals("application") && uri.equals(Reader.namespace))
    {
      return parentStatus;
    }
    else
    {
      throw new SyntaxErrorException(getMessage("appxmlg"));
    }
  }

  Scope getLocalScope()
  {
    return localScope;
  }

  Scope getFileScope()
  {
    return fileScope;
  }

}
