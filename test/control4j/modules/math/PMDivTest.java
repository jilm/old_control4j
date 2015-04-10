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
  private Signal[] inBuffer = new Signal[5];
  private Signal[] outBuffer = new Signal[5];

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
    inBuffer[0] = signal1;
    inBuffer[1] = signal2;
    div.process(inBuffer, 2, outBuffer, 1);
    assertEquals(4.0, outBuffer[0].getValue(), 1e-6);
  }

  @Test
  public void test2()
  {
    inBuffer[0] = signal1;
    inBuffer[1] = invalidSignal;
    div.process(inBuffer, 2, outBuffer, 1);
    assertFalse(outBuffer[0].isValid());
  }

  @Test
  public void test3()
  {
    inBuffer[0] = invalidSignal;
    inBuffer[1] = signal1;
    div.process(inBuffer, 2, outBuffer, 1);
    assertFalse(outBuffer[0].isValid());
  }

  @Test
  public void test4()
  {
    inBuffer[0] = signal1;
    inBuffer[1] = zeroSignal;
    div.process(inBuffer, 2, outBuffer, 1);
    assertFalse(outBuffer[0].isValid());
  }
}

