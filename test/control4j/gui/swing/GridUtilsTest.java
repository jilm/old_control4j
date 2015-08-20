package control4j.gui.swing;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

public class GridUtilsTest {

  @Before
  public void initialize() {
  }

  @Test
  public void testGrids1() {
    assertEquals(0, GridUtils.gridAlternatives(0));
  }

  @Test
  public void testGrids2() {
    assertEquals(1, GridUtils.gridAlternatives(1));
  }

  @Test
  public void testGrids3() {
    assertEquals(2, GridUtils.gridAlternatives(2));
  }

  @Test
  public void testGrids4() {
    assertEquals(3, GridUtils.gridAlternatives(3));
  }

  @Test
  public void testGrids5() {
    assertEquals(3, GridUtils.gridAlternatives(4));
  }

  @Test
  public void testGrids6() {
    assertEquals(4, GridUtils.gridAlternatives(5));
  }

  @Test
  public void testGrids7() {
    assertEquals(4, GridUtils.gridAlternatives(6));
  }

  @Test
  public void testGrids8() {
    assertEquals(5, GridUtils.gridAlternatives(7));
  }

  @Test
  public void testGrids9() {
    assertEquals(5, GridUtils.gridAlternatives(8));
  }

  @Test
  public void testGrids10() {
    assertEquals(5, GridUtils.gridAlternatives(9));
  }

  @Test
  public void testGrids11() {
    assertEquals(6, GridUtils.gridAlternatives(10));
  }

  //----------------------------------

  @Test
  public void testRows00() {
    assertEquals(0, GridUtils.rows(0, 0));
  }

  @Test
  public void testRows01() {
    assertEquals(1, GridUtils.rows(0, 1));
  }

  @Test
  public void testRows02() {
    assertEquals(1, GridUtils.rows(0, 2));
  }

  @Test
  public void testRows12() {
    assertEquals(2, GridUtils.rows(1, 2));
  }

  @Test
  public void testRows03() {
    assertEquals(1, GridUtils.rows(0, 3));
  }

  @Test
  public void testRows13() {
    assertEquals(2, GridUtils.rows(1, 3));
  }

  @Test
  public void testRows23() {
    assertEquals(3, GridUtils.rows(2, 3));
  }

  @Test
  public void testRows04() {
    assertEquals(1, GridUtils.rows(0, 4));
  }

  @Test
  public void testRows14() {
    assertEquals(2, GridUtils.rows(1, 4));
  }

  @Test
  public void testRows24() {
    assertEquals(4, GridUtils.rows(2, 4));
  }

  @Test
  public void testRows05() {
    assertEquals(1, GridUtils.rows(0, 5));
  }

  @Test
  public void testRows15() {
    assertEquals(2, GridUtils.rows(1, 5));
  }

  @Test
  public void testRows25() {
    assertEquals(3, GridUtils.rows(2, 5));
  }

  @Test
  public void testRows35() {
    assertEquals(5, GridUtils.rows(3, 5));
  }

  @Test
  public void testRows06() {
    assertEquals(1, GridUtils.rows(0, 6));
  }

  @Test
  public void testRows16() {
    assertEquals(2, GridUtils.rows(1, 6));
  }

  @Test
  public void testRows26() {
    assertEquals(3, GridUtils.rows(2, 6));
  }

  @Test
  public void testRows36() {
    assertEquals(6, GridUtils.rows(3, 6));
  }

  @Test
  public void testRows07() {
    assertEquals(1, GridUtils.rows(0, 7));
  }

  @Test
  public void testRows17() {
    assertEquals(2, GridUtils.rows(1, 7));
  }

  @Test
  public void testRows27() {
    assertEquals(3, GridUtils.rows(2, 7));
  }

  @Test
  public void testRows37() {
    assertEquals(4, GridUtils.rows(3, 7));
  }

  @Test
  public void testRows47() {
    assertEquals(7, GridUtils.rows(4, 7));
  }

  //----------------------------------

  @Test
  public void testCols00() {
    assertEquals(0, GridUtils.cols(0, 0));
  }

  @Test
  public void testCols01() {
    assertEquals(1, GridUtils.cols(0, 1));
  }

  @Test
  public void testCols02() {
    assertEquals(2, GridUtils.cols(0, 2));
  }

  @Test
  public void testCols12() {
    assertEquals(1, GridUtils.cols(1, 2));
  }

  @Test
  public void testCols03() {
    assertEquals(3, GridUtils.cols(0, 3));
  }

  @Test
  public void testCols13() {
    assertEquals(2, GridUtils.cols(1, 3));
  }

  @Test
  public void testCols23() {
    assertEquals(1, GridUtils.cols(2, 3));
  }

  @Test
  public void testCols04() {
    assertEquals(4, GridUtils.cols(0, 4));
  }

  @Test
  public void testCols14() {
    assertEquals(2, GridUtils.cols(1, 4));
  }

  @Test
  public void testCols24() {
    assertEquals(1, GridUtils.cols(2, 4));
  }

  @Test
  public void testCols05() {
    assertEquals(5, GridUtils.cols(0, 5));
  }

  @Test
  public void testCols15() {
    assertEquals(3, GridUtils.cols(1, 5));
  }

  @Test
  public void testCols25() {
    assertEquals(2, GridUtils.cols(2, 5));
  }

  @Test
  public void testCols35() {
    assertEquals(1, GridUtils.cols(3, 5));
  }

  @Test
  public void testCols06() {
    assertEquals(6, GridUtils.cols(0, 6));
  }

  @Test
  public void testCols16() {
    assertEquals(3, GridUtils.cols(1, 6));
  }

  @Test
  public void testCols26() {
    assertEquals(2, GridUtils.cols(2, 6));
  }

  @Test
  public void testCols36() {
    assertEquals(1, GridUtils.cols(3, 6));
  }

  @Test
  public void testCols07() {
    assertEquals(7, GridUtils.cols(0, 7));
  }

  @Test
  public void testCols17() {
    assertEquals(4, GridUtils.cols(1, 7));
  }

  @Test
  public void testCols27() {
    assertEquals(3, GridUtils.cols(2, 7));
  }

  @Test
  public void testCols37() {
    assertEquals(2, GridUtils.cols(3, 7));
  }

  @Test
  public void testCols47() {
    assertEquals(1, GridUtils.cols(4, 7));
  }

}
