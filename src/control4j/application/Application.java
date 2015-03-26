package control4j.application;

/*
 *  Copyright 2013, 2015 Jiri Lidinsky
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
import java.util.LinkedList;
import java.util.Collection;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import control4j.ErrorManager;
//import control4j.application.gui.ScreenDeclaration;
import control4j.gui.Screens;
import control4j.tools.DuplicateElementException;

/**
 *
 *  A crate object that contains the application definition.
 *
 */
public class Application extends Configurable
{

  /** Holds actual scope during the process of translation. */
  private Scope scopePointer = Scope.getGlobal();

  public Scope getScopePointer()
  {
    return scopePointer;
  }

  public void startScope()
  {
    scopePointer = new Scope(scopePointer);
  }

  public void endScope()
  {
    scopePointer = scopePointer.getParent();
  }

  /*
   *
   *     Definitions.
   *
   *     Definitions are just named values that may be
   *     referenced by the property objects.
   *
   */

  /** Define objects. */
  private ScopeMap<String> definitions;

  /**
   *  Adds a definition into the internal buffer.
   *
   *  @throws DuplicateElementException
   *             if there already is a definition under the same
   *             name and scope inside the internal buffer.
   */
  public void putDefinition(String name, Scope scope, String value)
  throws DuplicateElementException
  {
    if (definitions == null) 
      definitions = new ScopeMap<String>();
    definitions.put(name, scope, value);
  }

  /**
   *  Returns a value of definition with given name and scope.
   *
   *  @throws NoSuchElementException
   *             if there is no a value associated with given
   *             name and scope
   */
  public String getDefinition(String name, Scope scope)
  {
    if (definitions == null)
      throw new NoSuchElementException();
    return definitions.get(name, scope);
  }

  /*
   *
   *     Resource Definitions
   *
   */

  /** Resource definitions. */
  private ScopeMap<Resource> resources;

  public void putResource(String name, Scope scope, Resource resource)
  throws DuplicateElementException
  {
    if (resources == null)
      resources = new ScopeMap<Resource>();
    resources.put(name, scope, resource);
  }

  /*
   *
   *     Block Definitions
   *
   */

  private ScopeMap<Block> blocks;

  public void putBlock(String name, Scope scope, Block block)
  throws DuplicateElementException
  {
    if (blocks == null)
      blocks = new ScopeMap<Block>();
    blocks.put(name, scope, block);
  }

  /*
   *
   *     Signal Definitions.
   *
   */

  private ScopeMap<Signal> signals;

  private ArrayList<Signal> signalIndexes;

  public void putSignal(String name, Scope scope, Signal signal)
  throws DuplicateElementException
  {
    if (signals == null)
    {
      signals = new ScopeMap<Signal>();
      signalIndexes = new ArrayList<Signal>();
    }
    signals.put(name, scope, signal);
    signalIndexes.add(signal);
  }

  /**
   *  Returns the index of the given signal.
   */
  public int getSignalIndex(Signal signal)
  {
    if (signalIndexes == null) {} // TODO
    int index = signalIndexes.indexOf(signal);
    return index;
  }

  /**
   *  Returns the signal with the given name that was defined
   *  under the given scope or some of the parent scope.
   */
  public Signal getSignal(String name, Scope scope)
  {
    if (signals == null) {} // TODO
    return signals.get(name, scope);
  }

  /**
   *  Returns the signal with the given index.
   */
  public Signal getSignal(int index)
  {
    if (signalIndexes == null) {} // TODO
    return signalIndexes.get(index);
  }

  /*
   *
   *     Module Definitions.
   *
   */

  /** An array of module definitions. */
  private ArrayList<Module> modules;

  /**
   *  Adds a module definition into the internal buffer.
   */
  public void addModule(Module module)
  {
    if (modules == null) modules = new ArrayList<Module>();
    modules.add(module);
  }

  public Module getModule(int index)
  {
    if (modules == null) {} // TODO
    return modules.get(index);
  }

  public int getModulesSize()
  {
    if (modules == null) return 0;
    return modules.size();
  }

  private ArrayList<Use> uses;

  public void addUse(Use use)
  {
    if (uses == null) uses = new ArrayList<Use>();
    uses.add(use);
  }

  /*
   *
   *
   *
   */

  protected Scope resolveScope(int scopeCode, Scope localScope)
  {
    if (scopeCode == 0)
      return Scope.getGlobal();
    else if (scopeCode == 1)
      return localScope;
    else if (scopeCode == 2)
      return localScope.getParent();
    else
      throw new IllegalArgumentException();
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    toString("", sb);
    return sb.toString();
  }

  void toString(String indent, StringBuilder sb)
  {

    sb.append("Definitions:\n");
    if (definitions != null)
      sb.append(definitions.toString());
    else
      sb.append("No definition");

    sb.append("Configuration:\n");
    super.toString(indent, sb);

    sb.append("Resource Definitions:\n");
    if (resources != null)
      sb.append(resources.toString());
    else
      sb.append("No Resource Definition");

    if (modules != null)
      for (Module module : modules)
	module.toString("  ", sb);

    // print signal definitions
    if (signals != null)
    {
      sb.append(indent)
        .append("Signal Definitions:\n");
      sb.append(signals.toString());
    }
  }

}
