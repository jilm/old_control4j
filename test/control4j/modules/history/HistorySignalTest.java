package control4j.modules.history;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import control4j.Signal;

public class HistorySignalTest
{

  private HistorySignal signal1 = new HistorySignal(3);

  @Before
  public void initialize()
  {
    signal1.add(Signal.getSignal(5.2));
    signal1.add(Signal.getSignal(1.3));
    signal1.add(Signal.getSignal());
    signal1.add(Signal.getSignal(2.0));
  }

  @Test
  public void lengthTest()
  {
    assertEquals(3, signal1.getSize());
  }

  @Test
  public void getValueTest1()
  {
    assertEquals(2.0, signal1.getValue(), 1e-3);
  }

  @Test
  public void getValueTest2()
  {
    assertTrue(Double.isNaN(signal1.getValue(1)));
  }

  @Test
  public void getValueTest3()
  {
    assertEquals(1.3, signal1.getValue(2), 1e-3);
  }

  @Test
  public void validityTest()
  {
    assertTrue(signal1.isValid());
  }
}

