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

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import control4j.application.Scope;
import control4j.application.ErrorManager;
import control4j.application.ReferenceDecorator;
import control4j.tools.DuplicateElementException;

/**
 *  An adapter which can translate objects from nativelang package
 *  to the objects from the application package. Translated objects
 *  are sent into the given handler object.
 */
public class C4jToControlAdapter extends AbstractAdapter {

  /** A destination for translated objects. */
  protected control4j.application.Preprocessor handler;

  public C4jToControlAdapter(control4j.application.Preprocessor handler) {
    if (handler instanceof control4j.application.Preprocessor) {
      this.handler = (control4j.application.Preprocessor)handler;
    } else {
      throw new UnsupportedOperationException();
    }
  }

  /**
   *  Call to start new inner local scope.
   */
  public void startLevel() {
    handler.startScope();
  }

  /**
   *  Call to end current inner local scope and to return to the parent
   *  scope.
   */
  public void endLevel() {
    handler.endScope();
  }

  /**
   *  Put a module object.
   */
  public void put(Module module) {

    Scope localScope = handler.getScopePointer();
    control4j.application.Module destModule =
      new control4j.application.Module(module.getClassName());

    // translate configuration
    translateConfiguration(module, destModule, localScope);

    // translate resource definitions
    for (Resource resource : module.getResources()) {
      try {
        control4j.application.Resource destResource
          = new control4j.application.Resource();
        if (resource.isReference()) {
          handler.addResourceRef(
              resource.getHref(),
              resolveScope(resource.getScope(), localScope),
              destResource);
        } else {
          destResource.setClassName(resource.getClassName());
          translateConfiguration(resource, destResource, localScope);
        }
        if (destModule.getResourceKeys().contains(resource.getKey())) {
          // TODO: duplicate resource keys error!
        } else {
          destModule.putResource(resource.getKey(), destResource);
        }
      } catch (IllegalStateException e) {
        // TODO: refrence x definition error
      }
    }

    // translate input
    for (Input inp : module.getInput()) {
      control4j.application.Input translated
        = new control4j.application.Input();
      translateConfiguration(inp, translated, localScope);
      control4j.application.Input sub
        = substitute(inp.getHref(), inp.getScope(), translated);
      try {
        if (!isBlank(inp.getIndex())) {
          int index = Integer.parseInt(inp.getIndex());
          destModule.putInput(index, sub);
        } else {
          destModule.putInput(sub);
        }
      } catch (NumberFormatException e) {
        ErrorManager.newError();
        // TODO:
      }
    }

    // translate output
    for (Output outp : module.getOutput()) {
      control4j.application.Output translated
        = new control4j.application.Output();
      translateConfiguration(outp, translated, localScope);
      control4j.application.Output sub
        = substitute(outp.getHref(), outp.getScope(), translated);
      try {
        if (!isBlank(outp.getIndex())) {
          int index = Integer.parseInt(outp.getIndex());
          destModule.putOutput(index, sub);
        } else {
          destModule.putOutput(sub);
        }
      } catch (NumberFormatException e) {
        ErrorManager.newError();
        // TODO:
      }
    }

    // translate tagged input
    for (String tag : module.getInputTags()) {
      handler.addInputTag(destModule, tag);
    }

    // translate tagged output
    for (String tag : module.getOutputTags()) {
      handler.addOutputTag(destModule, tag);
    }

    // send translated module
    handler.addModule(destModule);

  }

  /**
   *  Put a block object.
   */
  public void put(Block block) {
    Scope localScope = handler.getScopePointer();
    control4j.application.Block translated = block.getExpandable();
    handler.putBlock(
        block.getName(),
        resolveScope(block.getScope(), localScope),
        translated);
  }

  /** If the block is expanded, this fields contains references to
      relevant objects, otherwise, these contains null. */
  private control4j.application.Use use;
  private Block block;

  /**
   *  Use this method to put a module which is part of a block.
   */
  void put(
      Module module,
      control4j.application.Use use,
      Block block) {

    this.use = use;
    this.block = block;
    put(module);
  }

  /**
   *  If the module is inside the block, this method provide substitution
   *  of given input if necessary.
   */
  protected control4j.application.Input substitute(
      String href, int scopeCode, control4j.application.Input input) {

    // If the block is expanded
    if (block != null && use != null) {
      // If the input is a reference to the block input
      if (scopeCode == Parser.LOCAL_SCOPE_CODE
              && block.getInput().contains(href)) {
        // Provide a substitution,
        // taka appropriate reference from the use object
        ReferenceDecorator<control4j.application.Input, Object> useInput
          = use.getInput(href);
        handler.addModuleInput(
            useInput.getHref(), useInput.getScope(), useInput.getDecorated());
        return useInput.getDecorated();
      }
    }
    // Otherwise, just return given data.
    handler.addModuleInput(
            href, resolveScope(scopeCode, handler.getScopePointer()), input);
    return input;
  }

