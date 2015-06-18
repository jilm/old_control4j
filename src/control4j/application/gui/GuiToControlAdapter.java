package control4j.application.gui;

/*
 *  Copyright 2015 Jiri Lidinsky
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

import control4j.gui.Screens;
import control4j.gui.VisualObject;
import control4j.gui.VisualContainer;
import control4j.gui.Changer;
import control4j.application.Application;
import control4j.application.Module;
import control4j.application.Resource;
import control4j.application.Input;

public class GuiToControlAdapter extends AbstractAdapter
{

  public GuiToControlAdapter(control4j.application.Application destination) {
    if (destination instanceof control4j.application.Application) {
    } else {
      throw new UnsupportedOperationException();
    }
  }

  protected Application destination;


  public void put(Screens screens)
  {
    Resource resource = new GuiResource(screens);
    //destination.addResource(resource);
  }

  public void put(VisualObject object)
  {
  }

  public void put(VisualContainer container)
  {
  }

  public void put(Changer changer)
  {
    Module module = new ChangerModule(changer, destination.getScopePointer());
    Input input = new Input();
    // TODO:  place input
    destination.addModule(module);
  }

}
