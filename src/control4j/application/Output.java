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

public class Output extends DeclarationBase
{
  private String signal;
  private int index;
  private Scope scope;
  private ConfigBuffer configuration;

  public Output(Scope scope, String signal)
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

  @Override
  protected DeclarationReference getThisObjectIdentification()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("module output (signal name: ")
      .append(signal)
      .append(')');
    return new DeclarationReference(sb.toString());
  }

}
