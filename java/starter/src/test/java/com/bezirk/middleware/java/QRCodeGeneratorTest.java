package com.bezirk.middleware.java;

import com.bezirk.middleware.java.ui.QRCodeGenerator;

import org.junit.Test;

import javafx.application.Application;

public class QRCodeGeneratorTest {
    //@Test
    public void test() {
        Application.launch(QRCodeGenerator.class, "Data to be displayed in QRCode");
    }

    //@Test(expected = RuntimeException.class)
    public void testInvalidUsage() {
        Application.launch(QRCodeGenerator.class);
    }
}
