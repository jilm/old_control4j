package control4j.gui.edit;

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

import java.util.Map;
import java.util.Collection;
import java.lang.reflect.Constructor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JTree;
import javax.swing.JMenu;
import javax.swing.JComponent;
import javax.swing.tree.TreePath;
import control4j.gui.components.AbstractComponent;
import control4j.gui.components.AbstractPanel;
import control4j.gui.components.Screen;
import control4j.gui.changers.Changer;
import control4j.gui.changers.IChangeable;
import control4j.scanner.Scanner;
import control4j.scanner.Item;

/**
 *
 */
class LayoutHandling implements ActionListener
{

  private JTree tree;
  private TreeModel treeModel;

  public LayoutHandling(JTree tree)
  {
    this.tree = tree;
    this.treeModel = (TreeModel)tree.getModel();
  }

  /**
   *  Adds Layout menu at the end of the given menu bar.
   */
  void addMenu(Menu menu)
  {
    JMenu layoutMenu = new JMenu("Layout");
    layoutMenu.setMnemonic('L');
    menu.add(layoutMenu);
    menu.addItem("Full width", "LAYOUT_FULL_WIDTH", this, 'w');
    menu.addItem("Full height", "LAYOUT_FULL_HEIGHT", this, 'h');
    menu.addSeparator();
    menu.addItem("Align left", "LAYOUT_ALIGN_LEFT", this, 'l');
  }

  /**
   *  Response method for Edit menu items.
   */
  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equals("LAYOUT_FULL_WIDTH"))
      doFullWidth();
  }

  private void doFullWidth()
  {
  }

}
