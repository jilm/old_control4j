package control4j.ld;

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
 *  Contains whole ladder diagram. It is a root object of ladder digram.
 *  Contains an ordered list of rungs.
 */
public class LadderDiagram
{
  /** An ordered list of rungs */
  private LinkedList<Rung> rungs = new LinkedList<Rung>();

  /**
   *  Returns number of rungs.
   *
   *  @return number of rungs in the diagram
   */
  public int size()
  {
    return rungs.size();
  }

  /**
   *  Adds a rung at the end of the list.
   *
   *  @param rung
   *             a rung to add at the end of the list.
   */
  public void add(Rung rung)
  {
    rungs.add(rung);
  }

  /**
   *  Inserts a rung to the list at specified position. Shifts the rung
   *  currently at the position and any subsequent rung to the right.
   *  
   *  @param rung
   *             a rung to be inserted
   *
   *  @param position
   *             a position where the rung will be inserted
   *
   *  @throws IndexOutOfBoundsException
   *             if the specified position is out of range
   */
  public void insert(Rung rung, int position)
  {
    rungs.add(position, rung);
  }

  /**
   *  Returns a rung at specified position
   *
   *  @param index
   *             an index of rung which will be returned
   *
   *  @return a rung at position index
   *
   *  @throws IndexOutOfBoundsException
   *             if the specified index is out of range
   */
  public Rung get(int index)
  {
    return rungs.get(index);
  }

  /**
   *  Removes the rung at the specified position in the list.
   *
   *  @param index
   *             the index of the rung to be removed
   *
   *  @throws IndexOutOfBoundsException
   *             if the specified index is out of range
   */
  public void remove(int index)
  {
    rungs.remove(index);
  }

}
