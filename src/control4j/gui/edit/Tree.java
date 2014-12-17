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

/**
 *
 */
public class Tree extends javax.swing.JTree
implements javax.swing.event.TableModelListener
{

  /**
   *
   */
  public Tree()
  {
    super();
  }

  /**
   *
   */
  public Tree(javax.swing.tree.TreeModel model)
  {
    super(model);
  }

  /**
   *
   */
  @Override
  public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
  {
    StringBuilder builder = new StringBuilder();
    // First character specifies the type of object
    if (value instanceof control4j.gui.Screens)
      return "Root";
    else if (value instanceof control4j.gui.VisualContainer)
      builder.append('P');
    else if (value instanceof control4j.gui.VisualObject)
      builder.append('C');
    else if (value instanceof control4j.gui.Changer)
      builder.append("Ch");
    else
      // should not happen
      assert false;
    // delimiter
    builder.append(':');
    // class name
    builder.append(value.getClass().getSimpleName());
    // delimiter
    builder.append(':');
    // name of the component, if supported
    builder.append(((control4j.gui.GuiObject)value).getName());
    return builder.toString();
  }

  /**
   *  Repaints a tree whenever table has changed.
   */
  public void tableChanged(javax.swing.event.TableModelEvent e)
  {
    repaint();
  }

}
