package control4j.application;

/*
 *  Copyright 2013, 2014, 2015 Jiri Lidinsky
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

import cz.lidinsky.tools.ToStringBuilder;

/**
 *
 *  This is a crate object for the module input definition.
 *  Each input definition has to contain a reference to the
 *  signal which is the name of the signal and its scope.
 *  Each individual input may have its own configuration.
 *  It may modify the behavior of the module, it may tell, for
 *  instance that some input will be negated before the processing.
 *
 */
public class Input extends Configurable
{

  /** Name of the signal that is attached to the input */
  private String href;

  /** input scope */
  private Scope scope;

  /**
   *  Initialize new object. The default value for index is -1.
   *
   *  @param scope
   *             scope where the signal will be searched. May
   *             not be null.
   *
   *  @param href
   *             id of the signal. May not be null nor empty string.
   */
  public Input(Scope scope, String href)
  {
    this.scope = scope;
    this.href = href;
  }

  /**
   *  Copy constructor.
   */
  public Input(Input input)
  {
    super(input);
    this.href = input.href;
    this.scope = input.scope;
  }

  /**
   *  Returns the name of the signal it refers to.
   */
  public String getHref()
  {
    return href;
  }

  /**
   *  Returns the scope where the referenced signal will be searched.
   */
  public Scope getScope()
  {
    return scope;
  }

  @Override
  public void toString(ToStringBuilder builder)
  {
    super.toString(builder);
    builder.append("href", href)
        .append("scope", scope);
  }

}
