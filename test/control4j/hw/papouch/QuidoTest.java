/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control4j.hw.papouch;

import control4j.protocols.spinel.SpinelMessage;
import junit.framework.TestCase;

/**
 *
 * @author jlidi_000
 */
public class QuidoTest extends TestCase {
    
    public QuidoTest(String testName) {
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
     * Test of getOneTimeTemperatureMeasurement method, of class Quido.
     */
    public void testGetOneTimeTemperatureMeasurement() throws Exception {
        System.out.println("getOneTimeTemperatureMeasurement");
        SpinelMessage message = new SpinelMessage(
                new int[] {0x2A, 0x61, 0x00, 0x08, 0x31, 0x02, 0x00, 0x01, 0x00, 0xF6, 0x42, 0x0D},
        0, 12);
        double expResult = 24.6d;
        double result = Quido.getOneTimeTemperatureMeasurement(message);
        assertEquals(expResult, result, 1e-3);
    }

    /**
     * Test of getBinaryInput method, of class Quido.
     */
    public void testGetBinaryInput() throws Exception {
        System.out.println("getBinaryInput");
        SpinelMessage message = new SpinelMessage(
        new int[] {0x2A,0x61,0x00,0x06,0x01,0x02,0x00,0xC2,0xA9,0x0D}, 0, 10);
        int expResult = 0xC2;
        int result = Quido.getBinaryInput(message);
        assertEquals(expResult, result);
    }
    
}
