package com.bezirk.starter;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestUhuConfig {
    BezirkConfig bezirkConfig = new BezirkConfig();


    @Test
    public void testIsDispalyEnabled() {

        bezirkConfig.setDisplayEnable("TRUE");
        assertTrue("DisplayEnable is not equal to the set value.", bezirkConfig.isDisplayEnabled());

        bezirkConfig.setDisplayEnable("true");
        assertTrue("DisplayEnable is not equal to the set value.", bezirkConfig.isDisplayEnabled());

        bezirkConfig.setDisplayEnable("enable");
        assertFalse("DisplayEnable is not equal to the set value.", bezirkConfig.isDisplayEnabled());

    }

    @Test
    public void testGetDisplayEnable() {

        String displayEnable = Boolean.FALSE.toString();
        bezirkConfig.setDisplayEnable(displayEnable);
        assertEquals("DisplayEnable is not equal to the set value.", displayEnable, bezirkConfig.getDisplayEnable());
    }

    @Test
    public void testGetDataPath() {
        String dataPath = "dataPath";
        bezirkConfig.setDataPath(dataPath);
        assertEquals("TestDataPath is not equal to the set value.", dataPath, bezirkConfig.getDataPath());


    }

}
