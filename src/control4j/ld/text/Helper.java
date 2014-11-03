package control4j.ld.text;

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

import control4j.ld.Contact;
import control4j.ld.ContactBlock;
import control4j.ld.SerialContactBlock;
import control4j.ld.ParallelContactBlock;

/**
 *  
 */
class Helper
{

  private Helper()
  {
  }

  /**
   *  Recursively counts and returns, how wide will be given contact
   *  block in characters.
   */
  static int getCharWidth(ContactBlock block)
  {
    Library library = Library.getInstance();
    if (block instanceof SerialContactBlock)
    {
      int widthName = 0;
      int widthLine = 0;
      int debt = 0;
      SerialContactBlock serialBlock = (SerialContactBlock)block;
      for (int i=0; i<serialBlock.size(); i++)
      {
        ContactBlock child = serialBlock.get(i);
	if (child instanceof ParallelContactBlock)
	{
	  widthName = Math.max(widthName, widthLine) + 1;
	  widthName += getCharWidth(child);
	  widthLine = widthName;
	  debt = 1;
	}
	else
	{
	  Contact contact = (Contact)child;
	  int contactNameWidth = contact.getName().length() + 1;
	  int contactLineWidth = library.get(contact.getType()).length() + 1;
	  int centre = widthName + contactNameWidth/2;
	  centre = Math.max(centre, widthLine + contactLineWidth/2);
	  widthName = centre - contactNameWidth/2 + contactNameWidth;
	  widthLine = centre - contactLineWidth/2 + contactLineWidth;
	  debt = 1;
	}
      }
      return Math.max(widthName, widthLine) - 1;
    }
    if (block instanceof ParallelContactBlock)
    {
      int width = 0;
      ParallelContactBlock parallelBlock = (ParallelContactBlock)block;
      for (int i=0; i<parallelBlock.size(); i++)
        width = Math.max(width, getCharWidth(parallelBlock.get(i)));
      return width;
    }
    else
    {
      Contact contact = (Contact)block;
      int width = contact.getName().length();
      width = Math.max(width, library.get(contact.getType()).length());
      return width;
    }
  }

  /**
   *  Recursively counts and returns how many contacts will be placed
   *  next to each other.
   */
  static int getWidth(ContactBlock block)
  {
    if (block instanceof SerialContactBlock)
    {
      int width = 0;
      SerialContactBlock serialBlock = (SerialContactBlock)block;
      for (int i=0; i<serialBlock.size(); i++)
        width += getWidth(serialBlock.get(i));
      return width;
    }
    else if (block instanceof ParallelContactBlock)
    {
      int width = 0;
      ParallelContactBlock parallelBlock = (ParallelContactBlock)block;
      for (int i=0; i<parallelBlock.size(); i++)
        width = Math.max(width, getWidth(parallelBlock.get(i)));
      return width;
    }
    else
    {
      return 1;
    }
  }

}
