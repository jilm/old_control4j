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
import control4j.gui.components.VDU;

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;
import cz.lidinsky.tools.tree.ChangeableNode;
import cz.lidinsky.tools.tree.Node;

import org.apache.commons.collections4.Factory;

import java.awt.GridLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

@AVariableInput
public class IMSignalScreen extends InputModule
implements Factory<JComponent> {

  private ChangeableNode<GuiObject> screen;

  private VDU vdu;

  @Override
  public void initialize(Module definition) {
    try {
      // create and configure VDU component
      vdu = new VDU();
      for (Input input : definition.getInput()) {
        if (input.isConnected()) {
          vdu.addCell();
          vdu.setLabel(vdu.getCellCount() - 1, input.getSignal().getLabel() + input.getSignal().getUnit());
        }
      }
      // wrap the VDU component into some scereen
      screen = new ChangeableNode<GuiObject>();
      screen.setDecorated(new Screen());
      ChangeableNode<GuiObject> vduNode = new ChangeableNode<GuiObject>();
      vduNode.setDecorated(vdu);
      screen.addChild(vduNode);
      // add the screen into the gui
      Gui root = (Gui)ResourceManager.getInstance().getResource(
          new GuiResource());
      root.add(screen);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw new CommonException()
        .setCause(e);
      // TODO:
    }
  }

  public JComponent create() {
    return new JPanel();
  }

  @Override
  public void put(Signal[] input, int inputLength) {
    for (int i = 0; i < inputLength; i++) {
      if (input[i].isValid()) {
        vdu.setValue(i, input[i].getValue());
      } else {
        vdu.setValue(i, Double.NaN);
      }
    }
    vdu.update();
  }

}
