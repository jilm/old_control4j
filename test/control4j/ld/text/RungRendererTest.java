package control4j.ld.text;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import control4j.ld.Rung;
import control4j.ld.Coil;
import control4j.ld.Contact;
import control4j.ld.SerialContactBlock;
import control4j.ld.ParallelContactBlock;
import java.util.List;

public class RungRendererTest
{

  private SerialContactBlock scb1;
  private SerialContactBlock scb2;
  private ParallelContactBlock pcb1;
  private ParallelContactBlock pcb2;
  private Contact c1;
  private Contact c2;
  private Contact c3;
  private Contact c4;
  private Rung rung;
  private Coil coil1;
  private Coil coil2;

  @Before
  public void initialize()
  {
    c1 = new Contact("XIC", "a");
    c2 = new Contact("XIC", "b");
    c3 = new Contact("XIO", "a_very_long_name");
    c4 = new Contact("XIC", "another_long_name");
    scb1 = new SerialContactBlock();
    scb2 = new SerialContactBlock();
    pcb1 = new ParallelContactBlock();
    pcb2 = new ParallelContactBlock();
    scb1.add(c1); scb1.add(c2);
    pcb1.add(c3); pcb1.add(c4);
    scb2.add(c1); scb2.add(pcb1);
    pcb2.add(scb2); pcb2.add(c2);
    rung = new Rung();
    rung.setContactBlock(pcb2);
    coil1 = new Coil("OTE", "coil1");
    coil2 = new Coil("RES", "coil2");
    rung.addCoil(coil1);
    rung.addCoil(coil2);
  }

  @Test
  public void test1()
  {
    RungRenderer rr = new RungRenderer();
    List<StringBuilder> ascii = rr.render(rung);
    for (StringBuilder line : ascii)
      System.out.println(line.toString());
  }

}
