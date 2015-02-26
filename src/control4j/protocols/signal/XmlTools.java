package control4j.protocols.signal;

/*
 *  Copyright 2015 Jiri Lidinsky
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

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class XmlTools
{

  protected static SimpleDateFormat dateFormat
      = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

  public static String formatDate(Date date)
  {
    return dateFormat.format(date);
  }

  public static Date parseDate(String string) throws ParseException
  {
    return dateFormat.parse(string);
  }

}
