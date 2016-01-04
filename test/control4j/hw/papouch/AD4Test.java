/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control4j.hw.papouch;

import control4j.protocols.spinel.SpinelMessage;
import junit.framework.TestCase;
import static org.junit.Assert.assertArrayEquals;

/**
 *
 * @author jlidi_000
 */
public class AD4Test extends TestCase {
    
    public AD4Test(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getOneTimeMeasurement method, of class AD4.
     */
    public void testGetOneTimeMeasurement() throws Exception {
        System.out.println("getOneTimeMeasurement");
        SpinelMessage message = new SpinelMessage(
                new int[] {0x2A,0x61,0x00,0x15,0x31,0x02,0x00,0x01,0x80,0x15,
                    0xF3,0x02,0x80,0x00,0x00,0x03,0x80,0x22,0x7B,0x04,0x88,
                    0x28,0x2B,0x22,0x0D}, 0, 25);
        int[] expResult = new int[] {5619, 0, 8827, 10283};
        int[] result = AD4.getOneTimeMeasurement(message);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getStatus method, of class AD4.
     */
    public void testGetStatus() throws Exception {
        System.out.println("getStatus");
        SpinelMessage message = new SpinelMessage(
                new int[] {0x2A,0x61,0x00,0x15,0x31,0x02,0x00,0x01,0x80,0x15,
                    0xF3,0x02,0x80,0x00,0x00,0x03,0x80,0x22,0x7B,0x04,0x88,
                    0x28,0x2B,0x22,0x0D}, 0, 25);
        int[] expResult = new int[] {0x80, 0x80, 0x80, 0x88};
        int[] result = AD4.getStatus(message);
        assertArrayEquals(expResult, result);
    }
    
}
