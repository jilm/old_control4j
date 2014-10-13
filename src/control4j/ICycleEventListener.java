package control4j;

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

/**
 *  This interface should be implemented by modules and resources, or 
 *  any other objects that need to be notified about cycle
 *  start and end events. If some module or resource implement
 *  this interface, it is automaticaly registered for notification
 *  during the application building.
 *
 *  @see ControlLoop
 */
public interface ICycleEventListener
{

  /**
   *  Method that is invoked after the new cycle starts. This
   *  method shoud be used by modules or resources that communicate
   *  with some hardware to send request for new input data.
   */
  public void cycleStart();

  /**
   *  Method that is invoked immediately before the first module
   *  is executed. It should be used to collect and process 
   *  input data for a new cycle.
   */
  public void processingStart();

  /**
   *  Method that is invoked immediately after the last module has 
   *  finished and before wait for a new cycle begins. This may be
   *  used by modules and resources that communicate with some
   *  output hardware to send output data.
   */
  public void cycleEnd();

}
