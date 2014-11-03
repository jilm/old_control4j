package control4j.ld.xml;

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

import control4j.tools.ISaxStatus;
import control4j.tools.SaxHandler;
import org.xml.sax.SAXException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import control4j.ld.LadderDiagram;

/**
 *  
 */
public class Loader
{
  public static LadderDiagram load(InputStream inputStream) 
  throws IOException, SAXException, ParserConfigurationException
  {
    RootStatus rootStatus = new RootStatus();
    XMLReader reader = XMLReaderFactory.createXMLReader();
    SAXParserFactory factory = SAXParserFactory.newInstance();
    //SAXParserFactory factory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
    SAXParser parser = factory.newSAXParser();
    //reader.setContentHandler(new SaxHandler(rootStatus));
    //reader.parse(new InputSource(inputStream));
    parser.parse(inputStream, new SaxHandler(rootStatus));
    return rootStatus.ld;
  }

  public static void main(String[] args) throws Exception
  {
    String fileName = args[0];
    InputStream inputStream = new FileInputStream(fileName);
    LadderDiagram ld = load(inputStream);
    System.out.println("rungs: " + ld.size());
  }
}
