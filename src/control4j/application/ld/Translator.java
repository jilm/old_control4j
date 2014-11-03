package control4j.application.ld;

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

import java.util.HashMap;
import control4j.application.Application;
import control4j.application.ModuleDeclaration;
import control4j.application.Input;
import control4j.application.Output;
import control4j.application.Scope;
import control4j.ld.LadderDiagram;
import control4j.ld.ContactBlock;
import control4j.ld.SerialContactBlock;
import control4j.ld.ParallelContactBlock;
import control4j.ld.Contact;
import control4j.ld.Rung;

/**
 *  Gets a ladder diagram and translates it to the control4j application.
 */
public class Translator
{
  private final Scope tempScope = new Scope();
  private int tempCounter = 0;
  private HashMap<String, String> invertedSignals = new HashMap<String, String>();
  private String declarationReference;
  private static final String AND_CLASS_NAME = "control4j.modules.bool.PMAnd";
  private static final String OR_CLASS_NAME = "control4j.modules.bool.PMOr";
  private static final String NOT_CLASS_NAME = "control4j.modules.bool.PMNot";

  /**
   *  Translates a ladder diagram into the control4j application.
   *
   *  @param ladderDiagram
   *             a diagram to translate
   *  @param application
   *             an output object. Created modules will be stored in
   *             this object
   */
  public void translate(LadderDiagram ladderDiagram, Application application)
  {
    for (int i=0; i<ladderDiagram.size(); i++)
    {
      declarationReference = "Rung: " + i;
      translateRung(ladderDiagram.get(i), application);
    }
  }

  /**
   *  Translates one rung into the form of modules.
   *
   */
  private void translateRung(Rung rung, Application application)
  {
    // translate contact block
    ContactBlock contactBlock = rung.getContactBlock();
    ModuleDeclaration outputModule = translateContactBlock(contactBlock, application);
    // translate output coils

  }

  /**
   *  Translates one contact block into the form of logical modules.
   *  
   *  @param block
   *             a contact block which will be translated
   *
   *  @param application
   *             an output object. Created modules will be placed here
   *
   *  @return the module which output is the final output of the whole
   *             contact block. Output of returned modul is not set and
   *             this must be done by the called method
   */
  private ModuleDeclaration translateContactBlock(ContactBlock block, Application application)
  {
    if (block instanceof SerialContactBlock)
    {
      SerialContactBlock contactBlock = (SerialContactBlock)block;
      ModuleDeclaration module = new ModuleDeclaration(AND_CLASS_NAME);
      for (int i=0; i<contactBlock.size(); i++)
      {
        ContactBlock child = contactBlock.get(i);
	if (child instanceof Contact)
	{
	  Input input = getInput((Contact)child, application);
	  input.setIndex(i);
	  input.setDeclarationReference(declarationReference);
	  module.addInput(input);
	}
	else
	{
	  ModuleDeclaration childModule = translateContactBlock(child, application);
	  String connectSignalName = createTempSignalName();
	  Input input = new Input(tempScope, connectSignalName);
	  input.setIndex(i);
	  input.setDeclarationReference(declarationReference);
	  module.addInput(input);
	  Output output = new Output(tempScope, connectSignalName);
	  output.setIndex(0);
	  output.setDeclarationReference(declarationReference);
	  childModule.addOutput(output);
	}
      }
      application.addModule(module);
      return module;
    }
    else if (block instanceof ParallelContactBlock)
    {
      ParallelContactBlock contactBlock = (ParallelContactBlock)block;
      ModuleDeclaration module = new ModuleDeclaration(OR_CLASS_NAME);
      for (int i=0; i<contactBlock.size(); i++)
      {
        ContactBlock child = contactBlock.get(i);
	if (child instanceof Contact)
	{
	  Input input = getInput((Contact)child, application);
	  input.setIndex(i);
	  input.setDeclarationReference(declarationReference);
	  module.addInput(input);
	}
	else
	{
	  ModuleDeclaration childModule = translateContactBlock(child, application);
	  String connectSignalName = createTempSignalName();
	  Input input = new Input(tempScope, connectSignalName);
	  input.setIndex(i);
	  input.setDeclarationReference(declarationReference);
	  module.addInput(input);
	  Output output = new Output(tempScope, connectSignalName);
	  output.setIndex(0);
	  output.setDeclarationReference(declarationReference);
	  childModule.addOutput(output);
	}
      }
      application.addModule(module);
      return module;
    }
    else
    {
      ModuleDeclaration module = new ModuleDeclaration(OR_CLASS_NAME);
      Input input = getInput((Contact)block, application);
      input.setIndex(0);
      input.setDeclarationReference(declarationReference);
      module.addInput(input);
      return module;
    }
  }

  /**
   *  Translates single contact into the input object for use in
   *  module declaration object. 
   */
  private Input getInput(Contact contact, Application application)
  {
    String name = contact.getName();
    String type = contact.getType();
    // normal contact
    if (type.equals("XIC"))
    {
      Input input = new Input(Scope.getGlobal(), name);
      return input;
    }
    // inverted contact
    else if (type.equals("XIO"))
    {
      // find signal in the buffer of inverted signals
      String invertedSignal = invertedSignals.get(name);
      if (invertedSignal == null)
      {
        invertedSignal = createTempSignalName();
	ModuleDeclaration module = new ModuleDeclaration(NOT_CLASS_NAME);
	Input input = new Input(Scope.getGlobal(), name);
	input.setIndex(0);
	input.setDeclarationReference(declarationReference);
	module.addInput(input);
	Output output = new Output(tempScope, invertedSignal);
	output.setIndex(0);
	output.setDeclarationReference(declarationReference);
	module.addOutput(output);
	application.addModule(module);
	invertedSignals.put(name, invertedSignal);
        input = new Input(tempScope, invertedSignal);
	return input;
      }
      else
      {
        Input input = new Input(tempScope, invertedSignal);
        return input;
      }
    }
    // not supported contact type
    else
    {
      throw new TranslationException("Unsupported contact type");
    }
  }

  private String createTempSignalName()
  {
    String name = "temp" + tempCounter;
    tempCounter ++;
    return name;
  }
}
