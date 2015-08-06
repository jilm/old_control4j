package control4j.application.ld;

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

import control4j.ld.Rung;
import control4j.ld.LadderDiagram;

/**
 *  Empty template for LD handlers. Object descendans are used as handlers
 *  for the XMLHandler object.
 *
 */
public class AbstractAdapter {

  /**
   *  It is called whenever the XML parser finds a ld element. May be called
   *  more than onece within the application. The default implementation does
   *  nothing
   */
  public void startLd() {}

  /**
   *  It is called when the XML parser hit ld end element. The default
   *  implementation does nothing.
   */
  public void endLd() {}

  /**
   *  It is called when the rung was loaded. The default implementation does
   *  nothing.
   *
   *  @param rung
   *             loaded rung. The object is complete
   */
  public void put(Rung rung) {}

  /**
   *  It is called when the whole ld was loaded. The default implementation
   *  does nothing.
   *
   *  @param ld
   *             loaded ld
   */
  public void put(LadderDiagram ld) {}

}
