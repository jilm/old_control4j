package control4j.modules;

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
import control4j.ConfigItem;
import control4j.ProcessModule;

/**
 *  Calculates average and variance from the history of the input signal.
 *  
 *  @see PMStatistics  
 */ 
public class PMSignalStatistics extends ProcessModule
{

  /**
   *  returned array contains following variables: 
   *  <ol>
   *    <li>Number of valid input signals. It means number of signals which 
   *        participate on calculation of the following values. This signal 
   *        is always valid.
   *    <li>Average. This signal is invalid if there is no valid input signal.
   *    <li>Variance. This signal is invalid if number of valid input signals 
   *        is less than two.
   *  </ol>
   *  
   *  <p>Timestamp of returned signals is set to the current system time.      
   *  
   *  <p>It uses <a href="http://en.wikipedia.org/wiki/Compensated_summation">
   *  Kahan</a> algorithm for sumation. For variance calculation it uses
   *  <a href="http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance">
   *  Two-pass algorithm.</a>        
   *
   *  @param input may contain any number of signals, but should contain at
   *         least one
   *               
   *  @return array of size three. The array contains count, average and 
   *          variance statistic calculated from the input data.   
   */        
  @Override
  public void process(
      Signal[] input, int inputLength, Signal[] output, int outputLength)
  {
    double sum = 0.0;
    double c = 0.0;
    int count = 0;
    double mean = 0.0; 
    
    // mean calculation
    for (int i=0; i<inputLength; i++)
    {
      if (input[i].isValid())
      {
        count++;
        double y = input[i].getValue() - c;
        double t = sum + y;
        c = (t - sum) - y;
        sum = t;
      }
    }
    if (count > 0) mean = sum / (double)count;
    
    // variance calculation
    double var_sum = 0.0;
    if (count > 1)
    {
      c = 0.0;
      for (int i=0; i<inputLength; i++)
      {
        if (input[i].isValid())
        {
          double x = (input[i].getValue()-mean)*(input[i].getValue()-mean);
          double y = x - c;
          double t = var_sum + y;
          c = (t - var_sum) - y;
          var_sum = t;
        }
      }
    }
    
    if (outputLength > 0)
      output[0] = Signal.getSignal((double)count);
    if (outputLength > 1)
    {
      if (count > 0)
        output[1] = Signal.getSignal(mean);
      else
        output[1] = Signal.getSignal();
    }
    if (outputLength > 2)
    {
      if (count > 1)
        output[2] = Signal.getSignal(var_sum / (double)(count-1));
      else
        output[2] = Signal.getSignal();
    }
    
  }

}
