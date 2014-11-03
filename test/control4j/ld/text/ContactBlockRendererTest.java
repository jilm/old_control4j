package control4j.ld.text;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import control4j.ld.Contact;
import control4j.ld.SerialContactBlock;
import control4j.ld.ParallelContactBlock;

public class ContactBlockRendererTest
{

  private SerialContactBlock scb1;
  private SerialContactBlock scb2;
  private ParallelContactBlock pcb1;
  private ParallelContactBlock pcb2;
  private Contact c1;
  private Contact c2;
  private Contact c3;
  private Contact c4;

  @Before
  public void initialize()
  {
    c1 = new Contact("XIC", "c1");
    c2 = new Contact("XIC", "c2");
    c3 = new Contact("XIO", "c3_a_very_long_name");
    c4 = new Contact("XIC", "c4_another_long_name");
    scb1 = new SerialContactBlock();
    scb2 = new SerialContactBlock();
    pcb1 = new ParallelContactBlock();
    pcb2 = new ParallelContactBlock();
    scb1.add(c1); scb1.add(c2);
    pcb1.add(c3); pcb1.add(c4);
    scb2.add(c1); scb2.add(pcb1);
    pcb2.add(scb1); pcb2.add(c2);
  }

  @Test
  public void test1()
  {
    System.out.println("*** scb2: c1-(c3|c4) ***");
    ContactBlockRenderer.render(scb2, 0).print();
  }

  @Test
  public void test2()
  {
    System.out.println("*** pcb2: (c1-c2)|c2 ***");
    ContactBlockRenderer.render(pcb2, 0).print();
  }

  @Test
  public void test3()
  {
    System.out.println("*** c3 ***");
    ContactBlockRenderer.render(c3, 0).print();
  } 

  @Test
  public void test4()
  {
    System.out.println("*** scb1: c1-c2 ***");
    ContactBlockRenderer.render(scb1, 0).print();
  } 

  @Test
  public void testLength1()
  {
    RendererBase b = ContactBlockRenderer.render(scb1, 50);
    b.print();
    assertEquals(50, b.getMaxLength());
  }

  @Test
  public void testLength2()
  {
    RendererBase b = ContactBlockRenderer.render(scb2, 50);
    b.print();
    assertEquals(50, b.getMaxLength());
  }

  @Test
  public void testLength3()
  {
    RendererBase b = ContactBlockRenderer.render(pcb1, 50);
    b.print();
    assertEquals(50, b.getMaxLength());
  }

  @Test
  public void testLength4()
  {
    RendererBase b = ContactBlockRenderer.render(pcb2, 50);
    b.print();
    assertEquals(50, b.getMaxLength());
  }
}
