package control4j.ld;

/*
 *  Copyright 2013, 2015 Jiri Lidinsky
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

import cz.lidinsky.tools.IToStringBuildable;
import cz.lidinsky.tools.ToStringBuilder;

import java.util.LinkedList;

/**
 *  One rung of the ladder diagram. It contains one ContactBlock object
 *  and a collection of parallel interconnected coils.
 */
public class Rung implements IToStringBuildable
{
  /** A contact block */
  private ContactBlock contactBlock = null;

  /** A list of parallel interconection of coils. This list may be empty. */
  private LinkedList<Coil> coilBlock = new LinkedList<Coil>();

  public ContactBlock getContactBlock()
  {
    return contactBlock;
  }

  public void setContactBlock(ContactBlock contactBlock)
  {
    this.contactBlock = contactBlock;
  }

  /**
   *  Returns number of coils.
   */
  public int coilBlockSize()
  {
    return coilBlock.size();
  }

  public void addCoil(Coil coil)
  {
    coilBlock.add(coil);
  }

  public Coil getCoil(int index)
  {
    return coilBlock.get(index);
  }

  public void removeCoil(int index)
  {
    coilBlock.remove(index);
  }

  @Override
  public String toString() {
    return new ToStringBuilder()
      .append(this)
      .toString();
  }

  public void toString(ToStringBuilder sb) {
    sb.append("contactBlock", contactBlock)
      .append("coilBlock", coilBlock);
  }

}
