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
package com.bezirk.middleware.android;

public class DataModel {
    private final Integer imageId;
    private final String titleText;
    private final boolean toggleButtonEnable; // ToggleButton Enable or disable
    private String hintText;
    private boolean toggleButtonState; // ToggleButton on/off when enabled
    private boolean icon;

    DataModel(Integer imageID, String title, String hint, boolean buttonEnable, boolean buttonState, boolean iconState) {

        imageId = imageID;

        titleText = title;

        hintText = hint;

        toggleButtonEnable = buttonEnable;

        toggleButtonState = buttonState;

        this.icon = iconState;
    }

    public String getHintText() {
        return hintText;
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
    }

    public String getTitleText() {
        return titleText;
    }

    public Integer getImageId() {
        return imageId;
    }

    public boolean isToggleButtonEnable() {
        return toggleButtonEnable;
    }

    public boolean isToggleButtonState() {
        return toggleButtonState;
    }

    public void setToggleButtonState(boolean toggleButtonState) {
        this.toggleButtonState = toggleButtonState;
    }

    public boolean isIcon() {
        return icon;
    }

    public void setIcon(boolean icon) {
        this.icon = icon;
    }
}
