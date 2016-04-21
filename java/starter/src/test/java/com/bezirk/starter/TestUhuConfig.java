package com.bezirk.starter;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestUhuConfig {
    com.bezirk.starter.UhuConfig uhuConfig = new com.bezirk.starter.UhuConfig();


    @Test
    public void testIsDispalyEnabled() {

        uhuConfig.setDisplayEnable("TRUE");
        assertTrue("DisplayEnable is not equal to the set value.", uhuConfig.isDisplayEnabled());

        uhuConfig.setDisplayEnable("true");
        assertTrue("DisplayEnable is not equal to the set value.", uhuConfig.isDisplayEnabled());

        uhuConfig.setDisplayEnable("enable");
        assertFalse("DisplayEnable is not equal to the set value.", uhuConfig.isDisplayEnabled());

    }

    @Test
    public void testGetDisplayEnable() {

        String displayEnable = Boolean.FALSE.toString();
        uhuConfig.setDisplayEnable(displayEnable);
        assertEquals("DisplayEnable is not equal to the set value.", displayEnable, uhuConfig.getDisplayEnable());
    }

    @Test
    public void testGetDataPath() {
        String dataPath = "dataPath";
        uhuConfig.setDataPath(dataPath);
        assertEquals("TestDataPath is not equal to the set value.", dataPath, uhuConfig.getDataPath());


    }

}
