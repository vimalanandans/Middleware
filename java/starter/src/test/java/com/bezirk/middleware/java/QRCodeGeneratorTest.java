package com.bezirk.middleware.java;

import com.bezirk.middleware.java.ui.QRCodeGenerator;

import javafx.application.Application;

public class QRCodeGeneratorTest {

    /**
     * Tests are commented as they break on jenkins
     */

    //@Test
    public void test() {
        Application.launch(QRCodeGenerator.class, "Data to be displayed in QRCode");
    }

    //@Test(expected = RuntimeException.class)
    public void testInvalidUsage() {
        Application.launch(QRCodeGenerator.class);
    }
}
