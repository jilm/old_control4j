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

import java.util.List;
import control4j.ld.Coil;
import control4j.ld.Rung;
import control4j.ld.Contact;

class RungRenderer
{

  private int length = 70;
  private int coilLength = 15;

  List<StringBuilder> render(Rung rung)
  {
    // render coil block
    RendererBase coilBlock = renderCoilBlock(rung, coilLength);
    // render contact block
    RendererBase contactBlock = ContactBlockRenderer.render(rung.getContactBlock(), length - 4 - coilBlock.getMaxLength());
    // connect it together
    contactBlock.addBar();
    contactBlock.ascii.get(1).append("--");
    for (int i=2; i<contactBlock.rightConnectors.size(); i++)
      contactBlock.rightConnectors.set(i, Boolean.valueOf(false));
    SerialRenderer renderer = new SerialRenderer();
    renderer.append(contactBlock);
    renderer.append(coilBlock);
    renderer.complete();
    // add left and right bar
    renderer.ascii.add(new StringBuilder());
    renderer.leftConnectors.add(Boolean.valueOf(false));
    renderer.rightConnectors.add(Boolean.valueOf(false));
    addBar(renderer);
    // return result
    return renderer.ascii;
  }

  private RendererBase renderCoilBlock(Rung rung, int length)
  {
    ParallelRenderer renderer = new ParallelRenderer();
    Contact tempContact = new Contact();
    for (int i=0; i<rung.coilBlockSize(); i++)
    {
      Coil coil = rung.getCoil(i);
      tempContact.setName(coil.getName());
      tempContact.setType(coil.getType());
      renderer.append(tempContact);
    }
    renderer.complete(length);
    return renderer;
  }

  private void addBar(RendererBase block)
  {
    block.alignRight();
    for (int i=0; i<block.ascii.size(); i++)
    {
      char barChar = block.leftConnectors.get(i).booleanValue() ? '+' : '|';
      block.ascii.get(i).insert(0, barChar);
      barChar = block.rightConnectors.get(i).booleanValue() ? '+' : '|';
      block.ascii.get(i).append(barChar);
    }
  }

}
