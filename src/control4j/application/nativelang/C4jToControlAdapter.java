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

import control4j.application.Scope;
import control4j.application.ErrorManager;
import control4j.tools.DuplicateElementException;

public class C4jToControlAdapter extends AbstractAdapter {

  protected control4j.application.Preprocessor handler;

  public C4jToControlAdapter(control4j.application.Preprocessor handler) {
    if (handler instanceof control4j.application.Preprocessor) {
      this.handler = (control4j.application.Preprocessor)handler;
    } else {
      throw new UnsupportedOperationException();
    }
  }

  public void startLevel() {
    handler.startScope();
  }

  public void endLevel() {
    handler.endScope();
  }

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
      handler.addModuleInput(
                  inp.getHref(),
                  resolveScope(inp.getScope(), localScope),
                  translated);
      try {
        if (!isBlank(inp.getIndex())) {
          int index = Integer.parseInt(inp.getIndex());
          destModule.putInput(index, translated);
        } else {
          destModule.putInput(translated);
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
      handler.addModuleOutput(
                  outp.getHref(),
                  resolveScope(outp.getScope(), localScope),
                  translated);
      try {
        if (!isBlank(outp.getIndex())) {
          int index = Integer.parseInt(outp.getIndex());
          destModule.putOutput(index, translated);
        } else {
          destModule.putOutput(translated);
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

  }

  public void put(Block block) {
      Scope localScope = handler.getScopePointer();
      control4j.application.Block translated = block.getExpandable();
      handler.putBlock(
          block.getName(),
          resolveScope(block.getScope(), localScope),
          translated);
  }

  public void put(Signal signal) {
      Scope localScope = handler.getScopePointer();
      control4j.application.Signal translated
          = new control4j.application.Signal();
      translateConfiguration(signal, translated, localScope);
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
          input.getIndex(), new control4j.application.Input());
    }
    for (Output output : use.getOutput()) {
      translated.putOutput(
          output.getIndex(), new control4j.application.Output());
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
