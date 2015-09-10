/*
 *  Copyright 2015 Jiri Lidinsky
 *
 *  This file is part of control4j.
 *
 *  control4j is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 3.
 *
 *  control4j is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with control4j.  If not, see <http://www.gnu.org/licenses/>.
 */

package control4j.gui.swing;

import control4j.ConfigItem;
import control4j.gui.VisualObject;
import control4j.modules.IGuiUpdateListener;

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;
import cz.lidinsky.tools.Validate;
import cz.lidinsky.tools.reflect.Getter;
import cz.lidinsky.tools.reflect.Setter;
import cz.lidinsky.tools.swing.JPopupMenu;
import cz.lidinsky.tools.swing.JMenuItem;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 *  A component which purpose is to display a grid of signals.
 *
 */
public class VDU extends JComponent implements MouseListener, ActionListener {

  private DecimalFormat format = new DecimalFormat();
  private float fontSize = 12.0f;

  public int digits = 5;

  private boolean dirty = true;

  public VDU() {
    super();
    valueComponent = new JLabel();
    valueComponent.setBackground(Color.BLACK);
    valueComponent.setOpaque(true);
    valueComponent.setHorizontalAlignment(SwingConstants.RIGHT);
    labelComponent = new JLabel();
    labelComponent.setHorizontalAlignment(SwingConstants.RIGHT);
    unitComponent = new JLabel();
    // Popup menu
    setComponentPopupMenu(new JPopupMenu()
        .addItem(new JMenuItem()
          .setFText("Show penalty matrix")
          .addFActionListener(this)
          .setFActionCommand("PENALTY_MATRIX"))
        .addItem(new JMenuItem()
          .setFText("Next layout")
          .addFActionListener(this)
          .setFActionCommand("NEXT_LAYOUT")));
  }

  //--------------------------------------------------------- Public Interface.

  /**
   *  Adds a required number of cells
   *
   *  @param number
   *             how many cells to add
   *
   *  @throws CommonException
   *             if the parameter contains negative number
   */
  public void addCells(final int number) {
    for (int i = 0; i < Validate.notNegative(number); i++) {
      cells.add(new Cell());
      dirty = true;
    }
  }

  /**
   *  Adds one cell.
   */
  public void addCell() {
    cells.add(new Cell());
    dirty = true;
  }

  /**
   *  Sets label for a cell.
   *
   *  @param index
   *             which cell will be affected
   *
   *  @param label
   *             new label for a cell with given index
   *
   *  @throws CommonException
   *             if the index is out of bounds
   *
   *  @throws CommonException
   *             if the label is null
   */
  public void setLabel(int index, String label) {
    cells
      .get(Validate.checkIndex(cells, index))
      .setLabel(Validate.notNull(label));
  }

  /**
   *  Sets the value for a particular cell.
   *
   *  @param index
   *             which cell will be affected
   *
   *  @param value
   *             new value for a cell with given index. If the value should be
   *             presented as unknown or invalid, write <code>NaN</code>
   *
   *  @throws CommonException
   *             if the index is out of bounds
   */
  public void setValue(int index, double value) {
    cells.get(index).setValue(value);
  }

  /**
   *  Sets the unit for a cell.
   *
   *  @param index
   *             which cell will be affected
   *
   *  @param unit
   *             new unit for a cell with given index, may be <code>null</code>
   *             value
   *
   *  @throws CommonException
   *             if the index is out of bounds
   */
  public void setUnit(int index, String unit) {
    cells
      .get(Validate.checkIndex(cells, index))
      .setUnit(unit);
  }

  public void hide(int index) {
  }

  public void show(int index) {
  }

  //--------------------------------------------------------- Cell Information.

  /** List of all cells inside the grid. */
  private List<Cell> cells = new ArrayList<Cell>();

  /**
   *  Contains information which are specific for a particular signal.
   */
  private class Cell {

    /** A label, if null, it will not be displayed. */
    private String label = "Value";

    /** Signal unit, if null, it will not be dispalyed. */
    private String unit = null;

    /** A value that will be shown. */
    private double value = Double.NaN;

    /** If this signal will be displayed at all. */
    private boolean visible = true;

    int refLabelWidth;
    int refUnitWidth;
    float labelRatio;
    float unitRatio;

    public void setLabel(String label) {
      this.label = label;
    }

    public void setValue(double value) {
      this.value = value;
    }

    public void setUnit(String unit) {
      this.unit = unit;
    }

  }

  //---------------------------------------------------------------- VDU Paint.

