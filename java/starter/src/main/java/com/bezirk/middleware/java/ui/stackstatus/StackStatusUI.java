/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.java.ui.stackstatus;

import com.bezirk.middleware.core.util.BezirkVersion;

import javax.swing.JOptionPane;

public final class StackStatusUI {

    /* Utility Class. All methods are static. Adding private constructor to suppress PMD warnings.*/
    private StackStatusUI() {

    }

    public static void showStackStatusUI(boolean status,
                                         String receivedVersion) {
        if (status) {
            JOptionPane.showMessageDialog(null, "Bezirk-is functioning normally",
                    "STACK-STATUS", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane
                    .showMessageDialog(
                            null,
                            "Expected message version: "
                                    + BezirkVersion.getWireVersion()
                                    + "\n\n"
                                    + "Received message version: "
                                    + receivedVersion
                                    + "\n\n\n"
                                    + " Different Versions of Bezirk exist in the network, there might be failure in the communication",
                            "STACK-STATUS", JOptionPane.ERROR_MESSAGE);

        }
    }
}
