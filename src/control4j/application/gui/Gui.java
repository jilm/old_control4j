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

import control4j.IConfigBuffer;
import control4j.InputModule;
import control4j.Resource;
import control4j.Signal;
import control4j.gui.Changer;
import control4j.gui.GuiObject;
import control4j.gui.Screens;
import control4j.gui.VisualBuilder;
import control4j.gui.VisualObject;

import cz.lidinsky.tools.tree.ChangeableNode;
import cz.lidinsky.tools.tree.Node;

import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JComponent;

public class Gui extends Resource {

  private JFrame mainFrame;

  private Node<GuiObject> gui;

  @Override
  public void initialize(control4j.application.Resource definition) {
    gui = ((GuiResource)definition).getGui();
  }

  public Gui add(Node<GuiObject> screenNode) {
    if (gui == null) {
      gui = new ChangeableNode<GuiObject>();
      gui.setDecorated(new Screens());
    }
    ((ChangeableNode<GuiObject>)gui).addChild(screenNode);
    return this;
  }

  /**
   *  Show gui
   */
  @Override
  public void prepare() {
    super.prepare();
    // Create and set up the window
    mainFrame = new JFrame("Top level demo");
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.add(VisualBuilder.createVisualComponent(gui));
    VisualBuilder.configureVisualComponent(gui);
    // Show the main window
    javax.swing.SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          mainFrame.pack();
          mainFrame.setVisible(true);
        }
      }
    );
  }

  private class Updater implements Runnable
  {
    private Signal[] buffer;

    Updater(Signal[] signals, int size)
    {
      buffer = new Signal[size];
      System.arraycopy(signals, 0, buffer, 0, size);
    }

    public void run()
    {
    }
  }

  @Override
  public boolean isEquivalent(control4j.application.Resource definition) {
    try {
      Class _class = Class.forName(definition.getClassName());
      return this.getClass().isAssignableFrom(_class);
    } catch (ClassNotFoundException e) {
      // TODO:
      return false;
    }
  }

}
