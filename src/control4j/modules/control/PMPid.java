package control4j.modules.control;

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
import control4j.Control;
import control4j.ConfigItem;
import control4j.ProcessModule;

/**
 *  Implementation of the discrete PID controller.
 */
public class PMPid extends ProcessModule
{
  /**
   *  Proportional gain. Default value: 1.0.
   */
  @ConfigItem(optional = true)
  public double kp = 1.0;

  /**
   *  Intergral gail. Default value: 0.0.
   */
  @ConfigItem(optional = true)
  public double ki = 0.0;

  /**
   *  Derivative gain. Default value: 0.0.
   */
  @ConfigItem(optional = true)
  public double kd = 0.0;

  @ConfigItem(key="output-max", optional=true)
  public double outputMax = Double.POSITIVE_INFINITY;

  @ConfigItem(key="output-min", optional=true)
  public double outputMin = Double.NEGATIVE_INFINITY;

  private double previousError = 0.0;
  private double integral = 0.0;

  /**
   *  Implementation of PID controller.
   *
   *  <p>It expects tree inputs. Error, enable and reset. First one, the
   *  error input is obligatory. It is the diference between reference and
   *  measured value. The enable and reset inputs are optional.
   *
   *  <p>This controller has windup protection according to
   *  {@link http://www.controlguru.com/2008/021008.html}
   */
  @Override
  public Signal[] process(Signal[] input)
  {
    // reset input
    boolean reset = false;
    if (getNumberOfAssignedInputs() >= 3)
      if (input[2] != null && input[2].isValid())
        reset = input[2].getBoolean();
    // reset integral part
    if (reset) integral = 0.0;
    // enable input
    boolean enable = true;
    if (getNumberOfAssignedInputs() >= 2)
      if (input[1] != null && input[1].isValid())
        enable = input[1].getBoolean();
    // PID calculation
    if (input[0].isValid())
    {
      double dt = ((double)Control.getLastCycleDuration()) / 1000.0;
      double error = input[0].getValue();
      if (!reset && enable) integral += error * dt;
      double derivative = 0.0;
      if (dt > 0.0) derivative = (error - previousError) / dt;
      previousError = error;
      double result = kp*error + ki*integral + kd*derivative;
      // windup protection
      if (result > outputMax)
      {
	result = outputMax;
	if (Math.abs(ki) > 1e-6)
	  integral = (result - kp*error - kd*derivative) / ki;
      }
      else if (result < outputMin)
      {
	result = outputMin;
	if (Math.abs(ki) > 1e-6)
	  integral = (result - kp*error - kd*derivative) / ki;
      }
      return new Signal[] { Signal.getSignal(result) };
    }
    else
    {
      return new Signal[] { Signal.getSignal() };
    }
  }
}
