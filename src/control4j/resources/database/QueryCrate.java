package control4j.resources.database;

/*
 *  Copyright 2013 Jiri Lidinsky
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

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import control4j.tools.Flag;
import control4j.tools.Queue;
import static control4j.tools.Logger.*;

public abstract class QueryCrate 
{
  private String query;
  protected SQLException exception;
  protected Flag finished = new Flag(false);

  private QueryCrate(String query)
  {
    this.query = query;
  }

  static QueryCrate getUpdateCrate(String query)
  {
    return new UpdateQueryCrate(query);
  }

  public String getQuery()
  {
    return this.query;
  }
  
  public synchronized SQLException getException()
  {
    return getException();
  }
  
  abstract void execute(Statement statement);
  
  public boolean isFinished()
  {
    return finished.isSet();
  }
  
  public abstract void close();
  
  private static class UpdateQueryCrate extends QueryCrate
  {
    UpdateQueryCrate(String query)
    {
      super(query);
    }
    
    @Override synchronized void execute(Statement statement)
    {
      try
      {
        statement.executeUpdate(getQuery());
      }
      catch (SQLException e)
      {
        this.exception = e;
      }
      finished.set();
      try { statement.close(); } catch (SQLException e) {}
    }
    
    @Override public void close()
    {
    }

  }
  
}
