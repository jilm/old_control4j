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
class ContactBlockRenderer
{

  public static RendererBase render(ContactBlock block, int width)
  {
    // block rendering
    if (block instanceof SerialContactBlock)
    {
      int contactsRemaining = Helper.getWidth(block);
      int spaceNeeded = Helper.getCharWidth(block);
      int extraSpace = width - spaceNeeded;
      SerialRenderer renderer = new SerialRenderer();
      SerialContactBlock contactBlock = (SerialContactBlock)block;
      for (int i=0; i<contactBlock.size(); i++)
      {
        float extraSpacePerContact = (float)extraSpace / (float)contactsRemaining;
        if (contactBlock.get(i) instanceof ParallelContactBlock)
	{
	  ParallelContactBlock child = (ParallelContactBlock)contactBlock.get(i);
          int childContacts = Helper.getWidth(child);
	  int childMinLength = Helper.getCharWidth(child);
	  int childExtra = Math.round(extraSpacePerContact * childContacts);
          int childLength = childMinLength + childExtra;
	  RendererBase childAscii = render(child, childLength);
	  renderer.append(childAscii);
	  contactsRemaining -= childContacts;
	  extraSpace -= childExtra;
	}
	else
	{
	  Contact child = (Contact)contactBlock.get(i); 
	  int childExtra = Math.round(extraSpacePerContact);
	  renderer.append(child, childExtra);
	  contactsRemaining--;
	  extraSpace -= childExtra;
	}
      }
      renderer.complete();
      return renderer;
    }
    else if (block instanceof ParallelContactBlock)
    {
      ParallelContactBlock contactBlock = (ParallelContactBlock)block;
      ParallelRenderer renderer = new ParallelRenderer();
      for (int i=0; i<contactBlock.size(); i++)
        if (contactBlock.get(i) instanceof SerialContactBlock)
	{
	  RendererBase child = render(contactBlock.get(i), width);
	  renderer.append(child);
	}
	else
	{
	  renderer.append((Contact)contactBlock.get(i));
	}
      renderer.complete(width);
      return renderer;
    }
    else 
    {
      ParallelRenderer renderer = new ParallelRenderer();
      renderer.append((Contact)block);
      renderer.complete(width);
      return renderer;
    }
  }

}
