package control4j.application;

/*
 *  Copyright 2015 Jiri Lidinsky
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

import java.util.HashMap;

public class ScopeMap<E>
{

  /**
   *  Creates an empty storage.
   */
  public ScopeMap()
  { }

  /*
   *
   *    Back-end storage implemantation
   *
   */

  private HashMap<Key, E> buffer;

  /**
   *  A key object which consists of a pair: a name and a scope.
   */
  protected static class Key
  {

    String name;
    Scope scope;

    Key(String name, Scope scope)
    {
      if (name == null || scope == null)
	throw new IllegalArgumentException();
      this.name = name;
      this.scope = scope;
    }

    /**
     *  Returns true if and only if the parameter is an instance
     *  of the Key class and the name equals to the objects name
     *  and scope equals to the keys scope.
     */
    @Override
    public boolean equals(Object object)
    {
      if (object == null) return false;
      if (object instanceof Key)
      {
	Key key = (Key)object;
	return name.equals(key.name) && scope == key.scope;
      }
      else
	return false;
    }

    @Override
    public int hashCode()
    {
      return name.hashCode() ^ scope.hashCode();
    }

  }

  /*
   *
   *     Access Methods
   *
   */

  /**
   *  Associates the given value with the specified key and scope.
   *  If the map previously contained such mapping the value is
   *  replaced.
   */
  public E put(String name, Scope scope, E value)
  {
    if (name == null || scope == null || value == null)
      throw new IllegalArgumentException();
    if (buffer == null) buffer = new HashMap<Key, E>();
    Key key = new Key(name, scope);
    return buffer.put(key, value);
  }

  public E get(String name, Scope scope)
  {
    if (name == null || scope == null)
      throw new IllegalArgumentException();
    if (buffer == null) return null;
    Key tempKey = new Key(name, scope);
    while (tempKey.scope != null)
    {
      E result = buffer.get(tempKey);
      if (result != null) return result;
      tempKey.scope = tempKey.scope.getParent();
    }
    return null;
  }

}