  private JLabel labelComponent;
  private JLabel valueComponent;
  private JLabel unitComponent;

  private Color VALID_COLOR = Color.GREEN;
  private Color INVALID_COLOR = Color.YELLOW;
  private String INVALID_TEXT = "?";

  float[] layout;

  /**
   *  Paints the component.
   */
  protected void paintComponent(Graphics g) {
    // update all of the dimensions, if necessary
    if (dirty) {
      update();
    }
    // finds appropriate layout
    if (resized() || dirty) {
      int[] gridLayout = findLayout();
      int count = cells.size();
      rows = gridLayout[1];
      cols = gridLayout[2];
      float cellWidth = getCellWidth(this.cols);
      float cellHeight = getCellHeight(this.rows);
      layout = calculateLayout(
          gridLayout[0], cellWidth, cellHeight, valueRatio, (float)refHeight);
      applyLayout(layout);
    }
    dirty = false;
    int count = cells.size();
    float cellWidth = layout[CELL_WIDTH];
    float cellHeight = layout[CELL_HEIGHT];
    float gap = getGap();
    Polygon triangle = new Polygon();
    triangle.addPoint((int)(gap * 0.5f), (int)(gap * 0.5f));
    triangle.addPoint((int)(cellWidth + gap), (int)(gap * 0.5));
    triangle.addPoint((int)(gap * 0.5f), (int)(cellHeight + gap));
    for (int i = 0; i < count; i++) {
      int col = i % cols;
      int row = i / cols;
      // paint background
      float x = col * (cellWidth + gap);
      float y = row * (cellHeight + gap);
      int cellX = (int)(col * cellWidth + gap * col);
      int cellY = (int)(row * cellHeight + gap * row);
      Graphics cellG = g.create(
          cellX, cellY,
          (int)(layout[CELL_WIDTH] + 2f * gap),
          (int)(layout[CELL_HEIGHT] + 2f * gap));
      cellG.setColor(Color.ORANGE);
      cellG.fillPolygon(triangle);
      // convert and paint the value
      double value = cells.get(i).value;
      String strValue;
      if (Double.isNaN(value)) {
        strValue = INVALID_TEXT;
        valueComponent.setForeground(INVALID_COLOR);
      } else {
        strValue = format.format(value);
        valueComponent.setForeground(VALID_COLOR);
      }
      valueComponent.setText(strValue);
      valueComponent.paint(
          g.create(
            (int)(layout[VALUE_X] + col * cellWidth + gap * col + gap),
            (int)(layout[VALUE_Y] + row * cellHeight + gap * row + gap),
            (int)layout[VALUE_WIDTH],
            (int)layout[VALUE_HEIGHT]));
      // paint the label
      labelComponent.setText(cells.get(i).label);
      labelComponent.paint(
          g.create(
            (int)(layout[LABEL_X] + col * cellWidth + gap * col + gap),
            (int)(layout[LABEL_Y] + row * cellHeight + gap * row + gap),
            (int)layout[LABEL_WIDTH],
            (int)layout[LABEL_HEIGHT]));
      // paint the unit
      unitComponent.setText(cells.get(i).unit);
      unitComponent.paint(
          g.create(
            (int)(layout[UNIT_X] + col * cellWidth + gap * col + gap),
            (int)(layout[UNIT_Y] + row * cellHeight + gap * row + gap),
            (int)layout[UNIT_WIDTH],
            (int)layout[UNIT_HEIGHT]));
    }
  }

  /**
   *  Height of the text with reference font.
   */
  private int refHeight;
  private int refValueWidth;
  private float valueRatio;

  /** Width of the longest label. */
  private int maxRefLabelWidth;

  /**
   *  Should be called after the content of some component changed. It means
   *  content which could influence the dimensions of this component. This
   *  method recalculates reference dimensions of all components.
   */
  private void update() {
    maxRefLabelWidth = 0;
    // get the reference font metrics
    FontMetrics metrics = getFontMetrics(getFont().deriveFont(10.0f));
    refHeight = metrics.getHeight();
    // calculate reference dimensions for the value
    //int digits = 5;
    double number = (Math.pow(10.0d, digits) - 1) * -1;
    String text = format.format(number);
    refValueWidth = metrics.stringWidth(text);
    valueRatio = (float)refValueWidth / (float)refHeight;
    // calculate dimensions of the components which are cell dependent
    for (Cell cell : cells) {
      // label
      if (cell.label != null) {
        cell.refLabelWidth = metrics.stringWidth(cell.label);
        maxRefLabelWidth = Math.max(maxRefLabelWidth, cell.refLabelWidth);
        cell.labelRatio = (float)cell.refLabelWidth / (float)refHeight;
      } else {
        cell.refLabelWidth = 0;
        cell.labelRatio = 0f;
      }
      // unit
      if (cell.unit != null) {
        cell.refUnitWidth = metrics.stringWidth(cell.unit);
        cell.unitRatio = (float)cell.refUnitWidth / (float)refHeight;
      } else {
        cell.refUnitWidth = 0;
        cell.unitRatio = 0f;
      }
    }
  }

