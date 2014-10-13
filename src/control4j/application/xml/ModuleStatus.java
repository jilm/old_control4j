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
import control4j.application.ModuleDeclaration;
import control4j.application.Property;
import control4j.application.Input;
import control4j.application.Output;
import control4j.application.Scope;
import static control4j.tools.LogMessages.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *  Inside the module declaration status. This status overtaks
 *  control when module element is detected.
 */
class ModuleStatus extends SaxStatusTemplate
{

  private RootStatus parentStatus;
  private ModuleDeclaration module;

  /**
   *  @param parentStatus
   *             the control will be returned to this object as soon
   *             as end element of module is detected
   *
   *  @param module
   *             an object which will be updated based on the content
   *             of the element module
   */
  ModuleStatus(RootStatus parentStatus, ModuleDeclaration module)
  {
    this.parentStatus = parentStatus;
    this.module = module;
  }

  /**
   *  Expected elements are: property, input and output.
   *
   *  @throws SyntaxErrorException
   *              if unexpected element appears
   */
  @Override
  public ISaxStatus startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
  {
    DeclarationReference reference = new DeclarationReference(fileReference);
    reference.setLineColumn(locator.getLineNumber(), locator.getColumnNumber());
    if (localName.equals("property") && uri.equals(Reader.namespace))
    {
      String key = attributes.getValue("key");
      if (key == null)
        throw new SyntaxErrorException(getMessage("appxmlh"));
      String value = attributes.getValue("value");
      if (value == null)
        throw new SyntaxErrorException(getMessage("appxmli"));
      Property property = new Property(key, value);
      property.setDeclarationReference(reference);
      module.setConfigItem(property);
      return new EndElementStatus(this);
    }
    else if ((localName.equals("input") || localName.equals("output"))
      && uri.equals(Reader.namespace))
    {
      try
      {
        // index of the input
        int index = -1;
        String strIndex = attributes.getValue("index");
        if (strIndex != null) index = Integer.parseInt(strIndex);
        // name of the signal
        String signal = attributes.getValue("signal");
	if (signal == null)
	  throw new SyntaxErrorException(getMessage("appxmlj"));
        // signal scope
	Scope signalScope;
        String scope = attributes.getValue("scope");
	if (scope == null || scope.equals("local"))
	  signalScope = parentStatus.getLocalScope();
        else if (scope.equals("file"))
	  signalScope = parentStatus.getFileScope();
        else if (scope.equals("global"))
	  signalScope = Scope.getGlobal();
        else
	  throw new SyntaxErrorException(getMessage("appxmle", scope));
        // input
	if (localName.equals("input") && uri.equals(Reader.namespace))
	{
	  Input input = new Input(signalScope, signal);
	  input.setDeclarationReference(reference);
	  input.setIndex(index);
	  module.addInput(input);
	  return new InputStatus(this, input);
	}
	else // output
	{
	  Output output = new Output(signalScope, signal);
	  output.setDeclarationReference(reference);
	  output.setIndex(index);
	  module.addOutput(output);
	  return new OutputStatus(this, output);
	}
      }
      catch (NumberFormatException e)
      {
        throw new SyntaxErrorException(getMessage("appxmlk"), e);
      }
    }
    else
    {
      throw new SyntaxErrorException(getMessage("appxmll"));
    }
  }

  @Override
  public ISaxStatus endElement(String uri, String localName, String qName) 
  throws SAXException
  {
    if (localName.equals("module") && uri.equals(Reader.namespace))
    {
      module.complete();
      return parentStatus;
    }
    else
    {
      throw new SyntaxErrorException(getMessage("appxmlm"));
    }
  }

}
