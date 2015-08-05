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
import control4j.gui.GuiObject;
import control4j.gui.components.Screen;
import control4j.application.Preprocessor;
import control4j.application.Module;
import control4j.application.Resource;
import control4j.application.Input;

import cz.lidinsky.tools.tree.Builder;
import cz.lidinsky.tools.tree.Node;

public class GuiToControlAdapter extends AbstractAdapter {

  private Preprocessor handler;

  private GuiResource resource;

  public GuiToControlAdapter(Preprocessor handler) {
    this.handler = handler;
    resource = new GuiResource();
  }

  @Override
  public void close() {
    if (!isEmpty) {
      handler.putResource(
          "_Gui_", handler.getScopePointer().getGlobal(), resource);
    }
  }

  //---------------------------------------------------------- Building a Tree.

  private Builder<GuiObject> treeBuilder = new Builder<GuiObject>();

  private boolean isEmpty = true;

  @Override
  public void open() {
    treeBuilder.open();
  }

  @Override
  public void close(GuiObject object) {
    Node<GuiObject> node = treeBuilder.close(object);
    setParents(node);
    if (object instanceof Screen) {
      resource.addScreenNode(node);
      isEmpty = false;
    } else if (object instanceof Changer) {
      Changer changer = (Changer)object;
      Module module = new ChangerModule(changer, handler.getScopePointer());
      Input input = new Input();
      module.putInput(0, input);
      module.putResource(null, resource);
      handler.addModule(module);
      handler.addModuleInput(
          changer.getSignalName(), handler.getScopePointer(), input);
    }
  }

  private void setParents(Node<GuiObject> node) {
    for (Node<GuiObject> child : node.getChildren()) {
      if (isChanger(child)) {
        getChanger(child).setParent(getVO(node));
      }
    }
  }

  private boolean isChanger(Node<GuiObject> node) {
    if (node == null) {
      return false;
    } else {
      return node.getDecorated() instanceof Changer;
    }
  }

  private Changer getChanger(Node<GuiObject> node) {
    if (node == null) {
      return null; // TODO:
    } else {
      return (Changer)node.getDecorated(); // TODO:
    }
  }

  private VisualObject getVO(Node<GuiObject> node) {
    if (node == null) {
      return null; // TODO:
    } else {
      return (VisualObject)node.getDecorated(); // TODO:
    }
  }


  public void put(Changer changer)
  {
    //Module module = new ChangerModule(changer, destination.getScopePointer());
    //Input input = new Input();
    // TODO:  place input
    //destination.addModule(module);
  }

}
