package control4j.scanner;

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
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.table.TableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 *  Model for table that is cappable to show results of scanner.
 *  The table has only two columns, named key and value.
 *  The datatype of each cell in value column don't have to be the
 *  same.
 */
public class KeyValueTableModel implements TableModel
{

  /**
   *  List of event listeners
   */
  private LinkedList<TableModelListener> listeners
    = new LinkedList<TableModelListener>();

  /**
   *  The data
   */
  private ArrayList<Item> data = new ArrayList<Item>();

  /**
   *  Sets new data to be shown in the table. Previously shown
   *  data are removed.
   *
   *  @param data
   *             the data to be shown
   */
  public void setData(Map<String, Item> data)
  {
    this.data.clear();
    if (data != null) this.data.addAll(data.values());
    for (TableModelListener listener : listeners)
      listener.tableChanged(new TableModelEvent(this));
  }

  public void addTableModelListener(TableModelListener l)
  {
    if (l != null) listeners.add(l);
  }

  /**
   *  Returns the data type of the column. For index 0 it returns String,
   *  and for index 1 it returns Object. If you need the datatype more
   *  specificaly, call the method getCellClass.
   *
   *  @param columnIndex
   *             index of the column, may be 0 or 1
   *
   *  @see #getCellClass
   */
  public Class<?> getColumnClass(int columnIndex)
  {
    if (columnIndex == 0)
      return String.class;
    else
      return Object.class;
  }

  /**
   *  Return datatype for the specific cell.
   *
   *  @param row
   *             index of the row
   *
   *  @param column
   *             index of the column, may be 0 ro 1
   */
  public Class<?> getCellClass(int row, int column)
  {
    if (column == 0)
      return String.class;
    else
      return data.get(row).getValueClass();
  }

  /**
   *  Always returns number 2.
   */
  public int getColumnCount()
  {
    return 2;
  }

  /**
   *  Returns names "Key" and "Value".
   */
  public String getColumnName(int columnIndex)
  {
    if (columnIndex == 0)
      return "Key";
    else
      return "Value";
  }

  public int getRowCount()
  {
    if (data == null)
      return 0;
    else
      return data.size();
  }

  public Object getValueAt(int rowIndex, int columnIndex)
  {
    if (columnIndex == 0)
      return data.get(rowIndex).getKey();
    else
      return data.get(rowIndex).getValue();
  }

  /**
   *  Returns true, if there is a Setter method defined for the key.
   *  else returns false.
   */
  public boolean isCellEditable(int rowIndex, int columnIndex)
  {
    if (columnIndex == 0)
      return false;
    else
      return data.get(rowIndex).isWritable();
  }

  public void removeTableModelListener(TableModelListener l)
  {
    if (l != null) listeners.remove(l);
  }

  /**
   *  @throws IllegalArgumentException
   *             if the Setter method invocation fails
   */
  public void setValueAt(Object aValue, int rowIndex, int columnIndex)
  {
    try
    {
      data.get(rowIndex).setValue(aValue);
    }
    catch (java.lang.reflect.InvocationTargetException e)
    {
      throw new IllegalArgumentException(e.getTargetException().getMessage());
    }
    update();
  }

  protected void update()
  {
    try
    {
      for (Item item : data) item.update();
    }
    catch (java.lang.reflect.InvocationTargetException e) { }
    catch (java.lang.IllegalAccessException e) { }
    fireTableChanged();
  }

  protected void fireTableChanged()
  {
    TableModelEvent event = new TableModelEvent(this, 0, data.size()-1, 1);
    for (TableModelListener listener : listeners)
      listener.tableChanged(event);
  }

}
