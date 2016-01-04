/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control4j.modules.papouch;

import control4j.Signal;
import control4j.application.Module;
import control4j.protocols.spinel.SpinelException;
import control4j.protocols.spinel.SpinelMessage;
import junit.framework.TestCase;

/**
 *
 * @author jlidi_000
 */
public class IMDA2Test extends TestCase {
    
    public IMDA2Test(String testName) {
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
     * Test of initialize method, of class IMDA2.
     */
    public void testInitialize() {
        System.out.println("initialize");
        Module definition = null;
        IMDA2 instance = new IMDA2();
        instance.initialize(definition);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of put method, of class IMDA2.
     */
    public void testPut() {
        System.out.println("put");
        Signal[] input = null;
        int inputLength = 0;
        IMDA2 instance = new IMDA2();
        instance.put(input, inputLength);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createMessage method, of class IMDA2.
     */
    public void testCreateMessage() throws SpinelException {
        System.out.println("createMessage");
        int address = 0x31;
        int channel = 0;
        double value = 1.0d;
        SpinelMessage expResult = new SpinelMessage(new int[] {
            0x2A,0x61,0x00,0x08,0x31,0x02,0x40,0x01,0x0F,0xFF,0xEA,0x0D}, 0, 12);
        SpinelMessage result = IMDA2.createMessage(address, channel, value);
        result.setSig(2);
        assertEquals(expResult, result);
    }
    
}
