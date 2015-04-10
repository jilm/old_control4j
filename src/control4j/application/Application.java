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

import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.notBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.lang3.tuple.ImmutableTriple;

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
  private ScopeMap<ValueObject> definitions;

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
      definitions = new ScopeMap<ValueObject>();
    definitions.put(name, scope, new ValueObject(value));
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
    return definitions.get(name, scope).getValue();
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

  public Resource getResource(String name, Scope scope)
  {
    if (resources == null)
      throw new NoSuchElementException();
    return resources.get(name, scope);
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

  /**
   *  Returns a block definition with given name which is under
   *  the given scope.
   *
   *  @throws NoSuchElementException
   *             if such a blok is not present in the internal
   *             buffer
   */
  public Block getBlock(String name, Scope scope)
  {
    if (blocks == null)
      throw new NoSuchElementException();
    return blocks.get(name, scope);
  }

  /*
   *
   *     Signal Definitions.
   *
   */

  /** A data structure for name and scope look up. */
  private ScopeMap<Signal> signals;

  /** A data structure for indexed look up. */
  private ArrayList<Triple<String, Scope, Signal>> signalIndexes;

  /**
   *  Puts given signal into the internal data structure.
   *  A unique order number is assigned to the signal (index).
   *
   *  @param name
   *             an identifier of the signal which serves
   *             for referencing it
   *
   *  @param scope
   *             a scope under which the signal was defined
   *
   *  @param signal
   *             a signal object to store
   *
   *  @throws IllegalArgumentException
   *             if the name is empty string or a blank string
   *
   *  @throws NullPointerException
   *             if either of the parameters is <code>null</code>
   *             value
   *
   *  @throws DuplicateElementException
   *             the pair name and scope must be unique across
   *             the whole application. If it isn't, this exception
   *             is thrown
   */
  public void putSignal(String name, Scope scope, Signal signal)
  throws DuplicateElementException
  {
    String trimmedName = notBlank(name).trim();
    notNull(scope);
    notNull(signal);
    if (signals == null)
    {
      signals = new ScopeMap<Signal>();
      signalIndexes = new ArrayList<Triple<String, Scope, Signal>>();
    }
    signals.put(name, scope, signal);
    signalIndexes.add(
        new ImmutableTriple<String, Scope, Signal>(name, scope, signal));
  }

  /**
   *  Returns the index of the given signal.
   *
   *  @param signal
   *
   *  @return an order number of the given signal
   *
   *  @throws NullPointerException
   *             if the parameter is <code>null</code> value
   *
   *  @throws NoSuchElementException
   *             if the given signal is not present in the internal
   *             buffer
   */
  public int getSignalIndex(Signal signal)
  {
    notNull(signal);
    if (signalIndexes == null) // TODO:
      throw new NoSuchElementException();
    for (int i = 0; i < signalIndexes.size(); i++)
    {
      if (signalIndexes.get(i).getRight() == signal)
        return i;
    }
    throw new NoSuchElementException(); // TODO:
  }

  /**
   *  Returns the signal with the given name that was defined
   *  under the given scope or some of the parent scope.
   *
   *  @throws NullPointerException
   *
   *  @throws NoSuchElementException
   */
  public Signal getSignal(String name, Scope scope)
  {
    notNull(name);
    notNull(scope);
    if (signals == null)  // TODO:
      throw new NoSuchElementException();
    return signals.get(name, scope);
  }

  /**
   *  Returns the signal with the given order number (index).
   *
   *  @throws IndexOutOfBoundsException
   *             if the index if out of range
   */
  public Triple<String, Scope, Signal> getSignal(int index)
  {
    if (signalIndexes == null)  // TODO:
      throw new IndexOutOfBoundsException();
    return signalIndexes.get(index);
  }

  /**
   *  Returns number of signal objects inside the internal buffer.
   */
  public int getSignalsSize()
  {
    if (signalIndexes == null) return 0;
    return signalIndexes.size();
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

  /*
   *
   *     Use Objects
   *
   */

  private ArrayList<Pair<Use, Scope>> uses 
      = new ArrayList<Pair<Use, Scope>>();

  /**
   *  Adds a given use object into the internal buffer.
   *
   *  @param use
   *             an object to add
   *
   *  @param scope
   *             a scope under which the use object was defined
   */
  public void add(Use use, Scope scope)
  {
    notNull(use);
    notNull(scope);
    uses.add(new ImmutablePair<Use, Scope>(use, scope));
  }

  public int getUseObjectsSize()
  {
    return uses.size();
  }

  public Pair<Use, Scope> getUse(int index)
  {
    return uses.get(index);
  }

  public void removeUse(int index)
  {
    uses.remove(index);
  }

  public int indexOf(Use use, Scope scope)
  {
    for (int i=0; i<uses.size(); i++)
    {
      Pair<Use, Scope> u = uses.get(i);
      if (u.getKey().equals(use) && u.getValue().equals(scope))
        return i;
    }
    throw new NoSuchElementException();
  }

  public int indexOf(Use use, int startIndex)
  {
    for (int i=startIndex; i<uses.size(); i++)
      if (uses.get(i).getKey().equals(use))
        return i;
    throw new NoSuchElementException();
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

  /**
   *  Returns the content of this object in the human readable form.
   */
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("Application\n");
    toString("  ", sb);
    sb.append("\n");
    return sb.toString();
  }

  /**
   *  Returns the content of this object in the human readable form.
   */
  @Override
  void toString(String indent, StringBuilder sb)
  {
    String indent2 = indent + "  ";

    // print definitions
    if (definitions != null && !definitions.isEmpty())
    {
      sb.append(indent)
        .append("Definitions\n");
      definitions.toString(indent2, sb);
    }

    // write configuration
    super.toString(indent, sb);

    // write resource definitions
    if (resources != null && !resources.isEmpty())
    {
      sb.append(indent).append("Resource Definitions\n");
      resources.toString(indent2, sb);
    }

    // write modules
    if (modules != null && modules.size() > 0)
    {
      sb.append(indent).append("Modules\n");
      for (Module module : modules)
        module.toString(indent2, sb);
    }

    // write signal definitions
    if (signals != null && !signals.isEmpty())
    {
      sb.append(indent).append("Signal Definitions\n");
      signals.toString(indent2, sb);
    }

    // write use objects
    if (uses != null && uses.size() > 0)
    {
      sb.append(indent).append("Use Objects\n");
      //for (Use use : uses)
        //use.toString(indent2, sb);
    }

    // print block objects
    if (blocks != null && !blocks.isEmpty())
    {
      sb.append(indent).append("Block Definitions\n");
      blocks.toString(indent2, sb);
    }

  }
}
