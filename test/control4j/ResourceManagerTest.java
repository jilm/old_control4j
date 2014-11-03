package control4j;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import control4j.application.Property;
import control4j.application.ConfigBuffer;

public class ResourceManagerTest
{
  private static ResourceManager instance;

  private static control4j.resources.Resource res1;
  private static control4j.resources.Resource res2;

  private static class TestResource extends control4j.resources.Resource { }
  private static class TestResource2 extends control4j.resources.Resource { }

  private class TestObject
  {
    @Resource
    private TestResource resource1;

    @Resource(key="RESOURCE2")
    private TestResource resource2;
  }

  private class TestObject2
  {
    @Resource private TestResource2 resource1;
  }

  private TestObject testObject;
  private TestObject2 testObject2;
  private ConfigBuffer configuration;
  private ConfigBuffer configuration2;
  private ConfigBuffer configuration3;

  @BeforeClass
  public static void initialize()
  {
    instance = ResourceManager.getInstance();
    res1 = new TestResource();
    instance.add("res1", res1);
    res2 = new TestResource();
    instance.add("res2", res2);
  }

  @Before
  public void init()
  {
    testObject = new TestObject();
    testObject2 = new TestObject2();
    configuration = new ConfigBuffer();
    configuration.put(new Property("resource1", "res1"));
    configuration.put(new Property("RESOURCE2", "res2"));
    configuration2 = new ConfigBuffer();
    configuration2.put(new Property("resource1", "res1"));
    configuration3 = new ConfigBuffer();
    configuration3.put(new Property("resource1", "resx"));
  }

  @Test
  public void TestGet1()
  {
    control4j.resources.Resource res = instance.get("res1");
    assertEquals(res1, res);
  }

  @Test
  public void TestAssignResources1()
  {
    instance.assignResources(testObject, configuration);
    assertEquals(res1, testObject.resource1);
  }

  @Test
  public void TestAssignResources2()
  {
    instance.assignResources(testObject, configuration);
    assertEquals(res2, testObject.resource2);
  }

  @Test(expected=SyntaxErrorException.class)
  public void TestAssignResources3()
  {
    instance.assignResources(testObject, configuration2);
  }

  @Test(expected=SyntaxErrorException.class)
  public void TestAssignResources4()
  {
    instance.assignResources(testObject, configuration3);
  }

  @Test(expected=SyntaxErrorException.class)
  public void TestAssignResources5()
  {
    instance.assignResources(testObject2, configuration2);
  }


}
