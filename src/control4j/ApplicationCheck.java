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

import java.util.LinkedList;
import java.util.Arrays;
import control4j.application.SignalManager;
import control4j.application.SignalDeclaration;
import static control4j.tools.LogMessages.*;
import static control4j.tools.Logger.*;

/**
 *
 *  Checks the consistency of the application. It checks that there is
 *  no input whose value is unavailable in time of module execution and
 *  that there are no two or more outputs which would be connected into
 *  one signal.
 *
 *  <p>To perform the check, just call the check method.
 *
 *  @see #check
 *
 */
class ApplicationCheck
{

  private OutputMarker[] outputOccupancy;

  private boolean[] inputOccupancy;

  private LinkedList<InputMarker> brokenInputs = new LinkedList<InputMarker>();

  /**
   *  True value indicated that the check has already been performed.
   */
  private boolean completed = false;
  
  /**
   *  Does nothing.
   */
  public ApplicationCheck()
  { }

  /**
   *  Initialize internal structures before the check begins.
   */
  private void initialize()
  {
    int signals = SignalManager.getInstance().size();
    outputOccupancy = new OutputMarker[signals];
    inputOccupancy = new boolean[signals];
    Arrays.fill(outputOccupancy, null);
    Arrays.fill(inputOccupancy, false);
    brokenInputs.clear();
    completed = false;
  }

  /**
   *  Check that the application is consistent and may be executed.
   *
   */
  public void check()
  {
    // initialize 
    initialize();
    // mark signal usage
    int signals = SignalManager.getInstance().size();
    ModuleManager moduleManager = ModuleManager.getInstance();
    for (Module module : moduleManager)
    {
      checkInputs(module);
      markInputs(module);
      markOutputs(module);
    }
    // evaluate results
    // inputs that are not available in time of module execution
    ErrorManager errors = ErrorManager.getInstance();
    for (InputMarker input : brokenInputs)
      if (outputOccupancy[input.signal] != null)
      {
        // report loopback problem
        errors.reportLoopback(input.input, input.module.getDeclarationReference());
      }
    // find joined outputs and not used signals
    for (int i=0; i<signals; i++)
    {
      if (!inputOccupancy[i])
      {
        // just warning that thre is unused signal
        SignalDeclaration signal = SignalManager.getInstance().get(i);
	errors.reportUnusedSignal(signal.getDeclarationReference());
      }
      if (outputOccupancy[i] == null && inputOccupancy[i])
      {
        // report missing output error
        SignalDeclaration signal = SignalManager.getInstance().get(i);
	errors.reportMissingOutput(signal.getDeclarationReference());
      }
      if (outputOccupancy[i] != null && outputOccupancy[i].next != null)
      {
        // report joined outputs
	int number = 0;
	OutputMarker marker = outputOccupancy[i];
	while (marker != null)
	{
	  number ++;
	  marker = marker.next;
	}
	int[] outputs = new int[number];
	String[] moduleReferences = new String[number];
	marker = outputOccupancy[i];
	int count = 0;
	while (marker != null)
	{
	  outputs[count] = marker.output;
	  moduleReferences[count] = marker.module.getDeclarationReference();
	  count++;
	  marker = marker.next;
	}
	errors.reportJoinedOutputs(moduleReferences, outputs);
      }
    }
  }

  /**
   *
   */
  private void checkInputs(Module module)
  {
    int[] map = getInputMap(module);
    if (map != null)
      for (int i=0; i<map.length; i++)
        if (map[i] >= 0 && outputOccupancy[map[i]] == null)
	{
	  InputMarker error = new InputMarker();
	  error.signal = map[i];
	  error.module = module;
	  error.input = i;
	  brokenInputs.add(error);
        }
  }

  /**
   *
   */
  private void markOutputs(Module module)
  {
    int[] map = getOutputMap(module);
    if (map != null)
      for (int i=0; i<map.length; i++)
        if (map[i] >= 0)
	{
	  OutputMarker marker = new OutputMarker();
	  marker.module = module;
	  marker.output = i;
	  marker.next = outputOccupancy[map[i]];
	  outputOccupancy[map[i]] = marker;
        }
  }

  private void markInputs(Module module)
  {
    int[] map = getInputMap(module);
    if (map != null)
      for (int i : map)
        if (i >= 0)
	  inputOccupancy[i] = true;
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
  private class InputMarker
  {
    public int signal;
    public Module module;
    public int input;
  }

  /**
   *
   */
  private class OutputMarker
  {
    public Module module;
    public int output;
    public OutputMarker next;
  }

}
