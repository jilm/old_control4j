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
 *
 */
public class SerialContactBlock extends ContactBlock
implements IToStringBuildable
{
  private LinkedList<ContactBlock> contacts = new LinkedList<ContactBlock>();

  public int size()
  {
    return contacts.size();
  }

  public void add(ContactBlock contactBlock)
  {
    contacts.add(contactBlock);
  }

  public ContactBlock get(int index)
  {
    return contacts.get(index);
  }

  public void remove(int index)
  {
    contacts.remove(index);
  }

  public void insert(ContactBlock contactBlock, int position)
  {
    contacts.add(position, contactBlock);
  }

  public void toString(ToStringBuilder sb) {
    sb.append("contacts", contacts);
  }
}
