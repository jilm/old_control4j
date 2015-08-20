package control4j.gui.swing;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

public class VDUTest extends VDU {

  @Before
  public void initialize() {
  }

  @Test
  public void test1() {
    float[] layout = oneLineLayout(50f, 30f, 2f/3f, 10f);
    for (int i = 0; i < layout.length; i++) {
      System.out.println(layout[i]);
    }
    assertArrayEquals(new float[] {50f, 30f, 20f, 30f, 30f, 0f, 30f, 30f, 30f, 0f, 0f, 20f}, layout, 0.5f);
  }

  @Test
  public void test2() {
    float[] layout = oneLineLayout(50f, 30f, 2.5f, 10f);
    assertArrayEquals(
        new float[] {50f, 30f, 28.6f, 11.4f, 21.4f, 0f, 11.4f, 21.4f, 11.4f, 0f, 0f, 7.6f}, layout, 0.5f);
  }

  @Test
  public void test3() {
    float[] layout = twoLinesLayout(50f, 30f, 2.5f, 10f);
    assertArrayEquals(
        new float[] {50f, 30f, 45f, 18f, 0f, 12f, 18f, 50f, 12f, 0f, 0f, 12f},
        layout, 0.5f);
  }

}
