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

import static cz.lidinsky.tools.Validate.notNull;

import java.util.List;
import control4j.ld.LadderDiagram;
import control4j.ld.Rung;
import java.io.InputStream;
import java.io.FileInputStream;
//import control4j.ld.xml.Loader;
import control4j.application.ld.AbstractAdapter;
import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.xml.XMLReader;

/**
 *  Responsible for rendering the whole ladder diagram.
 */
public class Renderer extends AbstractAdapter {

  private int width = 80;

  public List<StringBuilder> Render(LadderDiagram ld) {
    return null;
  }

  public static void main(String[] args) throws Exception {

    control4j.application.nativelang.XMLHandler c4jHandler
        = new control4j.application.nativelang.XMLHandler();

    c4jHandler.setDestination(
        new control4j.application.nativelang.AbstractAdapter());

    control4j.application.gui.XMLHandler guiHandler
        = new control4j.application.gui.XMLHandler(
            new control4j.application.gui.AbstractAdapter());

    control4j.application.ld.XMLHandler ldHandler
      = new control4j.application.ld.XMLHandler();
    ldHandler.setHandler(new Renderer());

    XMLReader reader = new XMLReader();
    reader.addHandler(c4jHandler);
    reader.addHandler(guiHandler);
    reader.addHandler(ldHandler);

    String filename = args[0];
    java.io.File file = new java.io.File(filename);
    reader.load(file);

    //LadderDiagram ld = Loader.load(is);
    //is.close();
    //for (int i=0; i<ld.size(); i++)
    //{
      //List<StringBuilder> ascii = renderer.render(ld.get(i));
      //for (int j=0; j<ascii.size(); j++)
        //System.out.println(ascii.get(j).toString());
    //}
  }

  public void put(Rung rung) {
    try {
      RungRenderer renderer = new RungRenderer();
      List<StringBuilder> ascii = renderer.render(notNull(rung));
      for (int j=0; j<ascii.size(); j++) {
        System.out.println(ascii.get(j).toString());
      }
    } catch (Exception e) {
      throw new CommonException()
        .setCause(e)
        .set("message", "LD rendering failed!")
        .set("rung", rung);
    }
  }

  }
