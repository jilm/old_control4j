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

/**
 *
 *  A class which serves as a scope marker for signal definitions.
 *
 *  There is one global scope which allows to share signals between 
 *  different parts of the project. Each part of the project may 
 *  create arbitrary number of local scopes.
 *
 *  Scopes may be ordered hierarchicaly. Each scope (except the global
 *  one) may have one parent scope attached. In that case, if there
 *  is not a signal in a particular scope, even parent scope will be
 *  scaned.
 *  
 */
public class Scope
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
  public Scope()
  {
    parent = null;
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
    this.parent = parent;
  }

  /**
   *  Returns parent scope or null if the parent scope has not
   *  been attached.
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
