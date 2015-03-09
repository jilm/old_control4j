package control4j.application.nativelang;

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

import java.util.NoSuchElementException;

/**
 *
 *  A data structure that is dedicated to store declarations of
 *  resources, signals, property values, blocks, ... Each such
 *  object is identified by a name and scope. Name and scorp pair
 *  must be unique for each value.
 *
 */
class NameScopeMap<E>
{

  /**
   *  Initialize an empty data structure.
   */
  public NameScopeMap()
  {
  }

  /**
   *  Stores a given value into this data structure under the
   *  key which is comprised of the name and the scope.
   *
   *  @param name
   *
   *  @param scope
   *
   *  @param value
   *             a value which is to be stored
   *
   *  @throws NullPointerException
   *             if either of the parameters contain null value
   *
   *  @throws SuchElementsAlreadyExistsException
   *             if there already is a value with the some name
   *             and scope stored inside this data structure
   */
  public void put(String name, Scope scope, E value)
  {
  }

  /**
   *  Returns a value which is stored under the key which is
   *  comprised of the name and the scope parameters. It returns
   *  a value which was stored under the given name, and under
   *  the given scope or under some of the parents of the given
   *  scope. If there are more values with the same name but
   *  different scopes, it returns the one which scope is the
   *  closest parent of the given scope.
   *
   *  @param name
   *
   *  @param scope
   *
   *  @return a value that is stored under the given name and
   *             scope
   *
   *  @throws NullPointerException
   *             if either of the parameters contain null value
   *
   *  @throws NoSuchElementException
   *             if there is no value under the given name
   *             and scope
   */
  public E get(String name, Scope scope)
  throws NoSuchElementException
  {
    return null;
  }

}
