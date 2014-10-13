package control4j.tools;

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
import java.io.IOException;

/**
 *  This interface is used to hand response to the requestor in
 *  request / response protocols. Implementation of this interface 
 *  must be thread safe.
 *
 *  <p>Instance of the object which implements this interface is
 *  used only for one transaction. After the transaction is finished,
 *  e.g. response is obtained, new object must be created for a new
 *  transaction.
 */
public interface IResponseCrate<E>
{

  /**
   *  Returns response to the request. This method block until the response
   *  is received. The exception is thrown if something went wrong.
   *
   *  @return a response to the request
   *
   *  @throws IOException 
   *              if something went wrong
   */
  public E getResponse() throws IOException;

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
   *  Returns a time which should be the best expression of the data
   *  creation. This information is used when the signals are created
   *  on the basis of the response. If you communicate with AD converter, 
   *  for example, this method should return a time of measurement. If
   *  such information is not available, it could be estimated based on
   *  time of response delivery.
   *
   *  <p>The value is available after the transaction is finished. Until 
   *  then it returns <code>null</code>.
   *
   *  <p>Don't call this method if the transaction ended up with exception.
   *  In such a case this method returns <code>null</code>.
   *
   *  @return a timestamp of the response
   */
  public Date getTimestamp();
}
