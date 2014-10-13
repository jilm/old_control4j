package control4j.tools;

/*
 *  Copyright 2013 Jiri Lidinsky
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
 *  Serves as an interchange store between two threads. 
 *  It remembers only one object reference of type E and every time 
 *  a new object is stored, old one (if any) is overwritten.
 *  Overy time object is read, it is deleted from this object.
 *  Object is thread safe. 
 */    
public class InterchangePoint<E>
{
  private E store = null;
  
  /**
   *  Stores an item item into internal buffer. Previously
   *  stored object is overwritten. If item is null it makes
   *  the internal store empty. This method doesn't block 
   *  if internal store is not empty. Threads which wait on
   *  value are notified.       
   *  
   *  @param item item to store
   */         
  public synchronized void forcedSet(E item)
  {
    store = item;
    notifyAll();
  }
  
  /**
   *  Returns the item that was previosly stored by the set method.
   *  After that the item is no longer available in the internal storage.
   *  It means the item is deleted from internal store by this 
   *  method. If the internal store is empty, this method blocks
   *  until an item is available.
   *  
   *  @return item that was previously stored by the set method.
   */            
  public synchronized E get()
  {
    while (store == null)
    {
      try
      {
        wait();
      }
      catch (InterruptedException e) {}
    }
    E item = store;
    store = null;
    return item;
  }
  
  /**
   *  Returns the item that was previously stored by the set method,
   *  or it returns null if the internal store is empty. The returned
   *  item is removed from an internal store and it stays empty.
   *  
   *  @return item that was previously stored by the set method
   *          or null if the internal store was empty.
   */                      
  public synchronized E tryGet()
  {
    E item = store;
    store = null;
    return item;
  }
  
  /**
   *  Returns true if and only if the internal store is empty.
   *  It means if get function will block.   
   *  
   *  @return true if internal storage is empty, false otherwise.
   */   
  public synchronized boolean isEmpty()
  {
    return store == null;
  }
}
