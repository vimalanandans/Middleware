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
    private final Device device;

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
