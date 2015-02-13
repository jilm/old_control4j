package control4j.protocols.signal;

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

import java.util.HashSet;
import java.util.Iterator;

/**
 *
 *  A request for new data.
 *
 */
public class Request extends Message implements Iterable<String>
{

  protected HashSet<String> ids = null;

  public Request()
  {
  }

  /**
   *  Add a new id into the set of requested signals. If the id
   *  is already in the set, nothing happens.
   *
   *  @param id
   *             an id which will be added into the set
   *             of requested signals
   *
   *  @throws NullPointerException
   *             if <code>id</code> contains <code>null</code> value
   */
  public void add(String id)
  {
    if (id == null) throw new NullPointerException();
    if (ids == null) ids = new HashSet<String>();
    ids.add(id);
  }

  public void remove(String id)
  {
  }

  /**
   *  Returns number of ids in the set of requested signals.
   *
   *  @return number of ids in the internal set
   */
  public int size()
  {
    if (ids == null)
      return 0;
    else
      return ids.size();
  }

  /**
   *  Returns an iterator over the elements in the set.
   */
  public Iterator<String> iterator()
  {
    if (ids == null)
      return new EmptyIterator();
    else
      return ids.iterator();
  }

  private class EmptyIterator implements Iterator<String>
  {
    public boolean hasNext()
    {
      return false;
    }

    public String next()
    {
      throw new java.util.NoSuchElementException();
    }

    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }

}
