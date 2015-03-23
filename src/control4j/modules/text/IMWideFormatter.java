package control4j.modules.text;

/*
 *  Copyright 2013, 2015 Jiri Lidinsky
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

import java.util.Locale;
import control4j.Signal;
import control4j.Resource;
import control4j.ConfigItem;
import control4j.InputModule;
import control4j.SignalFormat;
import control4j.IConfigBuffer;
import control4j.resources.ITextWriter;

/**
 *  Prints input signals in a human readable and wide form on 
 *  the given text device. Text device may be a file or just text 
 *  console. All of the signal values are printed on one line.
 */
public class IMWideFormatter extends InputModule
{
  /**
   *  Text device on which it will be printed.
   */
  @Resource(key="text-device")
  public ITextWriter textDevice;

  /**
   *  A string which is used to separate particular data.
   *  Default value is a space.
   */
  @ConfigItem(optional=true)
  public String delimiter = " ";

  /**
   *  A valid ISO Language Code. These codes are the lower-case, 
   *  two-letter codes as defined by ISO-639 (en or cz for example). 
   *  You can find a full list of these codes at a number of sites, 
   *  such as 
   *  <a href="http://www.loc.gov/standards/iso639-2/php/English_list.php">
   *  here</a>
   *  Default value is taken from system settings of the computer.
   *  
   *  @see #initialize
   */
  @ConfigItem(optional=true)
  public String language = null;

  /**
   *  A valid ISO Country Code. These codes are the upper-case, 
   *  two-letter codes as defined by ISO-3166 (CZ or US for example). 
   *  You can find a full list of these codes at a number of sites, such as:
   *  <a href="http://www.iso.ch/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/list-en1.html">here</a>
   *  Default value is taken from system settings.
   *
   *  @see #initialize
   */
  @ConfigItem(optional=true)
  public String country = null;

  /**
   *  Maximum fraction digits for decimal numbers.
   *  Default value is two.
   */
  @ConfigItem(key="max-fraction-digits", optional=true)
  public int maxFractionDigits = 2;

  /**
   *  Controls the print of the header with the names of signals.
   *  Default is true (header is printed).
   */
  @ConfigItem(optional=true)
  public boolean header = true;

  private SignalFormat signalFormat;
  private StringBuilder stringBuilder = new StringBuilder();

  /**
   *  Initialize the formatter. It uses variables: language, country
   *  and maxFractionDigits to create and initialize locale for
   *  the formatter. If language and country variables are null
   *  (not used) it uses default system locale settings of the
   *  computer. If you want to set country, you must also set
   *  the language property.
   *
   *  @param configuration
   *             not used, may be null
   *  @see java.util.Locale
   */
  @Override
  protected void initialize(IConfigBuffer configuration) 
  {
    super.initialize(configuration);
    Locale locale;
    if (language == null)
      locale = Locale.getDefault();
    else if (country == null)
      locale = new Locale(language);
    else
      locale = new Locale(language, country);
    signalFormat = new SignalFormat(locale);
    signalFormat.setMaximumFractionDigits(maxFractionDigits);
  }
  
  /**
   *  Print input signal values on the text device in the human readable
   *  form. All of the signal values are printed on one line. First input
   *  (input with index zero) serves as an enable input. Signals
   *  whose index starts from one are printed only if the enable
   *  input is valid and true, or if this input is not used (null).
   *  Otherwise the print is disabled.
   *
   *  <p>Each line starts with date and time. It is the timestamp
   *  of the signal with index one. Timestamp is followed by
   *  all of the signal values whose index is greater than zero.
   *  Values are separated by <code>delimiter</code>.
   *  
   *  <p>Before the first line with values is printed, the header
   *  may appear on the text device. The header contains names of
   *  the signals separated by the <code>delimiter</code>. Header
   *  function is suppressed if the config item <code>header</code>
   *  is set to false.
   *  
   *  @param input
   *             signal with index zero is interpreted as boolean.
   *             Function of this module is enabled only if this
   *             signal is null (not used) or if it is valid and
   *             true; function is disabled otherwise. Values
   *             of signals whose index starts from one will be
   *             printed on text device. These cannot be null.
   */
  @Override
  public void put(Signal[] input, int inputLength)
  {
    if (input[0] == null || (input[0].isValid() && input[0].getBoolean()))
    {
      if (header) printHeader(inputLength);

      stringBuilder.delete(0, stringBuilder.length());
      stringBuilder.append(signalFormat.dateFormat(input[1].getTimestamp()));
      stringBuilder.append(delimiter);
      stringBuilder.append(signalFormat.timeFormat(input[1].getTimestamp()));
      for (int i=1; i<inputLength; i++)
      {
        stringBuilder.append(delimiter);
	stringBuilder.append(input[i].valueToString(signalFormat));
      }
      textDevice.println(stringBuilder.toString());
    }
  }

  private void printHeader(int size)
  {
    stringBuilder.delete(0, stringBuilder.length());
    stringBuilder.append("date");
    stringBuilder.append(delimiter);
    stringBuilder.append("time");
    for (int i=1; i<size; i++)
    {
      stringBuilder.append(delimiter);
      stringBuilder.append("???"); 
    }
    textDevice.println(stringBuilder.toString());
    header = false;
  }
						    
}
