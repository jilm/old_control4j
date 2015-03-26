package control4j.application;

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

import java.util.HashSet;
import java.util.LinkedList;

import control4j.tools.DuplicateElementException;

public class Block extends DeclarationBase
{

  public Block()
  {
    super();
  }

  private HashSet<String> inputSet;

  public void addInput(String name)
  {
    if (inputSet == null) inputSet = new HashSet<String>();
    inputSet.add(name);
  }

  private HashSet<String> outputSet;

  public void addOutput(String name)
  {
    if (outputSet == null) outputSet = new HashSet<String>();
    outputSet.add(name);
  }

  private LinkedList<Module> modules = new LinkedList<Module>();

  public void addModule(Module module)
  {
    modules.add(module);
  }

  private ScopeMap<Signal> signals;

  public void putSignal(String name, Scope scope, Signal signal)
  throws DuplicateElementException
  {
    if (signals == null) signals = new ScopeMap<Signal>();
    signals.put(name, scope, signal);
  }

  private LinkedList<Use> uses;

  public void addUse(Use use)
  {
    if (uses == null) uses = new LinkedList<Use>();
    uses.add(use);
  }

  void toString(String indent, StringBuilder sb)
  {
    sb.append("Block {\n");
    String indent2 = indent + "  ";
    sb.append(indent).append("}\n");
  }
}
