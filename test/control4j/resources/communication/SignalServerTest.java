package control4j.resources.communication;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import control4j.Signal;

public class SignalServerTest
{

  private SignalServer server;
  private SignalClient client;

  @Before
  public void initialize()
  {
    // prepare the server
    server = new SignalServer();
    client = new SignalClient();
    client.host = "localhost";

    server.prepare(); // start server
    client.prepare();
    // give some values into the server
    server.put("signal1", Signal.getSignal()); // invalid signal
    server.put("signal2", Signal.getSignal(54.21));
    // connect some client
    control4j.tools.Tools.sleep(200);
    server.cycleEnd(); // prepare data for transmission
    client.cycleEnd();
    control4j.tools.Tools.sleep(200);
  }

  @Test(timeout=2000)
  public void test1()
  {
    control4j.protocols.signal.DataResponse response = null;
    while (response == null) {
      response = client.read();
    }
    assertNotNull("Message was not received!", response);
    assertFalse(response.getData().get(0).isValid());
    assertEquals(54.21, response.getData().get(1).getValue());
  }

}
