package control4j.ld.text;

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

import java.util.HashMap;
import java.util.Properties;
import java.util.Enumeration;
import java.io.InputStream;
import java.io.FileInputStream;

/**
 *  Return representation of the atomic components such as contacts
 *  and coils.
 *
 *  This ascii representation is loaded from a file.
 */
class Library
{

  private Properties defaults;
  private Properties properties;
  private static Library singleton = null;
  private String filename = "control4j/asciirepresentation.properties";

  private Library()
  {
    // preset default values
    defaults = new Properties();
    defaults.put("XIC", "--] [--");
    defaults.put("XIO", "--]/[--");
    defaults.put("OTE", "--( )--");
    defaults.put("OTL", "--(S)--");
    defaults.put("OTU", "--(R)--");
    defaults.put("OSR", "--[OSR]--");
    defaults.put("CTU", "--(CTU)--");
    defaults.put("CTD", "--(CTD)--");
    defaults.put("RES", "--(RES)--");
    defaults.put("TON", "--(TON)--");
    defaults.put("TOF", "--(TOF)--");
    defaults.put("RTO", "--(RTO)--");
    // load ascii representation from a file
    properties = new Properties();
    try
    {
      InputStream inputStream = new FileInputStream(filename);
      properties.load(inputStream);
      inputStream.close();
    }
    catch (Exception e)
    {
      properties = defaults;
    }
  }

  public static Library getInstance()
  {
    if (singleton == null) singleton = new Library();
    return singleton;
  }

  String get(String type)
  {
    return (String)properties.get(type);
  }

}
