package control4j.application;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

public class ScopeMapKeyTest extends ScopeMap
{

  Scope scope1 = new Scope(Scope.getGlobal());
  Scope scope2 = new Scope(Scope.getGlobal());
  Scope scope3 = new Scope(scope1);
  Key key1 = new Key("name1", scope1);
  Key key2 = new Key("name1", scope1);
  Key key3 = new Key("name1", scope2);

  @Before
  public void initialize()
  {
  }

  @Test
  public void test1()
  {
    assertTrue(key1.equals(key2));
  }

  @Test
  public void test2()
  {
    assertFalse(key3.equals(key2));
  }

  @Test
  public void test3()
  {
    assertEquals(key1.hashCode(), key2.hashCode());
  }

  @Test
  public void test4()
  {
    assertTrue((key1.hashCode() - key3.hashCode()) != 0);
  }

}

