package control4j.application.nativelang;

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

/**
 *
 *  An immutable object which serves as a scope marker.
 *
 *  <p>There is one global scope which allows to share definitions
 *  across different parts of the project. Each part of the project 
 *  may create arbitrary number of local scopes.
 *
 *  <p>Scopes are ordered hierarchicaly. Each scope (except the 
 *  global one) have got one parent scope attached. If there is not 
 *  a wanted definition in a particular scope, even parent 
 *  scope will be scaned.
 *  
 */
public class Scope extends DeclarationBase
{

  /** The global scope */
  private static final Scope global = new Scope();

  /** Parent scope */
  private Scope parent;

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
  private Scope()
  {
    parent = null;
  }

  /**
   *  Creates a new scope with parent scope attached.
   *
   *  @param parent
   *
   *  @throws NullPointerException
   *             if the parameter contains null value
   */
  public Scope(Scope parent)
  {
    if (parent == null)
      throw new NullPointerException();
    this.parent = parent;
  }

  /**
   *  Returns parent scope or null if this is a global scope.
   *
   *  @return parent scope or null
   */
  public Scope getParent()
  {
    return parent;
  }

  /**
   *  Tests wheather the given scope belongs under this scope and returns
   *  the result. It means, it searches the whole chain of the parent scopes 
   *  of this scope and returns the number that corresponds to the order
   *  of the given scope in the parents chain. It returns zero if the 
   *  requested scope is directly equal to this scope, one if it is
   *  equal to the parent, two if it is equal to the parent of the parent,
   *  etc. If the requested scope has not been found, it returns -1.
   *
   *  @param request
   *             requested scope 
   *
   *  @return number which reflect order of requested scope in the parent
   *             chain of this scope or minus one if the requested scope
   *             doesn't belong to this scope at all
   *
   *  @throws IllegalArgumentException
   *             if the argument is null
   */
  public int belongs(Scope request)
  {
    if (request == null) 
      throw new IllegalArgumentException();
    int order = 0;
    Scope scope = this;
    while (scope != null)
    {
      if (scope == request) return order;
      scope = scope.getParent();
      order ++;
    }
    return -1;
  }

}
