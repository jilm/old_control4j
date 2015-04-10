package control4j;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import control4j.application.ConfigBuffer;

public class ConfigurationHelperTest
{

  public ConfigBuffer configuration;
  public TestClass1 testClass1 = new TestClass1();

  private class TestClass1
  {
    @ConfigItem
    public String stringItem1;

    @ConfigItem(key="stringItem2")
    public String stringItem3;

    @ConfigItem
    public int intItem1;

    @ConfigItem(optional=true)
    public int intItem2;

    @ConfigItem
    public double doubleItem1;

    public double doubleItem2;
    public double doubleItem3;

    @ConfigItem
    public boolean booleanItem1;

    @ConfigItem
    public void setDoubleItem2(double value)
    {
      doubleItem2 = value;
    }

    @ConfigItem(key="double-item3")
    public void setDoubleItem3(double value)
    {
      doubleItem3 = value;
    }

    @ConfigItem(key="double-item4", optional=true)
    public void setDoubleItem4(double value)
    {
      throw new IllegalArgumentException("double-item4");
    }

    @ConfigItem(key="double-item5", optional=true)
    public void setDoubleItem5(double value)
    { }

  }

  @Before
  public void initialize()
  {
    configuration = new ConfigBuffer();
    configuration.put("stringItem1", "value1");
    configuration.put("stringItem2", "value2");
    configuration.put("intItem1", "123");
    configuration.put("doubleItem1", "1.23");
    configuration.put("booleanItem1", "true");
    configuration.put("setDoubleItem2", "2.34");
    configuration.put("double-item3", "3.45");
  }

  @Test
  public void test1()
  {
    ConfigurationHelper.assignConfiguration(testClass1, configuration, null);
    assertEquals("value1", testClass1.stringItem1);
  }

  @Test
  public void test2()
  {
    ConfigurationHelper.assignConfiguration(testClass1, configuration, null);
    assertEquals(123, testClass1.intItem1);
  }

  @Test
  public void test3()
  {
    ConfigurationHelper.assignConfiguration(testClass1, configuration, null);
    assertEquals(1.23, testClass1.doubleItem1, 1e-3);
  }

  @Test
  public void test4()
  {
    ConfigurationHelper.assignConfiguration(testClass1, configuration, null);
    assertEquals(true, testClass1.booleanItem1);
  }

  @Test
  public void test5()
  {
    ConfigurationHelper.assignConfiguration(testClass1, configuration, null);
    assertEquals("value2", testClass1.stringItem3);
  }

  @Test(expected=SyntaxErrorException.class)
  public void test6()
  {
    try
    {
    configuration.put("intItem2", "abc");
    ConfigurationHelper.assignConfiguration(testClass1, configuration, null);
    }
    catch(IllegalArgumentException e)
    {
      System.out.println(e.getMessage());
      throw e;
    }
  }

  @Test
  public void test7()
  {
    ConfigurationHelper.assignConfiguration(testClass1, configuration, null);
    assertEquals(2.34, testClass1.doubleItem2, 1e-3);
  }

  @Test
  public void test8()
  {
    ConfigurationHelper.assignConfiguration(testClass1, configuration, null);
    assertEquals(3.45, testClass1.doubleItem3, 1e-3);
  }

  @Test(expected=SyntaxErrorException.class)
  public void test9()
  {
    configuration.put("double-item4", "3.45");
    ConfigurationHelper.assignConfiguration(testClass1, configuration, null);
  }

  @Test(expected=SyntaxErrorException.class)
  public void test10()
  {
    configuration.put("double-item5", "abcd");
    ConfigurationHelper.assignConfiguration(testClass1, configuration, null);
  }


}

