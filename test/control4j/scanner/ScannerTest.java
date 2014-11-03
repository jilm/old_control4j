package control4j.scanner;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import java.util.Map;

public class ScannerTest
{

  private class TestObject
  {
    public double doubleValue;

    @Setter(key="double value")
    public void setDoubleValue(double value)
    {
      doubleValue = value;
    }

    @Getter(key="double value")
    public double getDoubleValue()
    {
      System.out.println("get");
      return doubleValue;
    }

  }

  private TestObject object;
  private Scanner scanner;

  @Before
  public void init() throws Exception
  {
    object = new TestObject();
    scanner = new Scanner();
  }


  @Test
  public void testScanObject1() throws Exception
  {
    Map<String, Item> scan = scanner.scanObject(object);
    assertEquals(1, scan.size());
  }

  @Test
  public void testScanObject2() throws Exception
  {
    Map<String, Item> scan = scanner.scanObject(object);
    assertNotNull(scan.get("double value"));
  }

  @Test
  public void testScanObject3() throws Exception
  {
    Map<String, Item> scan = scanner.scanObject(object);
    Item doubleValue = scan.get("double value");
    assertEquals(double.class, doubleValue.getValueClass());
  }

  @Test
  public void testScanObject4() throws Exception
  {
    Map<String, Item> scan = scanner.scanObject(object);
    Item doubleValue = scan.get("double value");
    assertTrue(doubleValue.isReadable());
    assertTrue(doubleValue.isWritable());
  }

  @Test
  public void testScanObject5() throws Exception
  {
    Map<String, Item> scan = scanner.scanObject(object);
    Item doubleValue = scan.get("double value");
    doubleValue.setValue(new Double(123.456));
    assertEquals(123.456, object.doubleValue, 0.0001);
  }

  @Test
  public void testScanObject6() throws Exception
  {
    Map<String, Item> scan = scanner.scanObject(object);
    Item doubleValue = scan.get("double value");
    object.doubleValue = 234.56;
    Double value = (Double)doubleValue.getValue();
    assertEquals(234.56, value.doubleValue(), 0.0001);
  }

}
