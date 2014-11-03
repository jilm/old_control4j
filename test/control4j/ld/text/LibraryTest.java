package control4j.ld.text;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

public class LibraryTest
{

  Library library;

  @Before
  public void initialize()
  {
    library = Library.getInstance();
  }

  @Test
  public void test1()
  {
    String rto = library.get("RTO");
    assertTrue(rto.equals("--(RTO)--"));
  }

}
