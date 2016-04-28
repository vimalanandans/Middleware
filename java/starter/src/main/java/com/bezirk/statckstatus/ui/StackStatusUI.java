package com.bezirk.statckstatus.ui;

import com.bezirk.commons.BezirkVersion;

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