  /**
   *  If the module is inside the block, this method provide substitution
   *  of given output if necessary.
   */
  protected control4j.application.Output substitute(
      String href, int scopeCode, control4j.application.Output output) {

    // If the block is expanded
    if (block != null && use != null) {
      // If the output is a reference to the block output
      if (scopeCode == Parser.LOCAL_SCOPE_CODE
              && block.getOutput().contains(href)) {
        // Provide a substitution,
        // take appropriate reference from the use object
        ReferenceDecorator<control4j.application.Output, Object> useOutput
          = use.getOutput(href);
        handler.addModuleOutput(
          useOutput.getHref(), useOutput.getScope(), useOutput.getDecorated());
        return useOutput.getDecorated();
      }
    }
    // Otherwise, just return given data.
    handler.addModuleOutput(
            href, resolveScope(scopeCode, handler.getScopePointer()), output);
    return output;
  }

  public void put(Signal signal) {
    Scope localScope = handler.getScopePointer();
    control4j.application.Signal translated
      = new control4j.application.Signal();
    translateConfiguration(signal, translated, localScope);
    // translate tags
    for (Tag tag : signal.getTags()) {
      control4j.application.Tag translatedTag = new control4j.application.Tag();
      translateConfiguration(tag, translatedTag, localScope);
      translated.putTag(tag.getName(), translatedTag);
    }
    // TODO: other signal properties!
    handler.putSignal(
        signal.getName(),
        resolveScope(signal.getScope(), localScope),
        translated);
  }

  public void put(ResourceDef resource) {
      Scope localScope = handler.getScopePointer();
      control4j.application.Resource translated =
          new control4j.application.Resource()
          .setClassName(resource.getClassName());
      translateConfiguration(resource, translated, localScope);
      handler.putResource(
          resource.getName(),
          resolveScope(resource.getScope(), localScope),
          translated);
  }

  public void put(Define define) {
      Scope localScope = handler.getScopePointer();
      Scope scope =
          define.getScope() == 0 ? Scope.getGlobal() : localScope;
      handler.putDefinition(
          define.getName(),
          resolveScope(define.getScope(), localScope),
          define.getValue());
  }

  public void put(Property property) {
    try {
      Scope localScope = handler.getScopePointer();
      if (property.isReference()) {
        control4j.application.Property destProperty
            = handler.putProperty(property.getKey(), (String)null);
        handler.addPropertyReference(
            property.getHref(),
            resolveScope(property.getScope(), localScope),
            destProperty);
      } else {
        handler.putProperty(property.getKey(), property.getValue());
      }
    } catch (IllegalStateException e) {
      // TODO: property value x href error
    } catch (DuplicateElementException e) {
      // TODO: duplicate configuration item error!
    }
  }

  public void put(Use use) {
    Scope localScope = handler.getScopePointer();
    control4j.application.Use translated
        = new control4j.application.Use(
            use.getHref(),
            resolveScope(use.getScope(), localScope));
    translateConfiguration(use, translated, localScope);
    for (Input input : use.getInput()) {
      translated.putInput(
          input.getIndex(),
          new ReferenceDecorator<control4j.application.Input, Object>(
              input.getHref(),
              resolveScope(input.getScope(), localScope),
              null,
              new control4j.application.Input()));
    }
    for (Output output : use.getOutput()) {
      translated.putOutput(
          output.getIndex(),
          new ReferenceDecorator<control4j.application.Output, Object>(
              output.getHref(),
              resolveScope(output.getScope(), localScope),
              null,
              new control4j.application.Output()));
    }
    handler.add(translated, localScope);
  }

  protected Scope resolveScope(int scopeCode, Scope localScope) {
    if (scopeCode == 0)
      return Scope.getGlobal();
    else if (scopeCode == 1)
      return localScope;
    else if (scopeCode == 2)
      return localScope.getParent();
    else
      throw new IllegalArgumentException();
  }

  protected void translateConfiguration(
      Configurable source,
      control4j.application.Configurable destination,
      Scope localScope) {

    for (Property srcProperty : source.getConfiguration()) {
      try {
        if (destination.containsKey(srcProperty.getKey())) {
          // TODO: duplicate configuration item error!
        } else if (srcProperty.isReference()) {
          control4j.application.Property destProperty
              = destination.putProperty(srcProperty.getKey(), null);
          handler.addPropertyReference(srcProperty.getHref(),
              resolveScope(srcProperty.getScope(), localScope), destProperty);
        } else {
          destination.putProperty(srcProperty.getKey(), srcProperty.getValue());
        }
      } catch (IllegalStateException e) {
        // TODO: property value x href error
      }
    }
  }

}
