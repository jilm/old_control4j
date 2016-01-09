/*
 *  Copyright 2015, 2016 Jiri Lidinsky
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

package control4j.application.nativelang;

import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.collections4.CollectionUtils.unmodifiableCollection;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static control4j.tools.LogMessages.getMessage;

import java.util.ArrayList;
import java.util.Collection;

import control4j.application.Preprocessor;
import cz.lidinsky.tools.ToStringBuilder;
import java.util.List;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 *
 *  Stands for a block element. The block is a definition object. It contains
 *  modules, signal definitions, references to other blocks, input and output
 *  objects. Each block is identified by a name. Each block could be referenced
 *  by a use object.
 *
 */
public class Block extends DescriptionBase implements IDefinition {

  /** An empty constructor. */
  public Block() {}

  /** Identification of the block. */
  private String name;

  /** Returns a name, which is identification of the block. */
  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  private int scope;

  @Override
  public int getScope() {
    return scope;
  }

  @Override
  public void setScope(final int scope) {
    this.scope = scope;
  }

  private List<String> input;

  public void addInput(String name) {
      if (!isBlank(name)) {
          if (input == null) {
              input = new ArrayList<>();
          }
          input.add(trim(name));
      }
  }

  public Collection<String> getInput() {
    return unmodifiableCollection(
        emptyIfNull(input));
  }

  private List<String> output;

  public void addOutput(String name) {
      if (!isBlank(name)) {
          if (output == null) {
              output = new ArrayList<>();
          }
          output.add(trim(name));
      }
  }

  public Collection<String> getOutput() {
    return unmodifiableCollection(
        emptyIfNull(output));
  }

  private ArrayList<Module> modules;

  public void add(Module module) {
      if (module != null) {
          if (modules == null) {
              modules = new ArrayList<>();
          }
          modules.add(module);
      }
  }

  public Collection<Module> getModules() {
    return unmodifiableCollection(
        emptyIfNull(modules));
  }

  private List<Signal> signals;

  public void add(Signal signal) {
      if (signal != null) {
          if (signals == null) {
              signals = new ArrayList<>();
          }
          signals.add(signal);
      }
  }

  public Collection<Signal> getSignals() {
    return unmodifiableCollection(
        emptyIfNull(signals));
  }

  private List<Use> uses;

  public void add(Use use) {
      if (use != null) {
          if (uses == null) {
              uses = new ArrayList<>();
          }
          uses.add(use);
      }
  }

  public Collection<Use> getUses() {
    return unmodifiableCollection(
        emptyIfNull(uses));
  }

  protected void check() {
    if (name == null) {
      throw new IllegalStateException(getMessage("msg002", "name",
          getDeclarationReferenceText()));
    }
  }

  /**
   *  A class that contain code to resolve the scope and expand inner
   *  elements of the block into the handler.
   */
  private class ExpandableBlock extends control4j.application.Block {

    @Override
    public void expand(
        control4j.application.Use use,
        Preprocessor handler) {

      C4jToControlAdapter translator = new C4jToControlAdapter(handler);

      // Expand all of the signal definitions
      for (Signal signal : getSignals()) {
        translator.put(signal);
      }

      // Expand all of the modules
      for (Module module : getModules()) {
        translator.put(module, use, Block.this);
      }

      // Expand all of the nested use objects
      for (Use nestedUse : getUses()) {
        translator.put(nestedUse);
      }

    }

  }

  /**
   *
   */
  public control4j.application.Block getExpandable() {
    return new ExpandableBlock();
  }

  @Override
  public void toString(ToStringBuilder builder) {
    super.toString(builder);
    builder.append("name", name)
        .append("scope", scope)
        .append("input", input)
        .append("output", output)
        .append("modules", modules)
        .append("signals", signals)
        .append("uses", uses);
  }

}
