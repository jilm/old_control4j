package control4j.modules.math;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import control4j.Signal;

public class PMDivTest
{

  private PMDiv div = new PMDiv();
  private Signal signal1;
  private Signal signal2;
  private Signal invalidSignal;
  private Signal zeroSignal;
  private Signal[] buffer = new Signal[5];

  @Before
  public void initialize()
  {
    signal1 = Signal.getSignal(12.0);
    signal2 = Signal.getSignal(3.0);
    invalidSignal = Signal.getSignal();
    zeroSignal = Signal.getSignal(0.0);
  }

  @Test
  public void test1()
  {
    buffer[0] = signal1;
    buffer[1] = signal2;
    Signal[]  result = div.process(buffer);
    assertEquals(4.0, result[0].getValue(), 1e-6);
  }

  @Test
  public void test2()
  {
    buffer[0] = signal1;
    buffer[1] = invalidSignal;
    Signal[]  result = div.process(buffer);
    assertFalse(result[0].isValid());
  }

  @Test
  public void test3()
  {
    buffer[0] = invalidSignal;
    buffer[1] = signal1;
    Signal[]  result = div.process(buffer);
    assertFalse(result[0].isValid());
  }

  @Test
  public void test4()
  {
    buffer[0] = signal1;
    buffer[1] = zeroSignal;
    Signal[]  result = div.process(buffer);
    assertFalse(result[0].isValid());
  }
}

