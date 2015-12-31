package control4j.modules;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import control4j.Signal;

public class IMExportTest
{

  private class Export extends IMExport {
    public Export() {
      ids = new String[2];
      ids[0] = "signal1";
      ids[1] = "signal2";
    }
  }

  private class Import extends OMImport {
    public Import() {
      ids = new String[2];
      ids[0] = "signal1";
      ids[1] = "signal2";
    }
  }

  private IMExport server;
  private OMImport client;

  @Before
  public void initialize()
  {
  }

  @Test(timeout=2000)
  public void test1()
  {
    // prepare the server
    server = new Export();
    server.setPort(56789);
    client = new Import();
    client.setHost("localhost");
    client.setPort(56789);
    
    System.err.println("--- Going to call prepare methods.");
    server.prepare(); // start server
    client.prepare();
    // connect some client
    control4j.tools.Tools.sleep(200);

    System.err.println("--- Going to call cycleStart methods.");
    server.cycleStart();
    client.cycleStart();
    control4j.tools.Tools.sleep(200);

    // give some values into the server
    Signal[] toExport
        = new Signal[] {Signal.getSignal(), Signal.getSignal(54.21)};
    server.put(toExport, 2); // invalid signal

    System.err.println("--- Going to get imported signals.");
    Signal[] imported = new Signal[2];
    client.get(imported, 2);
    assertFalse(imported[0].isValid());
    assertFalse(imported[1].isValid());
    
    System.err.println("--- Going to call cycleEnd methods.");
    server.cycleEnd(); // prepare data for transmission
    client.cycleEnd();
    control4j.tools.Tools.sleep(200);

    System.err.println("--- Going to call cycleStart methods.");
    server.cycleStart();
    client.cycleStart();
    control4j.tools.Tools.sleep(200);

    // give some values into the server
    toExport
        = new Signal[] {Signal.getSignal(), Signal.getSignal(54.21)};
    server.put(toExport, 2); // invalid signal

    System.err.println("--- Going to get imported signals.");
    imported = new Signal[2];
    client.get(imported, 2);
    assertFalse(imported[0].isValid());
    assertEquals(54.21, imported[1].getValue(), 1e-3);
  }

}
