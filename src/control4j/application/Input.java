package control4j.application;

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

/**
 *
 *  This is a crate object for the module input declaration.
 *  Each input declaration has to contain a reference to the
 *  signal which is the name of the signal and its scope.
 *  Each input declaration contains index which is index
 *  into the input array whre the value of the signal will
 *  be stored. Each individual input may have its own configuration.
 *  It may modify the behavior of the module, it may tell, for
 *  instance that some input will be negated before the processing.
 *
 *  @see ModuleDeclaration
 *  
 */
public class Input extends DeclarationBase
{
  /** Name of the signal that is attached to the input */
  private String signal;

  /** Index of the module input */
  private int index;

  /** input scope */
  private Scope scope;

  /**
   *  Input configuration. This field may contain null, if the input
   *  doesn't heva any configuration.
   */
  private ConfigBuffer configuration;

  /**
   *  Initialize new object. The default value for index is -1.
   *
   *  @param scope
   *             scope where the signal will be searched. May
   *             not be null.
   *
   *  @param signal
   *             id of the signal. May not be null nor empty string.
   */
  public Input(Scope scope, String signal)
  {
    this.scope = scope;
    this.signal = signal;
    this.index = -1;
  }

  public int getIndex()
  {
    return index;
  }

  public void setIndex(int index)
  {
    this.index = index;
  }

  public String getSignal()
  {
    return signal;
  }

  public Scope getScope()
  {
    return scope;
  }

  public void putProperty(Property property)
  {
    if (configuration == null)
      configuration = new ConfigBuffer();
    configuration.put(property);
  }

  public ConfigBuffer getConfiguration()
  {
    return configuration;
  }

  /**
   *  Adds an information that this is input of some module.
   */
  @Override
  public void setDeclarationReference(DeclarationReference reference)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("module input (connected signal: ")
      .append(signal)
      .append(')');
    if (reference != null)
    {
      sb.append(", declared");
      super.setDeclarationReference(reference.addText(sb.toString()));
    }
    else
    {
      reference = new DeclarationReference(sb.toString());
      super.setDeclarationReference(reference);
    }
  }

}
