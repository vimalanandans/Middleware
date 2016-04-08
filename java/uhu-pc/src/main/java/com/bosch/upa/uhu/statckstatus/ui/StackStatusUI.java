package com.bosch.upa.uhu.statckstatus.ui;

import javax.swing.JOptionPane;

import com.bosch.upa.uhu.commons.UhuVersion;

public final class StackStatusUI {
    
    /* Utility Class. All methods are static. Adding private constructor to suppress PMD warnings.*/
    private StackStatusUI(){
        
    }

    public static void showStackStatusUI(boolean status,
            String receivedVersion) {
        if (status) {
            JOptionPane.showMessageDialog(null, "Uhu-is functioning normally",
                    "STACK-STATUS", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane
                    .showMessageDialog(
                            null,
                            "Expected message version: "
                                    + UhuVersion.getWireVersion()
                                    + "\n\n"
                                    + "Received message version: "
                                    + receivedVersion
                                    + "\n\n\n"
                                    + " Different Versions of Uhu exist in the network, there might be failure in the communication",
                            "STACK-STATUS", JOptionPane.ERROR_MESSAGE);

        }
    }
}
