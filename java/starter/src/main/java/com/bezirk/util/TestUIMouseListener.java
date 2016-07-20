package com.bezirk.util;

import com.bezirk.devices.DeviceInterface;
import com.bezirk.ui.statckstatus.StackStatusUI;
import com.bezirk.ui.commstest.CommsTest;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;

/**
 * @author AJC6KOR
 */
public class TestUIMouseListener implements MouseListener {

    private final String uiType;
    private final CommsTest commsTest;
    private final Integer pingCount;
    private final String misMatchVersion;
    DeviceInterface deviceInterface;

    public TestUIMouseListener(String uiType, CommsTest commsTest, Integer pingCount,
                               String misMatchVersion, DeviceInterface deviceInterface) {
        super();
        this.uiType = uiType;
        this.commsTest = commsTest;
        this.pingCount = pingCount;
        this.misMatchVersion = misMatchVersion;
        this.deviceInterface = deviceInterface;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

        if ("commsUI".equalsIgnoreCase(uiType)) {
            final String receivedMsgs = commsTest
                    .getSelectedServices(deviceInterface.getDeviceName() + ":" + pingCount);
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
