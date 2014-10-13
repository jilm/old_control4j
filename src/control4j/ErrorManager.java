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

import control4j.tools.DeclarationReference;
import static control4j.tools.Logger.*;
import java.util.LinkedList;

/**
 *
 *  Provides a unified way to process and manage error messages.
 *
 *  <p>This class is singleton. There is only one instance of this
 *  object.
 *
 */
public class ErrorManager
{
  
  private static final ErrorManager instance = new ErrorManager();

  private LinkedList<String> errors = new LinkedList<String>();

  private LinkedList<String> warnings = new LinkedList<String>();

  private StringBuilder sb = new StringBuilder();

  private ErrorManager()
  { }

  public static ErrorManager getInstance()
  {
    return instance;
  }

  public void report()
  {
    for (String message : errors) severe(message);
    for (String message : warnings) warning(message);
    if (errors.size() > 0) System.exit(1);
  }
  
  /**
   *
   */
  public void reportLoopback(int inputIndex, String moduleReference)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("Syntax error: probably there is a loopback becouse I was not able to arrange modules such order that each module has all of its input awaylable in time of its execution. Problem detected here:")
      .append('\n')
      .append("Missing input with index: ")
      .append(inputIndex)
      .append('\n')
      .append("In module which was declared here:")
      .append('\n')
      .append(moduleReference);
    errors.add(sb.toString());
  }

  /**
   *
   */
  public void reportUnusedSignal(DeclarationReference signalReference)
  {
    sb.delete(0, sb.length());
    sb.append("Warning: there is unused signal:")
      .append('\n')
      .append(signalReference.toString());
    warnings.add(sb.toString());
  }

  /**
   *
   */
  public void reportMissingOutput(DeclarationReference signalReference)
  {
    sb.delete(0, sb.length())
      .append("Syntax error: missing output for signal:")
      .append('\n')
      .append(signalReference.toString());
    errors.add(sb.toString());
  }

  /**
   *
   */
  public void reportJoinedOutputs(String[] moduleReferences, int[] outputs)
  {
    sb.delete(0, sb.length())
      .append("Synatax Error: there are two or more outputs joined into one signal!");
    for (int i=0; i<outputs.length; i++)
    {
      sb.append('\n')
        .append("output index: ")
	.append(outputs[i]).append('\n')
	.append("in module: ").append('\n')
	.append(moduleReferences[i]);
    }
    errors.add(sb.toString());
  }

  /**
   *  Reports that there is a signal name used as an input or
   *  output that was not declared.
   *
   *  @param reference
   *             a reference to the input or output where the
   *             signal was used
   */
  public void reportUndeclaredSignal(String reference)
  {
    sb.delete(0, sb.length())
      .append("Syntax error: usage of undeclared signal.")
      .append('\n')
      .append(reference);
    errors.add(sb.toString());
  }

  /**
   *
   */
  public void reportSyntaxError(String message, String reference)
  {
    sb.delete(0, sb.length())
      .append("Syntax error: ")
      .append(message)
      .append('\n')
      .append(reference);
    errors.add(sb.toString());
  }

  /**
   *
   */
  public void reportDuplicateSignalDeclaration(String reference1, String reference2)
  {
    sb.delete(0, sb.length())
      .append("Syntax error: There are two or more identical signal declarations: ")
      .append('\n')
      .append(reference1)
      .append('\n')
      .append(reference2);
    errors.add(sb.toString());
  }
}
