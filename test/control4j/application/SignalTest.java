/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control4j.application;

import cz.lidinsky.tools.ToStringBuilder;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.Ignore;

/**
 *
 * @author jlidi_000
 */
public class SignalTest extends TestCase {
    
    public SignalTest(String testName) {
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
     * Test of setValueT_1Invalid method, of class Signal.
     */
    @Ignore
    public void testSetValueT_1Invalid() {
        System.out.println("setValueT_1Invalid");
        Signal instance = null;
        instance.setValueT_1Invalid();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setValueT_1 method, of class Signal.
     */
    @Ignore
    public void testSetValueT_1() {
        System.out.println("setValueT_1");
        String value = "";
        Signal instance = null;
        instance.setValueT_1(value);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isValueT_1Specified method, of class Signal.
     */
    @Ignore
    public void testIsValueT_1Specified() {
        System.out.println("isValueT_1Specified");
        Signal instance = null;
        boolean expResult = false;
        boolean result = instance.isValueT_1Specified();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isValueT_1Valid method, of class Signal.
     */
    @Ignore
    public void testIsValueT_1Valid() {
        System.out.println("isValueT_1Valid");
        Signal instance = null;
        boolean expResult = false;
        boolean result = instance.isValueT_1Valid();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getValueT_1 method, of class Signal.
     */
    @Ignore
    public void testGetValueT_1() {
        System.out.println("getValueT_1");
        Signal instance = null;
        double expResult = 0.0;
        double result = instance.getValueT_1();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of putTag method, of class Signal.
     */
    @Ignore
    public void testPutTag() {
        System.out.println("putTag");
        String name = "";
        Tag tag = null;
        Signal instance = null;
        instance.putTag(name, tag);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTagNames method, of class Signal.
     */
    @Ignore
    public void testGetTagNames() {
        System.out.println("getTagNames");
        Signal instance = null;
        Set<String> expResult = null;
        Set<String> result = instance.getTagNames();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTag method, of class Signal.
     */
    @Ignore
    public void testGetTag() {
        System.out.println("getTag");
        String name = "";
        Signal instance = null;
        Tag expResult = null;
        Tag result = instance.getTag(name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLabel method, of class Signal.
     */
    public void testGetLabel() {
        System.out.println("getLabel");
        Signal instance = new Signal("signal1");
        String expResult = "signal1";
        String result = instance.getLabel();
        assertEquals(expResult, result);
        instance.setLabel("sig1");
        expResult = "sig1";
        result = instance.getLabel();
        assertEquals(expResult, result);
    }

    /**
     * Test of setLabel method, of class Signal.
     */
    @Ignore
    public void testSetLabel() {
        System.out.println("setLabel");
        String label = "";
        Signal instance = null;
        instance.setLabel(label);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUnit method, of class Signal.
     */
    @Ignore
    public void testGetUnit() {
        System.out.println("getUnit");
        Signal instance = null;
        String expResult = "";
        String result = instance.getUnit();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setUnit method, of class Signal.
     */
    @Ignore
    public void testSetUnit() {
        System.out.println("setUnit");
        String unit = "";
        Signal instance = null;
        instance.setUnit(unit);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class Signal.
     */
    @Ignore
    public void testToString() {
        System.out.println("toString");
        ToStringBuilder builder = null;
        Signal instance = null;
        instance.toString(builder);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
