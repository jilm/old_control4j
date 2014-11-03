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
 *  Metainformations about a signal.
 */
public class SignalDeclaration extends DeclarationBase
{

  private String name;
  private String unit;
  private Scope scope;

  /**
   *  Crate a new signal declaration object with given name and scope.
   *
   *  @param scope
   *             scope of the signal. May be null, which is global scope.
   *
   *  @param name
   *             name of the signal. May not be null or empty string.
   */
  public SignalDeclaration(Scope scope, String name)
  {
    this.scope = scope;
    this.name = name;
  }

  /**
   *  Returns a name of the signal. This is a label which was assigned
   *  by a user or programmer.
   *
   *  @return a name of the signal
   */
  public String getName()
  {
    return name;
  }

  /**
   *  Returns a unit of the signal.
   *
   *  @return a unit of the signal
   */
  public String getUnit()
  {
    return unit;
  }

  /**
   *  Set the unit of the signal.
   *
   *  @param unit
   *             a unit of the signal
   */
  public void setUnit(String unit)
  {
    this.unit = unit;
  }

  /**
   *  Returns a scope of the signal.
   *
   *  @return a scope of the signal
   */
  public Scope getScope()
  {
    return scope;
  }

  @Override
  protected DeclarationReference getThisObjectIdentification()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("signal (with name name: ")
      .append(name)
      .append(')');
    return new DeclarationReference(sb.toString());
  }
}
