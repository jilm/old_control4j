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

import control4j.gui.GuiObject;
import control4j.gui.Screens;

import cz.lidinsky.tools.tree.ChangeableNode;
import cz.lidinsky.tools.tree.Node;

public class GuiResource extends control4j.application.Resource {

  private ChangeableNode<GuiObject> gui;

  public GuiResource() {
    super("control4j.application.gui.Gui");
    this.gui = new ChangeableNode<GuiObject>();
    gui.setDecorated(new Screens());
  }

  public void addScreenNode(Node<GuiObject> node) {
    gui.addChild(node);
  }

  public Node<GuiObject> getGui() {
    return gui;
  }

}
