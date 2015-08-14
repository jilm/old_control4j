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

import control4j.application.Input;
import control4j.application.Module;
import control4j.application.Output;
import control4j.application.Preprocessor;
import control4j.application.Reference;
import control4j.application.Scope;
import control4j.application.Signal;
import control4j.ld.Coil;
import control4j.ld.Contact;
import control4j.ld.ContactBlock;
import control4j.ld.LadderDiagram;
import control4j.ld.ParallelContactBlock;
import control4j.ld.Rung;
import control4j.ld.SerialContactBlock;

/**
 *
 *
 */
public class Ld2ControlAdapter extends AbstractAdapter {

  protected static final String AND_MODULE_CLASS_NAME
                                    ="control4j.modules.system.PMLDAnd";
  protected static final String OR_MODULE_CLASS_NAME
                                     ="control4j.modules.system.PMLDOr";
  protected static final String ADAPTER_MODULE_CLASS_NAME
                               = "control4j.modules.system.PMLDAdapter";

  protected Preprocessor handler;

  public Ld2ControlAdapter(Preprocessor handler) {
    this.handler = handler;
  }

  protected int tempSignalCounter = 0;

  @Override
  public void startLd() {
    handler.startScope();
  }

  @Override
  public void endLd() {
    handler.endScope();
  }

  /**
   *  Translate the rung into the modules.
   */
  @Override
  public void put(Rung rung) {
    // translate contact blocks
    Reference inputRef = translateContactBlock(rung.getContactBlock());
    Module adapter = new Module(ADAPTER_MODULE_CLASS_NAME);
    Input input = new Input();
    adapter.putInput(0, input);
    handler.addModuleInput(inputRef.getHref(), inputRef.getScope(), input);
    // translate coils
    for (int i = 0; i < rung.coilBlockSize(); i++) {
      Coil coil = rung.getCoil(i);
      Output output = new Output();
      adapter.putOutput(output);
      handler.addModuleOutput(coil.getName(), Scope.getGlobal(), output);
      handler.putSignal(coil.getName(),
          Scope.getGlobal(),
          new Signal(coil.getName()));
    }
    handler.addModule(adapter);
  }

  protected Reference translateContactBlock(ContactBlock contactBlock) {
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

  protected Reference translateSerialContactBlock(
                                    SerialContactBlock contacts) {

    // Create AND module
    Module module = new Module(AND_MODULE_CLASS_NAME);
    // Resolve its inputs
    for (int i = 0; i < contacts.size(); i++) {
      // Translate inner contact block and get reference to result signal
      Reference ref = translateContactBlock(contacts.get(i));
      // Put this signal as input to the AND module
      Input input = new Input();
      module.putInput(input);
      handler.addModuleInput(ref.getHref(), ref.getScope(), input);
    }
    // Create the output of the module
    String outputSignalName = getTempSignalName();
    Output output = new Output();
    module.putOutput(0, output);
    handler.addModuleOutput(
        outputSignalName, handler.getScopePointer(), output);
    handler.addModule(module);
    handler.putSignal(
        outputSignalName,
        handler.getScopePointer(),
        new Signal(outputSignalName));
    // Return reference to the output
    return new Reference(outputSignalName, handler.getScopePointer());
  }

  protected Reference translateParallelContactBlock(
                                     ParallelContactBlock contacts) {

    // Create OR module
    Module module = new Module(OR_MODULE_CLASS_NAME);
    // Resolve its inputs
    for (int i = 0; i < contacts.size(); i++) {
      // Translate inner contact block and get reference to result signal
      Reference ref = translateContactBlock(contacts.get(i));
      // Put this signal as input to the OR module
      Input input = new Input();
      module.putInput(input);
      handler.addModuleInput(ref.getHref(), ref.getScope(), input);
    }
    // Create the output of the module
    String outputSignalName = getTempSignalName();
    Output output = new Output();
    module.putOutput(0, output);
    handler.addModuleOutput(
        outputSignalName, handler.getScopePointer(), output);
    handler.addModule(module);
    handler.putSignal(
        outputSignalName,
        handler.getScopePointer(),
        new Signal(outputSignalName));
    // Return reference to the output
    return new Reference(outputSignalName, handler.getScopePointer());
  }

  protected Reference translateContact(Contact contact) {
    return new Reference(contact.getName(), Scope.getGlobal());
  }

  protected String getTempSignalName() {
    return "temp" + tempSignalCounter++;
  }

}
