package control4j.gui;

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

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.BorderFactory;

public class ObjectPropertiesTableRenderer extends DefaultTableCellRenderer implements TableCellRenderer
{
  private Border unselectedBorder = null;
  private Border selectedBorder = null;

  public ObjectPropertiesTableRenderer()
  {
    super();
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
  {
    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if (value instanceof Number)
      setHorizontalAlignment(RIGHT);
    else
      setHorizontalAlignment(LEFT);
    if (isSelected)
    {
      if (selectedBorder == null)
        selectedBorder = BorderFactory.createMatteBorder(2,5,2,5, table.getSelectionBackground());
      //setBorder(selectedBorder);
    }
    else
    {
      
    }
    return this;
  }
}
