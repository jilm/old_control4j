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

package control4j.application.macro;

import static cz.lidinsky.tools.Validate.notNull;

import control4j.application.ErrorManager;
import control4j.application.ErrorRecord;
import cz.lidinsky.tools.xml.IXMLHandler;
import cz.lidinsky.tools.xml.XMLReader;
import cz.lidinsky.tools.xml.AXMLStartElement;
import cz.lidinsky.tools.xml.AXMLEndElement;
import cz.lidinsky.tools.xml.AXMLText;
import cz.lidinsky.tools.xml.AXMLDefaultUri;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.Predicate;

/**
 *
 *  Load macros from the xml document.
 *
 */
@AXMLDefaultUri("http://control4j.lidinsky.cz/macro")
public class XMLHandler implements IXMLHandler {

  /**
   *  An object which translates objects under this package into the
   *  objects from destination package.
   */
  private AbstractAdapter adapter;

  protected XMLReader reader;

  public void setXMLReader(XMLReader reader) {
    this.reader = notNull(reader);
  }

  /**
   *  An empty constructor.
   */
  public XMLHandler(AbstractAdapter adapter) {
    this.adapter = notNull(adapter);
  }

  //-------------------------------------------- Implementation of IXMLHandler.

  public void startProcessing() { }

  /**
   *  Cleen up.
   */
  public void endProcessing() { }

  //------------------------------------------------------ Signal Screen Macro.

  private SignalScreen signalScreen;

  /**
   *  A macro which creates a gui screen where the input signals will be
   *  shown.
   */
  @AXMLStartElement("signal-screen")
  public boolean startSignalScreen(Attributes attributes) {
    signalScreen = new SignalScreen();
    return true;
  }

  @AXMLEndElement("signal-screen")
  public boolean endSignalScreen() {
    adapter.put(signalScreen);
    signalScreen = null;
    return true;
  }

  @AXMLStartElement("signal-screen/tags")
  public boolean startSignalScreenTags(Attributes attributes) {
    return true;
  }

  @AXMLEndElement("signal-screen/tags")
  public boolean endSignalScreenTags() {
    return true;
  }

  @AXMLText("signal-screen/tags")
  public boolean textSignalScreenTags(String text) {
    String[] tags = text.split(" ");
    for (String tag : tags) {
      signalScreen.addTag(tag);
    }
    return true;
  }

}
