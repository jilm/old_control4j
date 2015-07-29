package control4j.gui.components;

/*
 *  Copyright 2013, 2014 Jiri Lidinsky
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import javax.swing.JComponent;
import cz.lidinsky.tools.reflect.Getter;
import cz.lidinsky.tools.reflect.Setter;

/**
 *
 *  Plot a live graph.
 *
 *  <p>Up to five signals may be plotted in one graph.
 *
 */
@control4j.annotations.AGuiObject(name="Graph")
public class Graph extends control4j.gui.components.VisualObjectBase
implements control4j.ICycleEventListener
{

  /** X axis length in seconds. Must be greater than zero! */
  private int duration = 100;

  /** Y axis, min value */
  private float yMin = 0.0f;

  /** Y axis, max value */
  private float yMax = 1.0f;

  /** Width of the whole graph in pixels. */
  private int width = 100;

  /** Height of the whole graph in pixels. */
  private int height = 50;

  /** Contains data for the whole plot history. First index is time index,
      and the second one is signal index. Buffer is circular. */
  private float buffer[][] = new float[width][channels];

  /** Index into buffer which points to the oldest data. */
  private int oldDataIndex = 0;

  /** Index into buffer which points to the latest data. */
  private int latestDataIndex = 0;

  /** Time of latest displayed data sample */
  private long latestDataTime;

  /** Number of signals which may be plotted simultaneously into one graph */
  private static final int channels = 5;

  /** If data period is shorter than time per pixel, the average value is
      displayd on the graph. This serves as accumulator for avg calculation */
  private float[] sum = new float[channels];

  /** Counter for avg calculation */
  private int[] count = new int[channels];

  /** Colors of the curves */
  private Color[] curveColors = new Color[channels];

  /** Number of horizontal lines */
  private static final int horizontalLinesCount = 4;

  /** Horizontal line positions */
  private float[] horizontalLineValues = new float[horizontalLinesCount];

  /** Horizontal line colors */
  private Color[] horizontalLineColors = new Color[horizontalLinesCount];

  /** Specify half of the area */
  private float[] horizontalLineArea = new float[horizontalLinesCount];

  public Graph()
  {
    super();
    latestDataTime = System.currentTimeMillis();
  }

  private void writeSimulatedData()
  {
    float[] data = new float[channels];
    for (int i=0; i<width/2; i++)
    {
      for (int channel = 0; channel < channels; channel++)
      {
	data[channel] = (float)Math.random();
      }
      addToBuffer(data);
    }
  }

  @Getter("Width")
  public int getWidth()
  {
    return width;
  }

  @Setter("Width")
  public void setWidth(int width)
  {
    this.width = width;
    buffer = new float[width][channels];
    oldDataIndex = 0;
    latestDataIndex = 0;
    writeSimulatedData();
    if (component != null)
    {
      component.setSize(width, height);
      component.setPreferredSize(new Dimension(width, height));
      component.revalidate();
      component.repaint();
    }
  }

  @Getter("Height")
  public int getHeight()
  {
    return height;
  }

  @Setter("Height")
  public void setHeight(int height)
  {
    this.height = height;
    if (component != null)
    {
      component.setSize(width, height);
      component.setPreferredSize(new Dimension(width, height));
      component.revalidate();
      component.repaint();
    }
  }

  @Getter("Y Axis Min")
  public double getYMin()
  {
    return yMin;
  }

  @Setter("Y Axis Min")
  public void setYMin(double yMin)
  {
    this.yMin = (float)yMin;
    if (component != null)
      component.repaint();
  }

  @Getter("Y Axis Max")
  public double getYMax()
  {
    return yMax;
  }

  @Setter("Y Axis Max")
  public void setYMax(double yMax)
  {
    this.yMax = (float)yMax;
    if (component != null)
      component.repaint();
  }

  @Getter("X Axis")
  public int getDuration()
  {
    return duration;
  }

  @Setter("X Axis")
  public void setDuration(int duration)
  {
    this.duration = duration;
    if (component != null)
      component.repaint();
  }

  @Setter("Value 1")
  public void setValue1(double value)
  {
    newData((float)value, 0);
  }

  @Setter("Value 2")
  public void setValue2(double value)
  {
    newData((float)value, 1);
  }

  @Setter("Value 3")
  public void setValue3(double value)
  {
    newData((float)value, 2);
  }

  @Setter("Value 4")
  public void setValue4(double value)
  {
    newData((float)value, 3);
  }

  @Setter("Value 5")
  public void setValue5(double value)
  {
    newData((float)value, 4);
  }

  @Getter("Curve Color 1")
  public Color getCurveColor1()
  {
    return curveColors[0];
  }

  @Setter("Curve Color 1")
  public void setCurveColor1(Color color)
  {
    curveColors[0] = color;
    if (component != null) component.repaint();
  }

  @Getter("Curve Color 2")
  public Color getCurveColor2()
  {
    return curveColors[1];
  }

  @Setter("Curve Color 2")
  public void setCurveColor2(Color color)
  {
    curveColors[1] = color;
    if (component != null) component.repaint();
  }

  @Getter("Curve Color 3")
  public Color getCurveColor3()
  {
    return curveColors[2];
  }

  @Setter("Curve Color 3")
  public void setCurveColor3(Color color)
  {
    curveColors[2] = color;
    if (component != null) component.repaint();
  }

  @Getter("Curve Color 4")
  public Color getCurveColor4()
  {
    return curveColors[3];
  }

  @Setter("Curve Color 4")
  public void setCurveColor4(Color color)
  {
    curveColors[3] = color;
    if (component != null) component.repaint();
  }

  @Getter("Curve Color 5")
  public Color getCurveColor5()
  {
    return curveColors[4];
  }

  @Setter("Curve Color 5")
  public void setCurveColor5(Color color)
  {
    curveColors[4] = color;
    if (component != null) component.repaint();
  }

  @Getter("Horizonal Line 1")
  public double getHorizontalLine1Value()
  {
    return horizontalLineValues[0];
  }

  @Setter("Horizonal Line 1")
  public void setHorizontalLine1Value(double value)
  {
    horizontalLineValues[0] = (float)value;
    if (component != null) component.repaint();
  }

  @Getter("Horizonal Line 2")
  public double getHorizontalLine2Value()
  {
    return horizontalLineValues[1];
  }

  @Setter("Horizonal Line 2")
  public void setHorizontalLine2Value(double value)
  {
    horizontalLineValues[1] = (float)value;
    if (component != null) component.repaint();
  }

  @Getter("Horizonal Line 3")
  public double getHorizontalLine3Value()
  {
    return horizontalLineValues[2];
  }

  @Setter("Horizonal Line 3")
  public void setHorizontalLine3Value(double value)
  {
    horizontalLineValues[2] = (float)value;
    if (component != null) component.repaint();
  }

  @Getter("Horizonal Line 4")
  public double getHorizontalLine4Value()
  {
    return horizontalLineValues[3];
  }

  @Setter("Horizonal Line 4")
  public void setHorizontalLine4Value(double value)
  {
    horizontalLineValues[3] = (float)value;
    if (component != null) component.repaint();
  }

  @Getter("Horizontal Line 1 Color")
  public Color getHorizontal1Color()
  {
    return horizontalLineColors[0];
  }

  @Setter("Horizontal Line 1 Color")
  public void setHorzontal1Color(Color color)
  {
    horizontalLineColors[0] = color;
    if (component != null) component.repaint();
  }

  @Getter("Horizontal Line 2 Color")
  public Color getHorizontal2Color()
  {
    return horizontalLineColors[1];
  }

  @Setter("Horizontal Line 2 Color")
  public void setHorzontal2Color(Color color)
  {
    horizontalLineColors[1] = color;
    if (component != null) component.repaint();
  }

  @Getter("Horizontal Line 3 Color")
  public Color getHorizontal3Color()
  {
    return horizontalLineColors[2];
  }

  @Setter("Horizontal Line 3 Color")
  public void setHorzontal3Color(Color color)
  {
    horizontalLineColors[2] = color;
    if (component != null) component.repaint();
  }

  @Getter("Horizontal Line 4 Color")
  public Color getHorizontal4Color()
  {
    return horizontalLineColors[3];
  }

  @Setter("Horizontal Line 4 Color")
  public void setHorzontal4Color(Color color)
  {
    horizontalLineColors[3] = color;
    if (component != null) component.repaint();
  }

  @Getter("Horizontal Line 1 Area")
  public double getHorzontal1Area()
  {
    return horizontalLineArea[0];
  }

  @Setter("Horizontal Line 1 Area")
  public void setHorizontal1Area(double area)
  {
    horizontalLineArea[0] = (float)area;
    if (component != null) component.repaint();
  }

  @Getter("Horizontal Line 2 Area")
  public double getHorzontal2Area()
  {
    return horizontalLineArea[1];
  }

  @Setter("Horizontal Line 2 Area")
  public void setHorizontal2Area(double area)
  {
    horizontalLineArea[1] = (float)area;
    if (component != null) component.repaint();
  }

  @Getter("Horizontal Line 3 Area")
  public double getHorzontal3Area()
  {
    return horizontalLineArea[2];
  }

  @Setter("Horizontal Line 3 Area")
  public void setHorizontal3Area(double area)
  {
    horizontalLineArea[2] = (float)area;
    if (component != null) component.repaint();
  }

  @Getter("Horizontal Line 4 Area")
  public double getHorzontal4Area()
  {
    return horizontalLineArea[3];
  }

  @Setter("Horizontal Line 4 Area")
  public void setHorizontal4Area(double area)
  {
    horizontalLineArea[3] = (float)area;
    if (component != null) component.repaint();
  }

  /**
   *  Add new data into the internal buffer.
   */
  private void newData(float data, int channel)
  {
    // add new data to the average calculator
    if (!Float.isNaN(data))
    {
      sum[channel] += data;
      count[channel] ++;
    }
  }

  private void addToBuffer(float[] data)
  {
    latestDataIndex = (latestDataIndex+1) % width;
    for (int j=0; j<channels; j++)
      buffer[latestDataIndex][j] = data[j];
    if (latestDataIndex == oldDataIndex)
      oldDataIndex = (oldDataIndex+1) % width;
  }

  /**
   *  Return a custom graph painter component.
   */
  @Override
  protected JComponent createSwingComponent()
  {
    return new GraphPainter();
  }

  @Override
  public void configureVisualComponent()
  {
    super.configureVisualComponent();
    component.setSize(width, height);
    component.setPreferredSize(new Dimension(width, height));
    component.revalidate();
    component.repaint();
  }

  /**
   *  Write data from average calculator into the buffer.
   */
  public void cycleEnd()
  {
    // time length per pixel
    float timePerPixel = (float)duration / (float)width;
    // time since the last sample plot
    long currentTime = System.currentTimeMillis();
    int lastSampleDuration = (int)(currentTime - latestDataTime);
    if (lastSampleDuration > timePerPixel)
    {
      // write new data from average calculator to the buffer
      int pixels = Math.round(lastSampleDuration / timePerPixel);
      // calculate the average
      for (int i=0; i<channels; i++)
      {
	if (count[i] == 0)
	  sum[i] = Float.NaN;
        else
	  sum[i] /= count[i];
      }
      for (int i=0; i<pixels; i++) addToBuffer(sum);
      // initialize the average calculator
      for (int i=0; i<channels; i++)
      {
        sum[i] = 0.0f;
        count[i] = 0;
      }
      // actualize time
      latestDataTime = currentTime;
      // repaint graph
      if (component != null) component.repaint();
    }
  }

  public void cycleStart()
  {
  }

  public void processingStart()
  {
  }


  /**
   *
   *  Object which is responsible for graph painting.
   *
   */
  private class GraphPainter extends JComponent
  {

    /**
     *  Paint the whole graph.
     */
    @Override
    protected void paintComponent(Graphics g)
    {
      // paint border
      if (isOpaque())
      {
	g.setColor(getBackground());
	g.fillRect(0, 0, width, height);
      }

      // paint horizontal lines
      //g.setColor(getForeground());
      BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
	  BasicStroke.JOIN_ROUND, 1.0f, new float[] { 10.0f }, 0.0f);
      Graphics2D g2 = (Graphics2D)g;
      Stroke solid = g2.getStroke();
      g2.setStroke(dashed);
      for (int i=0; i<horizontalLinesCount; i++)
      {
	g2.setColor(horizontalLineColors[i]);
	float value = horizontalLineValues[i];
	if (!Float.isNaN(value))
	{
	  if (horizontalLineArea[i] > 0.0f)
	  {
            int y1 = height
	        - Math.round((value - yMin - horizontalLineArea[i])
		/ (yMax - yMin) * (float)height);
	    int y2 = height
	        - Math.round((value - yMin + horizontalLineArea[i])
		/ (yMax - yMin) * (float)height);
	    g2.setColor(horizontalLineColors[i].darker().darker());
	    g2.fillRect(0, Math.min(y1, y2), width, Math.abs(y2-y1));
            g2.setColor(horizontalLineColors[i]);
	    g2.drawLine(0, y1, width, y1);
	    g2.drawLine(0, y2, width, y2);
	  }
	  else
	  {
            int y = height
	        - Math.round((value - yMin) / (yMax - yMin) * (float)height);
	    g2.drawLine(0, y, width, y);
	  }
	}
      }

      // paint graph curves
      g2.setStroke(solid);
      int x1=0, y1=0, x2, y2;
      for (int channel=0; channel<channels; channel++)
      {
	g.setColor(curveColors[channel]);
	boolean firstPoint = false;
	for (int index=0; index < width; index++)
        {
	  // get a value to be painted
          int bufferIndex = latestDataIndex - index;
	  if (bufferIndex < 0) bufferIndex += width;
	  if (bufferIndex == oldDataIndex) break;
	  float value = buffer[bufferIndex][channel];
	  // calculate graph coordinates
	  if (Float.isNaN(value))
	  {
	    firstPoint = false;
	  }
	  else
	  {
            y2 = height - Math.round((value - yMin) / (yMax - yMin) * (float)height);
	    x2 = width - index - 1;
	    if (firstPoint) g.drawLine(x1, y1, x2, y2);
	    y1 = y2;
	    x1 = x2;
	    firstPoint = true;
          }
        }
      }
    }

  }

}
