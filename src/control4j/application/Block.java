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
    sb.append("\n");

    String indent2 = indent + "  ";
    String indent3 = indent2 + "  ";

    // write input
    if (inputSet != null && inputSet.size() > 0)
    {
      sb.append(indent).append("Input: ");
      sb.append(inputSet.toString()).append("\n");
    }

    // write output
    if (outputSet != null && outputSet.size() > 0)
    {
      sb.append(indent).append("Output: ");
      sb.append(outputSet.toString()).append("\n");
    }

    // write modules
    if (modules != null && modules.size() > 0)
    {
      sb.append(indent).append("Modules\n");
      for (Module module : modules)
	module.toString(indent2, sb);
    }

    // write signals
    if (signals != null && !signals.isEmpty())
    {
      sb.append(indent).append("Signal Definitions\n");
      signals.toString(indent2, sb);
    }

    // write use objects
    if (uses != null && uses.size() > 0)
    {
      sb.append(indent).append("Use Objects\n");
      for (Use use : uses)
      {
	use.toString(indent2, sb);
      }
    }
  }
}
