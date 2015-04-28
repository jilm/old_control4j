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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import control4j.tools.DuplicateElementException;

import cz.lidinsky.tools.ToStringBuilder;

public class Block extends DeclarationBase
{

  public Block()
  {
    super();
  }

  /*
   *
   *     Input of the Block
   *
   */

  private HashSet<String> inputSet = new HashSet<String>();

  public void addInput(String name)
  {
    inputSet.add(name);
  }

  public boolean containsInput(String name)
  {
    return inputSet.contains(name);
  }

  public Set<String> getInputSet()
  {
    return inputSet;
  }

  /*
   *
   *     Output of the Block
   *
   */

  private HashSet<String> outputSet = new HashSet<String>();

  public void addOutput(String name)
  {
    outputSet.add(name);
  }

  public boolean containsOutput(String name)
  {
    return outputSet.contains(name);
  }

  public Set<String> getOutputSet()
  {
    return outputSet;
  }

  /*
   *
   *     Modules.
   *
   */

  /** Internal buffer for modules. */
  private LinkedList<control4j.application.nativelang.Module> modules 
      = new LinkedList<control4j.application.nativelang.Module>();

  /**
   *  Add given module into the internal buffer.
   */
  public void addModule(control4j.application.nativelang.Module module)
  {
    modules.add(module);
  }

  /**
   *  Return collection that contains all of the modules that
   *  are under this block.
   */
  public Collection<control4j.application.nativelang.Module> getModules()
  {
    return modules;
  }

  /*
   *
   *     Signal Definitions.
   *
   *     Because it is not possible to resolve the scope of the
   *     signal definition by the time of translation, the signal
   *     definitions must be here in their raw object.
   *
   */

  private ArrayList<control4j.application.nativelang.Signal> signals
      = new ArrayList<control4j.application.nativelang.Signal>();

  public void addSignal(control4j.application.nativelang.Signal signal)
  {
    signals.add(signal);
  }

  Collection<control4j.application.nativelang.Signal> getSignals()
  {
    return signals;
  }

  /*
   *
   *     Use Objects
   *
   */

  private LinkedList<control4j.application.nativelang.Use> uses
      = new LinkedList<control4j.application.nativelang.Use>();

  public void addUse(control4j.application.nativelang.Use use)
  {
    uses.add(use);
  }

  Collection<control4j.application.nativelang.Use> getUseObjects()
  {
    return uses;
  }

  /*
   *
   *     Other Methods
   *
   */

  @Override
  public void toString(ToStringBuilder builder)
  {
    super.toString(builder);
    builder.append("inputSet", inputSet)
        .append("outputSet", outputSet)
        .append("modules", modules)
        .append("signals", signals)
        .append("uses", uses);
  }

}
