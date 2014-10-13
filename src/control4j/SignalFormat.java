package control4j;

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

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

/**
 *  Is used to customize string representation of the
 *  Signals objects.
 *
 *  @see Signal
 */
public class SignalFormat
{
  private DateFormat dateFormat;
  private DateFormat timeFormat;
  private NumberFormat numberFormat;

  /**
   *  Initialization is based on current system locales.
   */
  public SignalFormat()
  {
    dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
    timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
    numberFormat = NumberFormat.getInstance();
  }

  /**
   *  Particular formatters are initialized on the given 
   *  locale.
   *
   *  @param locale locale which will be used to format
   *         Signal properties
   */
  public SignalFormat(Locale locale)
  {
    dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
    timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
    numberFormat = NumberFormat.getInstance(locale);
  }

  /**
   *  Set maximum number of fraction digits for
   *  string representation of real numbers.
   *
   *  @param value desired number of fraction digits,
   *         must be non negative number
   */
  public void setMaximumFractionDigits(int value)
  {
    numberFormat.setMaximumFractionDigits(value);
  }

  /**
   *  Return string representation of the date part
   *  of the <code>timestamp</code> parameter. This
   *  methods is designed primarily for timestamp
   *  formatting.
   *
   *  @param timestamp date whose string representation
   *         is returned
   *  @return string representation of the date part
   *         of the given parameter
   */
  public String dateFormat(Date timestamp)
  {
    return dateFormat.format(timestamp);
  }

  /**
   *  Return string representation of the time part
   *  of the <code>timestamp</code> parameter. It is
   *  designed primarily for timestamp formatting.
   *
   *  @param timestamp date whose string representation
   *         is returned
   *  @return string representation of the time part of
   *         the given parameter
   */
  public String timeFormat(Date timestamp)
  {
    return timeFormat.format(timestamp);
  }

  /**
   *  Return string representation of the <code>value</code>
   *  parameter. Value is formated as real number.
   *
   *  @param value number whose string representation is
   *         returned
   *  @return a string representation of the parameter
   */
  public String format(double value)
  {
    return numberFormat.format(value);
  }

  /**
   *  Depending on parameter <code>value</code> returns "1" for true
   *  and "0" for false.
   *  
   *  @param value a parameter whose string representation will be
   *         returned
   *  @return "1" if value is true, "0" otherwise
   */
  public String format(boolean value)
  {
    return value ? "1" : "0";
  }

}
