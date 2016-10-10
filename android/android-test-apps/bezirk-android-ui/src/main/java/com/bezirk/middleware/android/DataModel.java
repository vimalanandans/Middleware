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
