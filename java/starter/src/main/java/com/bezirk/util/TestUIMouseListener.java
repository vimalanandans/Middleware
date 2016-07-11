package com.bezirk.util;

import com.bezirk.BezirkCompManager;
import com.bezirk.statckstatus.ui.StackStatusUI;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;

/**
 * @author AJC6KOR
 */
public class TestUIMouseListener implements MouseListener {

    private final String uiType;
    private final com.bezirk.commstest.ui.CommsTest commsTest;
    private final Integer pingCount;
    private final String misMatchVersion;

    public TestUIMouseListener(String uiType, com.bezirk.commstest.ui.CommsTest commsTest, Integer pingCount,
                               String misMatchVersion) {
        super();
        this.uiType = uiType;
        this.commsTest = commsTest;
        this.pingCount = pingCount;
        this.misMatchVersion = misMatchVersion;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

        if ("commsUI".equalsIgnoreCase(uiType)) {
            final String receivedMsgs = commsTest
                    .getSelectedServices(BezirkCompManager.getUpaDevice()
                            .getDeviceName() + ":" + pingCount);
            if (ValidatorUtility.checkForString(receivedMsgs)) {
                JOptionPane.showMessageDialog(null, receivedMsgs,
                        "DEVICES THAT RESPONDED TO PING MESSAGE",
                        JOptionPane.PLAIN_MESSAGE);
            }
        } else if ("sphereUI".equalsIgnoreCase(uiType)) {

            StackStatusUI.showStackStatusUI(false, misMatchVersion);
        }


    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        //Nothing to be done
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        //Nothing to be done

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        //Nothing to be done

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        //Nothing to be done

    }
}
