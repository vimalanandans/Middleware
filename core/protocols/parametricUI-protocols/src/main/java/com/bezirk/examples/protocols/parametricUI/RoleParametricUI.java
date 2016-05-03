/*
 * @author: Joao de Sousa (CR/RTC3-NA)
 */
package com.bezirk.examples.protocols.parametricUI;

import com.bezirk.middleware.messages.ProtocolRole;

/**
 * Role of a service offering a parametric UI
 */
public class RoleParametricUI extends ProtocolRole {

    private static final String evts[] =
            {NoticeUIshowPic.TOPIC,
                    NoticeUIshowText.TOPIC,
                    NoticeUIshowVideo.TOPIC,
                    RequestUIinputValues.TOPIC,
                    RequestUImultipleChoice.TOPIC,
                    RequestUIpickOne.TOPIC};

    @Override
    public String getRoleName() {
        return RoleParametricUI.class.getSimpleName();
    }

    @Override
    public String getDescription() {
        return "Showing text, pictures, and video to users; requesting user input on values and choices";
    }

    @Override
    public String[] getEventTopics() {
        return evts == null ? null : evts.clone();
    }

    @Override
    public String[] getStreamTopics() {
        return null;
    }
}
