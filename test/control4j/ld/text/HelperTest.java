package control4j.ld.text;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import control4j.ld.Contact;
import control4j.ld.SerialContactBlock;
import control4j.ld.ParallelContactBlock;

public class HelperTest
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
  public void testGetWidth1()
  {
    int width = Helper.getWidth(c1);
    assertEquals(1, width);
  }

  @Test
  public void testGetWidth2()
  {
    int width = Helper.getWidth(scb1);
    assertEquals(2, width);
  }

  @Test
  public void testGetWidth3()
  {
    int width = Helper.getWidth(pcb1);
    assertEquals(1, width);
  }
  
  @Test
  public void testGetWidth4()
  {
    int width = Helper.getWidth(scb2);
    assertEquals(2, width);
  }

  private int countCharWidth(RendererBase ascii)
  {
    int width = 0;
    for (int i=0; i<ascii.ascii.size(); i++)
      width = Math.max(width, ascii.ascii.get(i).length());
    return width;
  }

  @Test
  public void testGetCharWidth1()
  {
    int width = Helper.getCharWidth(c1);
    RendererBase ascii = ContactBlockRenderer.render(c1, 0);
    int actualWidth = countCharWidth(ascii);
    assertEquals(width, actualWidth);
  }

  @Test
  public void testGetCharWidth2()
  {
    int width = Helper.getCharWidth(c3);
    RendererBase ascii = ContactBlockRenderer.render(c3, 0);
    int actualWidth = countCharWidth(ascii);
    assertEquals(width, actualWidth);
  }

  @Test
  public void testGetCharWidth3()
  {
    int width = Helper.getCharWidth(scb1);
    RendererBase ascii = ContactBlockRenderer.render(scb1, 0);
    int actualWidth = countCharWidth(ascii);
    assertEquals(width, actualWidth);
  }

  @Test
  public void testGetCharWidth4()
  {
    int width = Helper.getCharWidth(scb2);
    RendererBase ascii = ContactBlockRenderer.render(scb2, 0);
    int actualWidth = countCharWidth(ascii);
    assertEquals(width, actualWidth);
  }

  @Test
  public void testGetCharWidth5()
  {
    int width = Helper.getCharWidth(pcb1);
    RendererBase ascii = ContactBlockRenderer.render(pcb1, 0);
    int actualWidth = countCharWidth(ascii);
    assertEquals(width, actualWidth);
  }

  @Test
  public void testGetCharWidth6()
  {
    int width = Helper.getCharWidth(pcb2);
    RendererBase ascii = ContactBlockRenderer.render(pcb2, 0);
    int actualWidth = countCharWidth(ascii);
    assertEquals(width, actualWidth);
  }
}
