package control4j.scanner;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

public class ItemTest
{

  private class TestObject
  {
    public double doubleValue;

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

  private TestObject object;
  private Item item;

  @Before
  public void init() throws Exception
  {
    object = new TestObject();
    item = new Item("double value", object);
    item.setSetter(object.getClass().getMethod("setDoubleValue", double.class));
    item.setGetter(object.getClass().getMethod("getDoubleValue"));
  }


  @Test
  public void testSetValue() throws Exception
  {
    item.setValue(new Double(58.55));
    assertEquals(58.55, object.doubleValue, 0.001);
  }

  @Test
  public void testGetValue1() throws Exception
  {
    Object value = item.getValue();
    assertTrue(value instanceof Double);
  }

  @Test
  public void testGetValue2() throws Exception
  {
    object.doubleValue = 11.25;
    Double value = (Double)item.getValue();
    assertEquals(11.25, value.doubleValue(), 0.001);
  }

  @Test
  public void testSetAndGetValue() throws Exception
  {
    object.doubleValue = 12.34;
    Object value = item.getValue();
    object.doubleValue = 56.78;
    item.setValue(value);
    assertEquals(12.34, object.doubleValue, 0.001);
  }

}
