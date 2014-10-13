package control4j.tools;

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

import java.util.ResourceBundle;

/**
 *  Auxiliary static class that reads logging messages from ResourceBundle
 *  "LogMessages" and make basic formating.
 */  
public class LogMessages
{
  private static final ResourceBundle logMessages = ResourceBundle.getBundle("control4j/messages");
  
  private LogMessages() {}
  
  public static String getMessage(String key)
  {
    return logMessages.getString(key);
  }
  
  public static String getMessage(String key, String param)
  {
    String message = logMessages.getString(key);
    return String.format(message, param);
  }
  
  public static String getMessage(String key, String param1, String param2)
  {
    String message = logMessages.getString(key);
    return String.format(message, param1, param2);
  }

  public static String getMessage(String key, int param)
  {
    String message = logMessages.getString(key);
    return String.format(message, param);
  }


}
