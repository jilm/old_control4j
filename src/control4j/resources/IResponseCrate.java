package control4j.resources;

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

import java.util.Date;

/**
 *  This interface is used to hand response to the requestor in
 *  request / response protocols. The response doesn't have to
 *  be available emediately after the request is sent. Implementation
 *  of this interface must be thread safe.
 */
public interface IResponseCrate<E>
{

  public E getResponse();

  public Exception getException();

  /**
   *  Returns true if the transaction is over. It means, that either the
   *  response is already available, or the exception was thrown. You
   *  should use this method if you don't want to block your process
   *  while waiting for response.
   *
   *  @return true if the transaction is over or false otherwise.
   */
  public boolean isFinished();

  /**
   *
   *  @return a time of the response
   */
  public Date getTimestamp();
}
