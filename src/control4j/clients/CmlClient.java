package control4j.clients;

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
import control4j.SignalFormat;
import java.net.InetAddress;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.EOFException;

public class CmlClient
{
  public static void main(String[] args) throws Exception
  {
    String host = args[0];
    InetAddress address = InetAddress.getByName(host);
    Socket socket = new Socket(address, 51234);
    InputStream inputStream = socket.getInputStream();
    OutputStream outputStream = socket.getOutputStream();
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
    Request request = new Request();
    SignalFormat format = new SignalFormat();
    try
    {
      while (true)
      {
        objectOutputStream.writeObject(request);
        Signal[] response = (Signal[])objectInputStream.readObject();
        for (Signal signal : response)
        {
          System.out.println(signal.toString(format, " ", ""));
        }
      }
    }
    catch (EOFException e)
    {
      // server was closed, quit the program
    }
  }

  private static class Request implements Serializable
  {
  }
}
