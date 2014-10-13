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
import control4j.ProcessModule;

/**
 *  Calculates average and variance from the input signals.
 */ 
public class PMStatistics extends ProcessModule
{

  /**
   *  Calculates basic statistics from the input signals. The returned array
   *  contains the following variables: 
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
  public Signal[] process(Signal[] input)
  {
    double sum = 0.0;
    double c = 0.0;
    int count = 0;
    double mean = 0.0; 
    
    // mean calculation
    int size = getNumberOfAssignedInputs();
    for (int i=0; i<size; i++)
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
      for (int i=0; i<size; i++)
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
    
    Signal[] result = new Signal[3];
    result[0] = Signal.getSignal((double)count);
    if (count > 0)
      result[1] = Signal.getSignal(mean);
    else
      result[1] = Signal.getSignal();
    if (count > 1)
      result[2] = Signal.getSignal(var_sum / (double)(count-1));
    else
      result[2] = Signal.getSignal();
    
    return result;
  }

}