  /** Auxiliary fields to detect window resize. */
  private int oldHeight;
  private int oldWidth;

  /**
   *  Returns true if the component has been resized since last call.
   */
  private boolean resized() {
    boolean resized = oldHeight != getHeight() || oldWidth != getWidth();
    oldHeight = getHeight();
    oldWidth = getWidth();
    return resized;
  }

  /**
   *  The gap between cells. It is the portion of the smaller component
   *  dimension.
   */
  private float gapRatio = 1f/40f;

  /**
   *  Returns the gap size in pixels.
   */
  private float getGap() {
    float size = (float)Math.min(getHeight(), getWidth());
    return size * gapRatio;
  }

  private float getCellWidth(int columns) {
    return ((float)getWidth() - getGap() * (columns + 1)) / (float)columns;
  }

  private float getCellHeight(int rows) {
    return ((float)getHeight() - getGap() * (rows + 1)) / (float)rows;
  }

  //------------------------------------------------------------------- Layout.

  private static final int options = 2;

  /** It is the number of option for optimal arrangement. */
  private int option;

  /** Optimal number of rows. */
  private int rows;

  /** Optimal number of columns. */
  private int cols;

  private float[] balanceVector = {0.2f, 0.8f};

  /**
   *  Finds optimal arrangement.
   */
  private int[] findLayout() {
    float width = getWidth();
    float height = getHeight();
    int count = cells.size();
    int grids = GridUtils.gridAlternatives(count);
    float penalty[][] = new float[grids][options];
    // Go through all of the possible row and column counts
    for (int grid = 0; grid < grids; grid++) {
      int rows = GridUtils.rows(grid, count);
      int cols = GridUtils.cols(grid, count);
      float cellWidth = getCellWidth(cols);
      float cellHeight = getCellHeight(rows);
      float cellRatio = cellWidth / cellHeight;
      float[] layout;
      for (int option = 0; option < options; option++) {
        layout = calculateLayout(option,
              cellWidth, cellHeight, valueRatio, (float)refHeight);
        penalty[grid][option] = 1.0f;
        for (Cell cell : cells) {
          penalty[grid][option] = Math.min(
              penalty[grid][option],
              product(qualityVector(layout, cell), balanceVector));
        }
      }
    }
    // Find option with the lowes penalty
    int[] max = max(penalty);
    int optGrid = max[0];
    int optOption = max[1];
    // returns result
    int[] result = new int[3];
    result[0] = optOption;
    result[1] = GridUtils.rows(optGrid, count);
    result[2] = GridUtils.cols(optGrid, count);
    return result;
  }

