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

package control4j.modules;

import control4j.AResource;
import control4j.AVariableInput;
import control4j.InputModule;
import control4j.Signal;
import control4j.resources.communication.SignalServer;

/**
 *  Exports input signals through the given resource. This module is dedicated
 *  to provide data to another control4j instance, or to some external program
 *  or application.
 *
 *  <h3>Resources</h3>
 *  <table>
 *      <caption>Resources</caption>
 *      <tr>
 *          <td>server</td>
 *          <td>IServer</td>
 *          <td></td>
 *      </tr>
 *  </table>
 *
 *  <h3>IO</h3>
 *  <table>
 *      <caption>IO</caption>
 *      <tr>
 *          <td>Input</td>
 *          <td></td>
 *          <td>A signal which will be exported.</td>
 *      </tr>
 *  </table>
 *
 *  @see SignalServer
 */
@AVariableInput
public class IMExport extends InputModule {

  /** A server resource. */
  @AResource
  private SignalServer server;

  /** Names of the input signal that will be used as an identifier */
  protected String ids[];

  /**
   *  For identification purposes, the communicated signals are identified by
   *  the signal label. This method collects all of the labels into the ids
   *  field.
   */
  @Override
  public void initialize(control4j.application.Module declaration) {
    super.initialize(declaration);
    // get id's of the signals
    int size = declaration.getInput().size();
    ids = new String[size];
    for (int i=0; i<size; i++) {
      ids[i] = declaration.getInput().get(i).getSignal().getLabel();
    }
  }

  /**
   *  Sends input signals throught the given server resource.
   */
  @Override
  protected void put(Signal[] input, int inputLength) {
    for (int i = 0; i < inputLength; i++) {
      server.put(ids[i], input[i]);
    }
  }

}
