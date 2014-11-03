package control4j.protocols.spinel;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

public class SpinelMessageTest
{
  public SpinelMessage message;

  @Before
  public void initialize()
  {
    message = new SpinelMessage(0x31, 0xf3);
    message.setSig(2);
  }

  @Test
  public void length()
  {
    assertEquals(9, message.length());
  }

  @Test
  public void get()
  {
    assertEquals(0x2a, message.get(0));
    assertEquals(0x61, message.get(1));
    assertEquals(0x00, message.get(2));
    assertEquals(0x05, message.get(3));
    assertEquals(0x31, message.get(4));
    assertEquals(0x02, message.get(5));
    assertEquals(0xf3, message.get(6));
    assertEquals(0x49, message.get(7)); // 73
    assertEquals(0x0d, message.get(8));
  }
}
