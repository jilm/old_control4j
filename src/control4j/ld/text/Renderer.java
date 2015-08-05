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

import java.util.List;
import control4j.ld.LadderDiagram;
import java.io.InputStream;
import java.io.FileInputStream;
//import control4j.ld.xml.Loader;

/**
 *  Responsible for rendering the whole ladder diagram.
 */
public class Renderer
{

  private int width = 80;

  public List<StringBuilder> Render(LadderDiagram ld)
  {
    return null;
  }

  public static void main(String[] args) throws Exception
  {
    String filename = args[0];
    InputStream is = new FileInputStream(filename);
    //LadderDiagram ld = Loader.load(is);
    is.close();
    RungRenderer renderer = new RungRenderer();
    //for (int i=0; i<ld.size(); i++)
    //{
      //List<StringBuilder> ascii = renderer.render(ld.get(i));
      //for (int j=0; j<ascii.size(); j++)
        //System.out.println(ascii.get(j).toString());
    //}
  }

}
