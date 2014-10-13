package control4j.modules.io;

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

import control4j.Signal;
import control4j.Resource;
import control4j.InputModule;
import control4j.IConfigBuffer;
import control4j.UnsupportedNumberOfIOException;
import control4j.resources.IBinaryOutput;

/**
 *  Sets outputs of the binary output hardware.
 */
public class IMBinaryOutput extends InputModule
{

  /**
   *  Binary output resource.
   */
  @Resource
  public IBinaryOutput hardware;

  @Override
  protected void initialize(IConfigBuffer configuration)
  {
    super.initialize(configuration);
    if (getNumberOfAssignedInputs() > hardware.getNumberOfBinaryOutputs())
      throw new UnsupportedNumberOfIOException();
  }

  /**
   */
  public void put(Signal[] input)
  {
    int size = getNumberOfAssignedInputs();
    for (int i=0; i<size; i++)
      if (input[i] != null && input[i].isValid())
        hardware.setBinaryOutput(i, input[i].getBoolean());
  }
}
