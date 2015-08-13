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

package control4j.modules.system;

import static cz.lidinsky.tools.Validate.notNull;

import control4j.AVariableInput;
import control4j.InputModule;
import control4j.ResourceManager;
import control4j.Signal;
import control4j.application.Input;
import control4j.application.Module;
import control4j.application.gui.Gui;
import control4j.application.gui.GuiResource;
import control4j.gui.GuiObject;
import control4j.gui.components.Box;
import control4j.gui.components.Screen;
import control4j.gui.components.Quantity;

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;
import cz.lidinsky.tools.tree.ChangeableNode;
import cz.lidinsky.tools.tree.Node;

@AVariableInput
public class IMSignalScreen extends InputModule {

  private ChangeableNode<GuiObject> screen;

  private Quantity[] quantities;

  @Override
  public void initialize(Module definition) {
    try {
      quantities = new Quantity[definition.getInput().size()];
      // create visual containers
      screen = new ChangeableNode<GuiObject>();
      screen.setDecorated(new Screen());
      ChangeableNode<GuiObject> box
        = (ChangeableNode<GuiObject>)screen.addChild(
            new ChangeableNode<GuiObject>());
      box.setDecorated(new Box());
      // create quantity objects
      int index = 0;
      for (Input input : definition.getInput()) {
        Node<GuiObject> quantityNode = createQuantity(input);
        box.addChild(quantityNode);
        quantities[index] = (Quantity)quantityNode.getDecorated();
        index++;
      }
      // add the screen into the gui
      Gui root = (Gui)ResourceManager.getInstance().getResource(
          new GuiResource());
      root.add(screen);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      System.exit(1);
      // TODO:
    }
  }

  @Override
  public void put(Signal[] input, int inputLength) {
    for (int i = 0; i < inputLength; i++) {
      if (input[i].isValid()) {
        quantities[i].setValue(input[i].getValue());
      } else {
        quantities[i].setValue(Double.NaN);
      }
    }
  }

  private Node<GuiObject> createQuantity(Input input) {
    Quantity quantity = new Quantity();
    quantity.setDigits(5); // TODO:
    Node<GuiObject> quantityNode = new ChangeableNode<GuiObject>();
    quantityNode.setDecorated(quantity);
    return quantityNode;
  }

}
