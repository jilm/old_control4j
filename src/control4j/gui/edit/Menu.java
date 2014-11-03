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

import java.awt.event.ActionListener;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

class Menu extends JMenuBar
{

  public Menu()
  {
    super();
  }

  /**
   *  Adds new menu item at the end of the menu.
   */
  Menu addItem(String text, String actionCommand, ActionListener listener)
  {
    JMenuItem item = new JMenuItem(text);
    item.setActionCommand(actionCommand);
    item.addActionListener(listener);
    JMenu lastMenu = getMenu(getMenuCount()-1);
    lastMenu.add(item);
    return this;
  }

  Menu addItem(String text, String actionCommand, ActionListener listener, char mnemonic)
  {
    JMenuItem item = new JMenuItem(text);
    item.setActionCommand(actionCommand);
    item.addActionListener(listener);
    item.setMnemonic(mnemonic);
    JMenu lastMenu = getMenu(getMenuCount()-1);
    lastMenu.add(item);
    return this;
  }

  /**
   *  Adds a separator at the end of the menu.
   */
  Menu addSeparator()
  {
    JMenu lastMenu = getMenu(getMenuCount()-1);
    lastMenu.add(new JSeparator());
    return this;
  }
}
