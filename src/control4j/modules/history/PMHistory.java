package control4j.modules.history;

/*
 *  Copyright 2013, 2015 Jiri Lidinsky
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

import java.util.Date;
import control4j.Signal;
import control4j.ConfigItem;
import control4j.ProcessModule;
import control4j.IConfigBuffer;

/**
 *  Records and provides history of the input signals.
 */
public class PMHistory extends ProcessModule
{
  /**
   *  Desired history size. It must contain positive integer number which
   *  will be used to alocate the array for the history size. So this
   *  number specify the maximum number of samples that will be stored
   *  for each signal.
   */
  @ConfigItem(key="history-size", optional=false)
  public int historySize;

  private HistorySignal buffer;

  @Override
  public void initialize(IConfigBuffer configuration)
  {
    buffer = new HistorySignal(historySize);
  }

  /**
   *  It stores incoming data into circular array which is available on the
   *  output. It ignores timestamp of the signal and stores signals in the
   *  order they were received. Returned signals are vector ones where the
   *  latest (most recent) sample has the index zero.
   *
   *  If the input signal is not valid, the nan value will be placed to the
   *  history array.
   *
   *  WARNING: the returned signals are not persistent.
   *
   *  @param input
   *             array of size at least one. May not contain null values.
   *
   *  @return array of the same size as the input parameter. It returns
   *             vector signals which contain the history of the appropriate
   *             input signal.
   */
  @Override
  public void process(
      Signal[] input, int inputLength, Signal[] output, int outputLength)
  {
    buffer.add(input[0]);
    output[0] = buffer;
  }

}
