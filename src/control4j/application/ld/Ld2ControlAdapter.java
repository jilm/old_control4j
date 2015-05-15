package control4j.application.ld;

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

import control4j.ld.Rung;
import control4j.ld.Coil;
import control4j.ld.Contact;
import control4j.ld.ContactBlock;
import control4j.ld.ParallelContactBlock;
import control4j.ld.SerialContactBlock;
import control4j.ld.LadderDiagram;
import control4j.application.Module;
import control4j.application.Signal;
import control4j.application.Input;
import control4j.application.Output;
import control4j.application.Scope;

/**
 *
 *
 */
class Ld2ControlAdapter extends AbstractAdapter {

  protected static final String AND_MODULE_CLASS_NAME
                                    ="control4j.modules.system.PMLDAnd";
  protected static final String OR_MODULE_CLASS_NAME
                                     ="control4j.modules.system.PMLDOr";
  protected static final String ADAPTER_MODULE_CLASS_NAME
	                       = "control4j.modules.system.PMLDAdapter";

  protected control4j.application.Application handler;

  public Ld2ControlAdapter(control4j.application.Application handler) {
    this.handler = handler;
  }

  protected int tempSignalCounter = 0;

  public void startLd() {
    handler.startScope();
  }

  public void endLd() {
    handler.endScope();
  }

  /**
   *  Translate the rung into the modules.
   */
  public void put(Rung rung) {
    // translate contact blocks
    Output output = translateContactBlock(rung.getContactBlock());
    Module adapter = new Module(ADAPTER_MODULE_CLASS_NAME);
    adapter.putInput(0, new Input(output.getScope(), output.getHref()));
    // translate coils
    try {
      for (int i = 0; i < rung.coilBlockSize(); i++) {
        Coil coil = rung.getCoil(i);
        adapter.putOutput(new Output(Scope.getGlobal(), coil.getName()));
        handler.putSignal(
	    coil.getName(), Scope.getGlobal(), new Signal());
      }
      handler.addModule(adapter);
    } catch (control4j.tools.DuplicateElementException e) {
      // TODO:
    }
  }

  public void put(LadderDiagram ld) {}

  protected Output translateContactBlock(ContactBlock contactBlock) {
    if (contactBlock instanceof SerialContactBlock) {
      return translateSerialContactBlock((SerialContactBlock)contactBlock);
    } else if (contactBlock instanceof ParallelContactBlock) {
      return translateParallelContactBlock((ParallelContactBlock)contactBlock);
    } else if (contactBlock instanceof Contact) {
      return translateContact((Contact)contactBlock);
    } else {
      // TODO:
      throw new AssertionError();
    }
  }

  protected Output translateSerialContactBlock(SerialContactBlock contacts) {
    try {
      Module module = new Module(AND_MODULE_CLASS_NAME);
      for (int i = 0; i < contacts.size(); i++) {
        Output output = translateContactBlock(contacts.get(i));
        module.putInput(new Input(output.getScope(), output.getHref()));
      }
      String outputSignalName = getTempSignalName();
      Output output = new Output(handler.getScopePointer(), outputSignalName);
      module.putOutput(0, output);
      handler.addModule(module);
      handler.putSignal(
	  outputSignalName, handler.getScopePointer(), new Signal());
      return output;
    } catch (control4j.tools.DuplicateElementException e) {
      // should not happen
      throw new AssertionError();
    }
  }

  protected Output translateParallelContactBlock(
					     ParallelContactBlock contacts) {

    try {
      Module module = new Module(OR_MODULE_CLASS_NAME);
      for (int i = 0; i < contacts.size(); i++) {
        Output output = translateContactBlock(contacts.get(i));
        module.putInput(new Input(output.getScope(), output.getHref()));
      }
      String outputSignalName = getTempSignalName();
      Output output = new Output(handler.getScopePointer(), outputSignalName);
      module.putOutput(0, output);
      handler.addModule(module);
      handler.putSignal(
	  outputSignalName, handler.getScopePointer(), new Signal());
      return output;
    } catch (control4j.tools.DuplicateElementException e) {
      // should not happen
      throw new AssertionError();
    }
  }

  protected Output translateContact(Contact contact) {
    return new Output(Scope.getGlobal(), contact.getName());
  }

  protected String getTempSignalName() {
    return "temp" + tempSignalCounter++;
  }

}
