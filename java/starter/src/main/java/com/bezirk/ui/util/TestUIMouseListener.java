package com.bezirk.ui.util;

import com.bezirk.devices.DeviceInterface;
import com.bezirk.ui.statckstatus.StackStatusUI;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author AJC6KOR
 */
public class TestUIMouseListener implements MouseListener {

    private final String uiType;

    private final Integer pingCount;
    private final String misMatchVersion;
    DeviceInterface deviceInterface;

    public TestUIMouseListener(String uiType, Integer pingCount,
                               String misMatchVersion, DeviceInterface deviceInterface) {
        super();
        this.uiType = uiType;

        this.pingCount = pingCount;
        this.misMatchVersion = misMatchVersion;
        this.deviceInterface = deviceInterface;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

        if ("sphereUI".equalsIgnoreCase(uiType)) {

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
