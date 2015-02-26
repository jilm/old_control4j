package control4j.protocols.signal;

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

import java.io.InputStream;
import java.io.OutputStream;

import control4j.protocols.tcp.RobustTcpTemplate;
import control4j.protocols.tcp.IInputStream;
import control4j.protocols.tcp.IOutputStream;

public class Client extends RobustTcpTemplate<Response, Request>
{

  public Client(String host, int port)
  {
    super(host, port);
  }

  @Override
  protected IInputStream<Response> getSpecificInputStream(InputStream is)
  {
    try {
    return new SignalResponseXmlInputStream(is);
    } catch (Exception e) { }; // TODO
    return null;
  }

  @Override
  protected IOutputStream<Request> getSpecificOutputStream(OutputStream os)
  {
    try {
    return new SignalRequestXmlOutputStream(os);
    } catch (Exception e) { }; // TODO
    return null;
  }

  @Override
  protected Request getEmptyRequest()
  {
    return new DataRequest();
  }

}
