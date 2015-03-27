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
import java.util.NoSuchElementException;

import control4j.tools.DuplicateElementException;

/**
 *
 *  A key, value data storage, where the key is composed of the
 *  pair: a name and a scope.
 *
 */
public class ScopeMap<E extends ObjectBase>
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
   *
   *  A key object which consists of a pair: a name and a scope.
   *
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

    /**
     *  Returns name and scope fields in the human readable form.
     */
    @Override
    public String toString()
    {
      String pattern = "name: {0}, scope: {1}";
      return java.text.MessageFormat.format(pattern, name, scope.toString());
    }

  }

  /*
   *
   *     Access Methods
   *
   */

  /**
   *  Associates the given value with the specified key and scope.
   *
   *  @throws IllegalArgumentException
   *             if eather of the params contain a null value
   *
   *  @throws DuplicateElementException
   *             if there already is a value with given name
   *             and scope inside the buffer
   */
  public void put(String name, Scope scope, E value)
  throws DuplicateElementException
  {
    if (name == null || scope == null || value == null)
      throw new IllegalArgumentException();
    if (buffer == null) buffer = new HashMap<Key, E>();
    Key key = new Key(name, scope);
    E result = buffer.put(key, value);
    if (result != null)
      throw new DuplicateElementException();
  }

  /**
   *  Returns the value that is associated with given name and scope.
   *
   *  @param name
   *
   *  @param scope
   *
   *  @return value that is associated with given name and scope
   *
   *  @throws IllegalArgumentException
   *             if eather name or scope contain null value
   *
   *  @throws NoSuchElementException
   *             if there is no such value in the internal buffer
   */
  public E get(String name, Scope scope)
  {
    if (name == null || scope == null)
      throw new IllegalArgumentException();
    if (buffer == null)
      throw new NoSuchElementException();
    Key tempKey = new Key(name, scope);
    while (tempKey.scope != null)
    {
      E result = buffer.get(tempKey);
      if (result != null) return result;
      tempKey.scope = tempKey.scope.getParent();
    }
    throw new NoSuchElementException();
  }

  public boolean isEmpty()
  {
    return buffer.isEmpty();
  }

  @Override
  public String toString()
  {
    return buffer.toString();
  }

  /**
   *  Writes each record on the separate line which starts
   *  with indent string.
   */
  void toString(String indent, StringBuilder sb)
  {
    java.util.Set<Key> keys = buffer.keySet();
    for (Key key : keys)
    {
      sb.append(indent)
	//.append(buffer.get(key).getClass().getSimpleName())
	.append('[')
	.append(key.toString())
	.append("] = ");
      String indent2 = indent + "  ";
      buffer.get(key).toString(indent2, sb);
    }

  }

}
