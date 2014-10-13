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
import java.util.Date;

import control4j.ConfigItem;
import control4j.IConfigBuffer;
import control4j.resources.Resource;
import control4j.tools.Flag;
import control4j.tools.Queue;
import static control4j.tools.Logger.*;

public class MySql extends Resource 
{

  @ConfigItem(optional=true)
  public String host = "localhost";
  
  @ConfigItem(optional=true)
  public int port = 3306;
  
  @ConfigItem(optional=false)
  public String user;
  
  @ConfigItem(optional=false)
  public String password;
  
  @ConfigItem(key="database-name", optional=false)
  public String databaseName;
  
  private DatabaseConnection databaseConnection;
  private Queue<QueryCrate> buffer = new Queue<QueryCrate>();

  public void initialize(IConfigBuffer configuration)
  {
    databaseConnection = new DatabaseConnection();
    databaseConnection.start();
  }
  
  public void executeUpdate(String query)
  {
    QueryCrate queryCrate = QueryCrate.getUpdateCrate(query);
    buffer.queue(queryCrate);
  }
  
  public void formatString(String value, StringBuilder sb)
  {
    sb.append('"');
    sb.append(value);
    sb.append('"');
  }
  
  public void formatDouble(double value, StringBuilder sb)
  {
    sb.append(String.format("%e", value));
  }
  
  public void formatDate(Date value, StringBuilder sb)
  {
    sb.append(String.format("'%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS'", value));
  }
  
  private class DatabaseConnection extends Thread
  {
    private Connection connection = null;
  
    public DatabaseConnection()
    {
      super("MySql:DatabaseConnection");
    }
    
    @Override public void run()
    {
      try
      {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        while (true)
        {
          try
          {
            connection = DriverManager.getConnection(getUrl());
            while (true)
            {
              // get data to store
              QueryCrate query = buffer.blockingDequeue();
              // execute the query
              query.execute(connection.createStatement());
            }
          }
          catch (SQLException e)
          {
            if (connection != null)
              try { connection.close(); } catch (SQLException ex) {}
          }
        }    
      }
      catch (ClassNotFoundException e)
      {
        warning("MySQL driver is probably not installed!");
      }
      catch (InstantiationException e)
      {
      }
      catch (IllegalAccessException e)
      {
      }
    }

    private String getUrl()
    {
      StringBuilder url = new StringBuilder();
      url.append("jdbc:mysql://");
      url.append(host);
      url.append(':');
      url.append(port);
      url.append('/');
      url.append(databaseName);
      url.append("?user=");
      url.append(user);
      url.append("&password=");
      url.append(password);
      return url.toString();
    }

  }

}
