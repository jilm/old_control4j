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

import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.JComponent;
import control4j.scanner.Setter;
import control4j.scanner.Getter;

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

  @Getter(key="Width")
  public int getWidth()
  {
    return width;
  }

  @Setter(key="Width")
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

  @Getter(key="Height")
  public int getHeight()
  {
    return height;
  }

  @Setter(key="Height")
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

  @Getter(key="Y Axis Min")
  public double getYMin()
  {
    return yMin;
  }

  @Setter(key="Y Axis Min")
  public void setYMin(double yMin)
  {
    this.yMin = (float)yMin;
    if (component != null)
      component.repaint();
  }

  @Getter(key="Y Axis Max")
  public double getYMax()
  {
    return yMax;
  }

  @Setter(key="Y Axis Max")
  public void setYMax(double yMax)
  {
    this.yMax = (float)yMax;
    if (component != null)
      component.repaint();
  }

  @Getter(key="X Axis")
  public int getDuration()
  {
    return duration;
  }

  @Setter(key="X Axis")
  public void setDuration(int duration)
  {
    this.duration = duration;
    if (component != null)
      component.repaint();
  }

  @Setter(key="Value 1")
  public void setValue1(double value)
  {
    newData((float)value, 0);
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
  protected void configureVisualComponent()
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
    int timePerPixel = duration / width;
    // time since the last sample plot
    long currentTime = System.currentTimeMillis();
    int lastSampleDuration = (int)(currentTime - latestDataTime);
    System.out.println("cycle end; time/pixel: " + timePerPixel + "; current time: " + currentTime + "; last time: " + latestDataTime);
    if (lastSampleDuration > timePerPixel)
    {
      System.out.println("Writing data to buffer");
      // write new data from average calculator to the buffer
      int pixels = lastSampleDuration / timePerPixel;
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
      // paint graph
      g.setColor(getForeground());
      int x1=0, y1=0, x2, y2;
      for (int channel=0; channel<channels; channel++)
      {
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
