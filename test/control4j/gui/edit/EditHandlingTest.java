package control4j.gui.edit;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import control4j.scanner.Getter;
import control4j.scanner.Setter;

public class EditHandlingTest
{

  public class TestObject
  {
    public double doubleValue;

    public TestObject()
    {
    }

    @Getter(key="double value")
    public double getDoubleValue()
    {
      System.out.println("get");
      return doubleValue;
    }

    @Setter(key="double value")
    public void setDoubleValue(double value)
    {
      doubleValue = value;
    }
  }

  private TestObject model;
  private TestObject copy;

  @Before
  public void init()
  {
    model = new TestObject();
    model.doubleValue = 58.55;
    copy = new TestObject();
  }


  @Test
  public void TestCopy1()
  {
    EditHandling.copy(model, copy);
    assertEquals(58.55, copy.doubleValue, 0.001);
  }

}
