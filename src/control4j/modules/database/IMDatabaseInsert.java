package control4j.modules.database;

/*
 *  Copyright 2013, 2014, 2015 Jiri Lidinsky
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

import control4j.Module;
import control4j.Signal;
import control4j.InputModule;
import control4j.ConfigItem;
import control4j.AResource;
import control4j.resources.database.MySql;

/**
 *  Inserts a record into the database.
 *
 *  <h3>Resources</h3>
 *  <table>
 *      <tr>
 *          <td>database</td>
 *          <td>MySql</td>
 *          <td>An object which provides access into the database.</td>
 *      </tr>
 *  </table>
 *
 *  <h3>Property</h3>
 *  <table>
 *      <tr>
 *          <td>table-name</td>
 *          <td>A name of the table to insert to. The string value is
 *          expected.</td>
 *      </tr>
 *  </table>
 *
 *  <h3>IO</h3>
 *  <table>
 *      <tr>
 *          <td>Input</td>
 *          <td>0</td>
 *          <td>The control input;<td>
 *      </tr>
 *  </table>
 */
public class IMDatabaseInsert extends InputModule
{
  @AResource
  public MySql database;

  @ConfigItem(key="table-name")
  public String tableName;

  private StringBuilder query = new StringBuilder();

  @Override
  public void put(Signal[] input, int inputLength)
  {
    int size = inputLength - 1;
    if (size <= 0) return;
    boolean enable = false;
    if (input[0] == null || (input[0].isValid() && input[0].getBoolean()))
      enable = true;
    if (enable)
    {
      // compose query
      query.delete(0, query.length());
      query.append("INSERT INTO ");
      query.append(tableName);
      query.append("(id, tstamp, validity, value, unit) VALUES ");
      for (int i=1; i<size; i++)
      {
        appendSignal(i, input[i], query);
        query.append(',');
      }
      appendSignal(size, input[size], query);
      query.append(';');
      database.executeUpdate(query.toString());
    }
  }

  private void appendSignal(int index, Signal signal, StringBuilder query)
  {
    query.append('(');
    database.formatString("TODO", query);
    query.append(',');
    database.formatDate(signal.getTimestamp(), query);
    query.append(',');
    if (signal.isValid())
    {
      query.append("0,");
      database.formatDouble(signal.getValue(), query);
    }
    else
    {
      query.append("1,0.0");
    }
    query.append(',');
    database.formatString(signal.getUnit(), query);
    query.append(')');
  }
}
