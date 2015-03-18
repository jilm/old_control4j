package control4j.application;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

public class ScopeMapTest
{

  ScopeMap<String> map;
  Scope scope1 = new Scope(Scope.getGlobal());
  Scope scope2 = new Scope(Scope.getGlobal());
  Scope scope3 = new Scope(scope1);
  Scope scope4 = new Scope(scope1);
  String name1 = "namea";
  String name2 = "nameb";
  String name3 = "namec";
  String name4 = "namec";
  String value1 = "valuex";
  String value2 = "valuey";
  String value3 = "valuez";

  @Before
  public void initialize()
  {
    map = new ScopeMap<String>();
    map.put(name4, Scope.getGlobal(), value2);
    map.put(name3, scope3, value1);
  }

  @Test
  public void test1()
  {
    String result = map.get("namec", Scope.getGlobal());
    assertTrue("valuey".equals(result));
  }

  @Test
  public void test2()
  {
    String result = map.get("namec", scope1);
    assertTrue("valuey".equals(result));
  }

  @Test
  public void test3()
  {
    String result = map.get("namec", scope3);
    assertTrue("valuex".equals(result));
  }

  @Test
  public void test4()
  {
    String result = map.get("namex", scope3);
    assertNull(result);
  }

/*
  @Test(expected=SyntaxErrorException.class)
  public void test6()
  {
  }
*/

}

