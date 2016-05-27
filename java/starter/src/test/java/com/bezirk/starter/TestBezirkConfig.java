package com.bezirk.starter;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestBezirkConfig {
    BezirkConfig bezirkConfig = new BezirkConfig();

    //Commenting all as setting display enabled functionality is deprecated
    //@Test
    public void testIsDispalyEnabled() {
        bezirkConfig.setDisplayEnable("TRUE");
        assertTrue("DisplayEnable is not equal to the set value.", bezirkConfig.isDisplayEnabled());

        bezirkConfig.setDisplayEnable("true");
        assertTrue("DisplayEnable is not equal to the set value.", bezirkConfig.isDisplayEnabled());

        bezirkConfig.setDisplayEnable("enable");
        assertFalse("DisplayEnable is not equal to the set value.", bezirkConfig.isDisplayEnabled());
    }

    //Commenting all as setting display enabled functionality is deprecated
    //@Test
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
