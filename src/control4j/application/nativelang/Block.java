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

import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.collections4.CollectionUtils.unmodifiableCollection;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static control4j.tools.LogMessages.getMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.NoSuchElementException;

import control4j.application.Preprocessor;
import control4j.application.Scope;
import control4j.application.ErrorRecord;
import control4j.application.ErrorManager;
import control4j.tools.DuplicateElementException;
import control4j.tools.ParseException;

import cz.lidinsky.tools.IToStringBuildable;
import cz.lidinsky.tools.ToStringBuilder;

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
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = trim(notBlank(name, getMessage("msg004", "name",
        getDeclarationReferenceText())));
  }

  private int scope;

  public int getScope() {
    return scope;
  }

  public void setScope(final int scope) {
    this.scope = scope;
  }

  private ArrayList<String> input;

  public void addInput(String name) {
    name = trim(notBlank(name, getMessage("msg004", "input name",
        getDeclarationReferenceText())));
    if (input == null) {
      input = new ArrayList<String>();
    }
    input.add(name);
  }

  public Collection<String> getInput() {
    return unmodifiableCollection(
        emptyIfNull(input));
  }

  private ArrayList<String> output;

  public void addOutput(String name) {
    name = trim(notBlank(name, getMessage("msg004", "output name",
        getDeclarationReferenceText())));
    if (output == null) {
      output = new ArrayList<String>();
    }
    output.add(name);
  }

  public Collection<String> getOutput() {
    return unmodifiableCollection(
        emptyIfNull(output));
  }

  private ArrayList<Module> modules;

  public void add(Module module) {
    notNull(module, getMessage("msg006", "module",
        getDeclarationReferenceText()));
    if (modules == null) {
      modules = new ArrayList<Module>();
    }
    modules.add(module);
  }

  public Collection<Module> getModules() {
    return unmodifiableCollection(
        emptyIfNull(modules));
  }

  private ArrayList<Signal> signals;

  public void add(Signal signal) {
    notNull(signal, getMessage("msg006", "signal",
        getDeclarationReferenceText()));
    if (signals == null) {
      signals = new ArrayList<Signal>();
    }
    signals.add(signal);
  }

  public Collection<Signal> getSignals() {
    return unmodifiableCollection(
        emptyIfNull(signals));
  }

  private ArrayList<Use> uses;

  public void add(Use use) {
    notNull(use, getMessage("msg006", "use",
        getDeclarationReferenceText()));
    if (uses == null) {
      uses = new ArrayList<Use>();
    }
    uses.add(use);
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