  private int[] max(float[][] matrix) {
    int[] max = new int[] {0, 0};
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        if (matrix[i][j] > matrix[max[0]][max[1]]) {
          max[0] = i;
          max[1] = j;
        }
      }
    }
    return max;
  }

  private float[][] calculatePenaltyMatrix() {
    float width = getWidth();
    float height = getHeight();
    int count = cells.size();
    int grids = GridUtils.gridAlternatives(count);
    float penalty[][] = new float[grids][options];
    // Go through all of the possible row and column counts
    for (int grid = 0; grid < grids; grid++) {
      int rows = GridUtils.rows(grid, count);
      int cols = GridUtils.cols(grid, count);
      float cellWidth = getCellWidth(cols);
      float cellHeight = getCellHeight(rows);
      float cellRatio = cellWidth / cellHeight;
      float[] layout;
      for (int option = 0; option < options; option++) {
        layout = calculateLayout(option,
              cellWidth, cellHeight, valueRatio, (float)refHeight);
        penalty[grid][option] = 1.0f;
        for (Cell cell : cells) {
          penalty[grid][option] = Math.min(
              penalty[grid][option],
              product(qualityVector(layout, cell), balanceVector));
        }
      }
    }
    return penalty;
  }

  //--------------------------------------------------------- Penalty Function.

  private float[] qualityVector(float[] layout, Cell cell) {
    float labelWidth
      = layout[LABEL_HEIGHT] * (float)cell.refLabelWidth / (float)refHeight;
    // 0.0 - 1.0; 0.0 the worst, 1.0 the best
    float lwf = labelWidthFactor(layout, labelWidth);
    // 0.0 - 1.0; 1.0 the best, 0.0 the worst
    float wast = wastSpace(layout, labelWidth);
    float[] result = new float[2];
    result[0] = lwf;
    result[1] = wast;
    return result;
  }

  protected static float product(float[] vector1, float[] vector2) {
    float sum = 0.0f;
    for (int i = 0; i < vector1.length; i++) {
      sum += vector1[i] * vector2[i];
    }
    return sum;
  }

  private String formatPenaltyMatrix(float[][] matrix) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        sb.append(" ");
        sb.append(matrix[i][j]);
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  // * value font size
  // * label font size
  // * label shortened  v
  // * label visible
  // * unit visible
  // * wast space

  //------------------------------------------------------------------ Options.

  private static final int LAYOUT_ARRAY_SIZE = 16;
  /** Indexes into the layout array. */
  private static final int CELL_WIDTH = 0;
  private static final int CELL_HEIGHT = 1;
  private static final int VALUE_WIDTH = 2;
  private static final int VALUE_HEIGHT = 3;
  private static final int VALUE_X = 4;
  private static final int VALUE_Y = 5;
  private static final int VALUE_FONT = 6;
  private static final int LABEL_WIDTH = 7;
  private static final int LABEL_HEIGHT = 8;
  private static final int LABEL_X = 9;
  private static final int LABEL_Y = 10;
  private static final int LABEL_FONT = 11;
  private static final int UNIT_WIDTH = 12;
  private static final int UNIT_HEIGHT = 13;
  private static final int UNIT_X = 14;
  private static final int UNIT_Y = 15;

  private float[] calculateLayout(int option,
      float cellWidth, float cellHeight, float valueRatio, float refHeight) {

    switch (option) {
      case 0:
        return oneLineLayout(cellWidth, cellHeight, valueRatio, (float)maxRefLabelWidth / (float)refHeight, refHeight);
      case 1:
        return twoLinesLayout(cellWidth, cellHeight, valueRatio, refHeight);
      default:
        throw new CommonException()
          .setCode(ExceptionCode.ILLEGAL_ARGUMENT);
    }
  }

  /**
   *  Label, value and unit are on one line. Label may be shortened if
   *  neccessary.
   */
  protected static float[] oneLineLayout(
      float cellWidth,
      float cellHeight,
      float valueRatio,
      float maxLabelRatio,
      float refHeight) {

    // portion for a unit box
    float cellRatio = cellWidth / cellHeight;
    float labelValueFontRatio = 2f/3f;
    float unitPortion = 1f/10f;
    float unitRatio = unitPortion * cellRatio;
    float minLabelPortion = 3f/10f;
    float minLabelRatio = minLabelPortion * cellRatio;
    float chainRatio = maxLabelRatio + valueRatio + cellRatio * unitPortion;
    float height;
    if (chainRatio <= cellRatio) {
      // If the cell is wide enough to contain the whole chain:
      // max label + value + unit
      height = cellHeight;
    } else if (cellRatio - valueRatio - unitRatio >= minLabelRatio) {
      // If the cell is not wide enough, but the place for label is still wider
      // than minimal portion for a label
      height = cellHeight;
    } else {
      // otherwise, scale the whole chain to fit the cell width
      height = cellWidth / (minLabelRatio + valueRatio + unitRatio);
    }
    float[] result = new float[LAYOUT_ARRAY_SIZE];
    result[CELL_WIDTH] = cellWidth;
    result[CELL_HEIGHT] = cellHeight;
    result[VALUE_WIDTH] = height * valueRatio;
    result[VALUE_HEIGHT] = height;
    result[UNIT_WIDTH] = height * unitRatio;
    result[UNIT_HEIGHT] = height;
    result[LABEL_WIDTH] = cellWidth - result[VALUE_WIDTH] - result[UNIT_WIDTH];
    result[LABEL_HEIGHT] = height;
    result[LABEL_X] = 0f;
    result[LABEL_Y] = 0f;
    result[VALUE_X] = result[LABEL_WIDTH];
    result[VALUE_Y] = 0f;
    result[UNIT_X] = result[LABEL_WIDTH] + result[VALUE_WIDTH];
    result[UNIT_Y] = 0f;
    result[VALUE_FONT] = 10f * height / refHeight;
    result[LABEL_FONT] = result[VALUE_FONT] * labelValueFontRatio;
    return result;
  }

  protected static float[] twoLinesLayout(
      float cellWidth, float cellHeight, float valueRatio, float refHeight) {

    float minLabelValueProportion = 2f/5f;
    float labelValueFontRatio = 2f/3f;
    float cellRatio = cellWidth / cellHeight;
    float valueWidth;
    float valueHeight;
    if (valueRatio * (1f - minLabelValueProportion) > cellRatio) {
      // if the value is still wider than the cell
      valueWidth = cellWidth;
      valueHeight = valueWidth / valueRatio;
    } else {
      valueHeight = cellHeight * (1f - minLabelValueProportion);
      valueWidth = valueHeight * valueRatio;
    }
    float[] result = new float[LAYOUT_ARRAY_SIZE];
    result[CELL_WIDTH] = cellWidth;
    result[CELL_HEIGHT] = cellHeight;
    result[VALUE_WIDTH] = valueWidth;
    result[VALUE_HEIGHT] = valueHeight;
    result[VALUE_X] = 0f;
    result[VALUE_Y] = cellHeight - valueHeight;
    result[VALUE_FONT] = 10f * valueHeight / refHeight;
    result[LABEL_WIDTH] = cellWidth;
    result[LABEL_HEIGHT] = valueHeight * minLabelValueProportion;
    result[LABEL_X] = 0f;
    result[LABEL_Y] = 0f;
    result[LABEL_FONT] = result[VALUE_FONT] * labelValueFontRatio;
    return result;
  }

  protected void applyLayout(float[] layout) {
    valueComponent.setSize((int)layout[VALUE_WIDTH], (int)layout[VALUE_HEIGHT]);
    valueComponent.setLocation(0, 0);
    valueComponent.setFont(getFont().deriveFont(layout[VALUE_FONT]));
    labelComponent.setSize((int)layout[LABEL_WIDTH], (int)layout[LABEL_HEIGHT]);
    labelComponent.setLocation(0, 0);
    labelComponent.setFont(getFont().deriveFont(layout[LABEL_FONT]));
  }

  protected static float wastSpace(float[] layout, float labelWidth) {
    return (layout[VALUE_WIDTH] * layout[VALUE_HEIGHT]
      + labelWidth * layout[LABEL_HEIGHT])
      / (layout[CELL_WIDTH] * layout[CELL_HEIGHT]);
  }

  /**
   *  Returns label width. It returns 1.0 if the label may stay unchanged and a
   *  number between 0.0 and 1.0 if it must be shortened.
   *
   *  @param cellRatio
   *             (cell width) / (cell height)
   */
  protected static float labelWidthFactor(float[] layout, float labelWidth) {
    return Math.min(1f, layout[LABEL_WIDTH] / labelWidth);
  }

  //---------------------------------- Mouse Listener Interface Implementation.

  public void mouseClicked(MouseEvent event) {
    if (event.getButton() == MouseEvent.BUTTON3) {
      float[][] penaltyMatrix = calculatePenaltyMatrix();
      JOptionPane.showMessageDialog(this, formatPenaltyMatrix(penaltyMatrix));
    }
  }

  public void mouseEntered(MouseEvent event) {}

  public void mouseExited(MouseEvent event) {}

  public void mousePressed(MouseEvent event) {}

  public void mouseReleased(MouseEvent event) {}

  //------------------------------------ Action Event Interface Implementation.

  public void actionPerformed(ActionEvent event) {
    if (event.getActionCommand().equals("PENALTY_MATRIX")) {
      float[][] penaltyMatrix = calculatePenaltyMatrix();
      JOptionPane.showMessageDialog(this, formatPenaltyMatrix(penaltyMatrix));
    } else if (event.getActionCommand().equals("NEXT_LAYOUT")) {
      float[][] penalty = calculatePenaltyMatrix();
      int[] max = max(penalty);
      penalty[max[0]][max[1]] = 0f;
      max = max(penalty);
      rows = GridUtils.rows(max[0], cells.size());
      cols = GridUtils.cols(max[0], cells.size());
      float cellWidth = getCellWidth(cols);
      float cellHeight = getCellHeight(rows);
      layout = calculateLayout(
          max[1], cellWidth, cellHeight, valueRatio, (float)refHeight);
      applyLayout(layout);
      repaint();
    }
  }

}
