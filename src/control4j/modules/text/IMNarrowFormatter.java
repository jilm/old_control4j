package control4j.modules.text;

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

import java.util.Locale;

import control4j.AMaxInput;
import control4j.AVariableInput;
import control4j.Signal;
import control4j.AResource;
//import control4j.ConfigItem;
import control4j.InputModule;
import control4j.SignalFormat;
import control4j.IConfigBuffer;
import control4j.ConfigItemNotFoundException;
import control4j.resources.ITextWriter;

import cz.lidinsky.tools.IToStringBuildable;
import cz.lidinsky.tools.ToStringBuilder;
import cz.lidinsky.tools.reflect.Setter;

/**
 *
 *  Prints values of input signals in a human readable form on
 *  the given text device. Text device may be a file or just a
 *  console. Value of each signal is printed on separate line
 *  together with timestamp and signal name.
 *
 */
@AMaxInput(1)
@AVariableInput
public class IMNarrowFormatter extends InputModule
implements IToStringBuildable
{

  /**
   *  Text device on which it will be printed.
   */
  @AResource
  public ITextWriter textDevice;

  /**
   *  A string which is used to separate particular data.
   *  Default value is a space.
   */
  @Setter("delimiter")
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
  @Setter("language")
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
  @Setter("country")
  public String country = null;

  /**
   *  Maximum fraction digits for decimal numbers.
   *  Default value is two.
   */
  @Setter("max-fraction-digits")
  public int maxFractionDigits = 2;

  private SignalFormat signalFormat;

  private String[] labels;

  /**
   *  Initialize the formatter. It uses variables: language, country
   *  and maxFractionDigits to create and initialize locale for
   *  the formatter. If language and country variables are null
   *  (not used) it uses default system locale settings of the
   *  computer. If you want to set country, you must also set
   *  the language property.
   *
   *  @param configuration
   *             module configuration
   *
   *  @see java.util.Locale
   */
  @Override
  public void initialize(control4j.application.Module definition) {
    super.initialize(definition);
    // initialize input labels
    int inputs = definition.getInputSize() - 1;
    labels = new String[inputs];
    for (int i = 0; i < inputs; i++) {
      try {
        labels[i]
            = definition.getInput(i + 1).getConfiguration().getString("label");
      } catch (ConfigItemNotFoundException e) {
        labels[i] = definition.getInput(i + 1).getHref();
      }
    }
    //
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
   *  Prints input signals on the text device in the human readable
   *  form. Signals are printed on separate lines. First input
   *  (input with index zero) serves as a enable input. Signals
   *  which index starts from one are printed only if the enable
   *  input is valid and true, or if this input is not used (null).
   *  Otherwise the print is disabled.
   *
   *  @param input
   *             signal with index zero is interpreted as boolean.
   *             Function of this module is enabled only if this
   *             signal is null (not used) or if it is valid and
   *             true; function is disabled otherwise. Values
   *             of signals which index starts from one will be
   *             printed on text device. These cannot be null.
   */
  @Override
  public void put(Signal[] input, int inputLength) {
    if (input[0] == null || (input[0].isValid() && input[0].getBoolean())) {
      for (int i=1; i<inputLength; i++) {
        textDevice.println(input[i].toString(
            signalFormat, delimiter, labels[i - 1]));
      }
    }
  }

  public void toString(ToStringBuilder builder) {
    builder.append("textDevice", textDevice)
           .append("delimiter", delimiter)
           .append("language", language)
           .append("country", country)
           .append("maxFractionDigits", maxFractionDigits)
           .append("signalFormat", signalFormat)
           .append("labels", labels);
  }
}
