package control4j;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import control4j.resources.Console;

public class ResourceTest
{

  @Before
  public void initialize()
  {
  }

  @Test
  public void test1() throws Exception
  {
    Resource console = Resource.getResource(Console.class, null);
    assertTrue(console instanceof Console);
  }

  @Test
  public void test2() throws Exception
  {
    Resource console = Resource.getResource(
	"control4j.resources.Console", null);
    assertTrue(console instanceof Console);
  }
}
