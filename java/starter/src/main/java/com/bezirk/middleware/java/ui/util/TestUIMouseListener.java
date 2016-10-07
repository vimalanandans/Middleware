package com.bezirk.middleware.java.ui.util;

import com.bezirk.middleware.core.device.Device;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author AJC6KOR
 */
public class TestUIMouseListener implements MouseListener {

    private final String uiType;

    private final Integer pingCount;
    private final String misMatchVersion;
    private Device device;

    public TestUIMouseListener(String uiType, Integer pingCount,
                               String misMatchVersion, Device device) {
        super();
        this.uiType = uiType;

        this.pingCount = pingCount;
        this.misMatchVersion = misMatchVersion;
        this.device = device;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

        if ("sphereUI".equalsIgnoreCase(uiType)) {

            com.bezirk.middleware.java.ui.stackstatus.StackStatusUI.showStackStatusUI(false, misMatchVersion);
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
