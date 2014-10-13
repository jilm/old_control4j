package control4j.application.gui;

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

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import control4j.application.Property;
import control4j.application.Application;
import control4j.application.IApplicationReader;
import control4j.tools.DeclarationReference;

public class Reader implements IApplicationReader
{

  private Application application;

  public void load(InputStream inputStream, Application application, DeclarationReference fileReference) throws IOException
  {
    this.application = application;
    control4j.gui.Reader reader = new control4j.gui.Reader();
    reader.load(inputStream);
    application.setScreens(reader.get());
  }

}
