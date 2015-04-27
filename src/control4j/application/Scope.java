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

import cz.lidinsky.tools.tree.AbstractNode;
import cz.lidinsky.tools.tree.INode;

/**
 *
 *  A class which serves as a scope marker for signal definitions.
 *
 *  <p>There is one global scope which allows to share signals between
 *  different parts of the project. Each part of the project may 
 *  create arbitrary number of local scopes.
 *
 *  <p>Scopes may be ordered hierarchicaly. Each scope (except the global
 *  one) may have one parent scope attached. In that case, if there
 *  is not a signal in a particular scope, even parent scope will be
 *  scaned.
 *  
 */
public class Scope extends AbstractNode<Scope> implements INode<Scope>
{

  /** The global scope */
  private static final Scope global = new Scope();

  /**
   *  Returns the global scope.
   *
   *  @return global scope
   */
  public static Scope getGlobal()
  {
    return global;
  }

  /**
   *  Creates a new scope without a parent scope.
   */
  public Scope()
  {
  }

  /**
   *  Creates a new scope with parent scope attached.
   *
   *  @param parent
   *             parent scope. It may be null, in such a case
   *             scope is treated the same as if it doesn't have
   *             any parent scope at all
   */
  public Scope(Scope parent)
  {
    parent.addChild(this);
  }

}
