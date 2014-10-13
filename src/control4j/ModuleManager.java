package control4j;

/*
 *  Copyright 2013, 2014 Jiri Lidinsky
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
import java.util.Iterator;
import control4j.application.SignalManager;
import static control4j.tools.LogMessages.*;
import static control4j.tools.Logger.*;

/**
 *
 *  Singleton, which contains instances of all modules which belong
 *  to the application in the internal buffer.
 *
 *  <p>The object may be in two states. In one state, one can add
 *  new objects but these objects are not accessible. The modules
 *  are not sorted in any way in the buffer. But, once the complete
 *  method is called, the modules in the buffer are sorted to the
 *  executable order, access methods are unlocked, but you cannot
 *  add another module any more. The object is prepared for execution.
 *
 */
public class ModuleManager implements Iterable<Module>
{
  
  /**
   *  A reference to the only instance of the resource manager.
   */
  private final static ModuleManager manager = new ModuleManager();

  /**
   *  Internal buffer for all of the modules.
   */
  private ArrayList<Module> buffer = new ArrayList<Module>();

  /**
   *  Indicates wheather the complete function was called
   */
  private boolean completed = false;

  /**
   *
   */
  private ModuleManager()
  { }

  /**
   *  Returns an instance of the ResourceManager class.
   *
   *  @return an instance ot the ResourceManager class
   */
  public static ModuleManager getInstance()
  {
    return manager;
  }

  /**
   *  Adds given module implementation into the internal buffer.
   *  The module must be fully configured and prepared.
   *
   *  @param module
   *             an object to be added, may not be null
   *
   *  @throws IllegalArgumentException
   *             if argument objec is null
   *
   *  @throws IllegalStateException
   *             if complete method has already been called
   */
  public void add(Module module)
  {
    if (module == null)
      throw new IllegalArgumentException();
    if (!completed)
      buffer.add(module);
    else
      throw new IllegalStateException();
  }

  /**
   *  Sorts the modules into the executable order. Should be called
   *  after all of the modules were added and before the execution.
   *
   *  @throws SyntaxErrorException
   *              if it is not possible to arrange modules into the
   *              executable order. There may be many reasons.
   */
  public void complete()
  {
    // Sort modules
    boolean[] outputs = new boolean[SignalManager.getInstance().size()];
    for (int i=0; i<outputs.length; i++) outputs[i] = false;
    for (int i=0; i<buffer.size()-1; i++)
    {
      for (int j=i+1; j<buffer.size(); j++)
      {
	int relation = compare(buffer.get(i), buffer.get(j), outputs);
	if (relation > 0)
	{
	  Module temp = buffer.set(j, buffer.get(i));
	  buffer.set(i, temp);
	}
      }
      markOutputs(buffer.get(i), outputs);
    }
    completed = true;
  }

  /**
   *  if module1 < module2 returns <0
   *  if module1 = module2 returns  0
   *  if module1 > module2 returns >0
   */
  private int compare(Module module1, Module module2, boolean[] outputs)
  {
    int score1 = 3;
    if (module1 instanceof OutputModule)
      score1 = 0;
    else if (module1 instanceof ProcessModule)
      score1 = checkInputs(module1, outputs) ? 1 : 2;
    int score2 = 3;
    if (module2 instanceof OutputModule)
      score2 = 0;
    if (module2 instanceof ProcessModule)
      score2 = checkInputs(module2, outputs) ? 1 : 2;
    return score1 - score2;
  }

  /**
   *
   */
  private int[] getInputMap(Module module)
  {
    int[] map;
    if (module instanceof InputModule)
      map = ((InputModule)(module)).getInputMap();
    else if (module instanceof ProcessModule)
      map = ((ProcessModule)(module)).getInputMap();
    else
      map = null;
    return map;
  }

  /**
   *
   */
  private int[] getOutputMap(Module module)
  {
    int[] map;
    if (module instanceof ProcessModule)
      map = ((ProcessModule)(module)).getOutputMap();
    else if (module instanceof OutputModule)
      map = ((OutputModule)(module)).getOutputMap();
    else
      map = null;
    return map;
  }

  /**
   *
   */
  private void markOutputs(Module module, boolean[] buffer)
  {
    int[] map = getOutputMap(module);
    if (map != null)
      for (int i : map)
        if (i >= 0) buffer[i] = true;
  }

  /**
   *  @return true if all of the inputs of given module are marked
   *            in the given buffer as true. Otherwise it returns
   *            false.
   */
  private boolean checkInputs(Module module, boolean[] buffer)
  {
    int[] map = getInputMap(module);
    if (map != null)
      for (int i : map) 
        if (i >= 0 && !buffer[i]) 
	  return false;
    return true;
  }

  /**
   *  Provides sequential access to the buffer with modules. Modules
   *  are ordered in the executable order.
   *
   *  @return an iterator object
   *
   *  @throws IllegalStateException
   *              if complete method has not been already called
   */
  public Iterator<Module> iterator()
  {
    if (completed)
      return buffer.iterator();
    else
      throw new IllegalStateException();
  }

  public int size()
  {
    return buffer.size();
  }

  public Module get(int index)
  {
    return buffer.get(index);
  }

}
