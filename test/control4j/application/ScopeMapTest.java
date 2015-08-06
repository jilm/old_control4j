package control4j.application;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import control4j.tools.DuplicateElementException;

public class ScopeMapTest
{

  ScopeMap<Tag> map;
  Scope root = new Scope();
  Scope scope1 = new Scope(root);
  Scope scope2 = new Scope(root);
  Scope scope3 = new Scope(scope1);
  Scope scope4 = new Scope(scope1);
  String name1 = "namea";
  String name2 = "nameb";
  String name3 = "namec";
  String name4 = "namec";
  Tag value1 = new Tag();
  Tag value2 = new Tag();
  Tag value3 = new Tag();

  @Before
  public void initialize()
  {
    map = new ScopeMap<Tag>();
    map.put(name4, root, value2);
    map.put(name3, scope3, value1);
  }

  @Test
  public void test1()
  {
    Tag result = map.get("namec", root);
    assertTrue(value2 == result);
  }

  @Test
  public void test2()
  {
    Tag result = map.get("namec", scope1);
    assertTrue(value2 == result);
  }

  @Test
  public void test3()
  {
    Tag result = map.get("namec", scope3);
    assertTrue(value1 == result);
  }

  @Ignore
  @Test(expected=java.util.NoSuchElementException.class)
  public void test4()
  {
    Tag result = map.get("namex", scope3);
  }

/*
  @Test(expected=SyntaxErrorException.class)
  public void test6()
  {
  }
*/

}

