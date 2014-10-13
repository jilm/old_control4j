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

import java.util.LinkedList;

/**
 *  FIFO data structure. This data structure is dedicated for
 *  exchange information between threads. All of the methods
 *  are synchronized.
 */
public class Queue<E>
{
  private LinkedList<E> list = new LinkedList<E>();

  /**
   *  Adds an item to the end of the queue.
   *
   *  @param item item to be added to the end of the queue
   */
  public synchronized void queue(E item)
  {
    list.add(item);
    notify();
  }

  /**
   *  Removes and returns an item from the head (first item) of the queue.
   *
   *  @return an item from the begin of the queue or null if
   *    the queue is empty.
   */
  public synchronized E dequeue()
  {
    return list.poll();
  }
  
  public synchronized void clear()
  {
    list.clear();
  }

  public synchronized E blockingDequeue()
  {
    while (list.size() == 0)
    {
      try { wait(); } catch (InterruptedException e) {}
    }
    return list.poll();
  }

  /**
   *  Returns true if and only if the queue is empty, it means, it doesn't
   *  contain any item.
   *
   *  @return true if the queue is empty, false otherwise.
   */
  public synchronized boolean isEmpty()
  {
    return list.size() == 0;
  }
  
}
