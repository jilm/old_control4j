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

import control4j.AResource;
import control4j.InputModule;
import control4j.Signal;
import control4j.gui.Changer;
import control4j.application.Module;
import control4j.application.gui.Gui;

/**
 *  It is a module used by the system, do not use it directly!
 */
public class IMGuiChanger extends InputModule {

  protected Changer changer;

  @AResource
  public Gui gui;

  @Override
  public void initialize(Module definition) {
    super.initialize(definition);
    this.changer = ((ChangerModule)definition).getChanger();
  }

  @Override
  public void put(Signal[] input, int inputSize) {
    changer.update(input[0]); // TODO:
  }

}
