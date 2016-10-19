package com.bezirk.middleware.core.common;

import com.bezirk.middleware.core.util.VersionManager;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class VersionManagerTest {

    @Test
    public void test() {
        assertNotNull("VersionManager could not be retrieved.", VersionManager.getBezirkVersion());
    }
}
