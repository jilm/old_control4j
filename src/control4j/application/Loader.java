package control4j.application;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.NoSuchElementException;
import org.xml.sax.Attributes;

import cz.lidinsky.tools.xml.XMLReader;
import static control4j.tools.Logger.*;

/**
 *
 *  Loads and returns the whole application from a given source.
 *  The application may be composed of pars that are written in
 *  various languages. The loader uses appropriate loader for
 *  each part. The language is recongnized on the basis of a
 *  namespace. The map which assign loader to the namespace is
 *  read from a ...
 *
 */
public class Loader {

  private ILoader loader;

  private Application application = new Application();

  private Preprocessor handler;

  /**
   *  Reads a map: loader to namespace form a ...
   *
   *  @throws IOException
   *             if something went wrong with the configuration
   *             reading
   */
  public Loader(Preprocessor handler) {
    this.handler = handler;
  }

  /**
   *  Loads and returns an application from a given file.
   *
   *  @param file
   *             a file from which the application will be loaded
   *
   *  @throws IOException
   *             if something is wrong with the file
   */
  public void load(File file) throws IOException {

    // TODO:  read handlers from a config file
    control4j.application.nativelang.XMLHandler c4jHandler
        = new control4j.application.nativelang.XMLHandler();
    c4jHandler.setDestination(
        new control4j.application.nativelang.C4jToControlAdapter(handler));

    control4j.application.gui.XMLHandler guiHandler
        = new control4j.application.gui.XMLHandler(
            new control4j.application.gui.GuiToControlAdapter(
              handler));
    control4j.application.ld.XMLHandler ldHandler
      = new control4j.application.ld.XMLHandler();
    ldHandler.setHandler(
        new control4j.application.ld.Ld2ControlAdapter(handler));

    control4j.application.macro.XMLHandler macroHandler
      = new control4j.application.macro.XMLHandler(
          new control4j.application.macro.Macro2ControlAdapter(
            handler));

    XMLReader reader = new XMLReader();
    reader.addHandler(c4jHandler);
    reader.addHandler(guiHandler);
    reader.addHandler(ldHandler);
    reader.addHandler(macroHandler);
    reader.load(file);
    //return application;
  }

}
