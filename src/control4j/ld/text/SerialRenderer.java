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
class SerialRenderer extends RendererBase
{

  SerialRenderer()
  {
    super();
  }

  SerialRenderer(RendererBase base)
  {
    super(base);
  }

  void complete()
  {
    alignRight();
  }

  public void append(Contact contact, int length)
  {
    if (ascii.size() == 0)
      init(contact, length);
    else
      addContact(contact, length);
  }

  public void append(RendererBase block)
  {
    if (ascii.size() == 0)
      copy(block);
    else
      addBlock(block);
  }

  private void init(Contact contact, int extraLength)
  {
    // extra space
    int halfExtra = extraLength / 2;
    // get contact name and ascii representation
    StringBuilder name = new StringBuilder(contact.getName());
    StringBuilder contactAscii = new StringBuilder(library.get(contact.getType()));
    // center align name
    ascii.add(name);
    leftConnectors.add(Boolean.valueOf(false));
    rightConnectors.add(Boolean.valueOf(false));
    int number = (contactAscii.length() - name.length()) / 2;
    fillLeft(0, number);
    fillLeft(0, halfExtra);
    fillRight(0, extraLength - halfExtra + ascii.get(0).length());
    // center align contact
    ascii.add(contactAscii);
    leftConnectors.add(Boolean.valueOf(true));
    rightConnectors.add(Boolean.valueOf(true));
    number *= -1;
    fillLeft(1, number);
    fillLeft(1, halfExtra);
    fillRight(1, extraLength - halfExtra + ascii.get(1).length());
  }

  /**
   *  Adds an ascii representation of the contact into the ascii buffer.
   *  The contact length will be length.
   */
  private void addContact(Contact contact, int extraLength)
  {
    addBar();
    // add extra space
    int halfExtra = extraLength / 2;
    fillRight(0, halfExtra + ascii.get(0).length());
    fillRight(1, halfExtra + ascii.get(1).length());
    // add a new contact
    String name = contact.getName();
    String contactAscii = library.get(contact.getType());
    // center align name
    int fill = ascii.get(1).length() + contactAscii.length()/2 - ascii.get(0).length() - name.length()/2;
    if (fill > 0) fillRight(0, fill+ascii.get(0).length());
    ascii.get(0).append(name);
    // center align contact
    fill *= -1;
    if (fill > 0) fillRight(1, fill+ascii.get(1).length());
    ascii.get(1).append(contactAscii);
    for (int i=2; i<rightConnectors.size(); i++)
      rightConnectors.set(i, Boolean.valueOf(false));
    // add extra space
    fillRight(0, extraLength - halfExtra + ascii.get(0).length());
    fillRight(1, extraLength - halfExtra + ascii.get(1).length());
  }

  private void addBlock(RendererBase block)
  {
    ensureCapacity(block.ascii.size());
    addBar(block.leftConnectors);
    for (int i=0; i<block.ascii.size(); i++)
    {
      ascii.get(i).append(block.ascii.get(i));
      rightConnectors.set(i, block.rightConnectors.get(i));
    }
    for (int i=block.rightConnectors.size(); i<rightConnectors.size(); i++)
      rightConnectors.set(i, Boolean.valueOf(false));
  }

}
