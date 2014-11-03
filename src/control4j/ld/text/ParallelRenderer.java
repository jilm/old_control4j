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
class ParallelRenderer extends RendererBase
{

  ParallelRenderer()
  {
    super();
  }

  void complete(int length)
  {
    alignCenter(length);
  }

  public void append(Contact contact)
  {
    if (ascii.size() == 0)
      init(contact);
    else
      addContact(contact);
  }

  public void append(RendererBase block)
  {
    if (ascii.size() == 0)
      copy(block);
    else
      addBlock(block);
  }

  private void init(Contact contact)
  {
    String name = contact.getName();
    String contactAscii = library.get(contact.getType());
    // center align name
    ascii.add(new StringBuilder(name));
    leftConnectors.add(Boolean.valueOf(false));
    rightConnectors.add(Boolean.valueOf(false));
    // center align contact
    ascii.add(new StringBuilder(contactAscii));
    leftConnectors.add(Boolean.valueOf(true));
    rightConnectors.add(Boolean.valueOf(true));
  }

  private void addContact(Contact contact)
  {
    // add a new contact
    StringBuilder name = new StringBuilder(contact.getName());
    StringBuilder contactAscii = new StringBuilder(library.get(contact.getType()));
    ascii.add(name);
    leftConnectors.add(Boolean.valueOf(false));
    rightConnectors.add(Boolean.valueOf(false));
    ascii.add(contactAscii);
    leftConnectors.add(Boolean.valueOf(true));
    rightConnectors.add(Boolean.valueOf(true));
  }

  private void addBlock(RendererBase block)
  {
    for (int i=0; i<block.ascii.size(); i++)
      ascii.add(new StringBuilder(block.ascii.get(i)));
    leftConnectors.addAll(block.leftConnectors);
    rightConnectors.addAll(block.rightConnectors);
  }

}
