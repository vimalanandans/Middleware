package com.bezirk.UhUbasics.protocols.parametricGUI;

import com.bezirk.Proxy.ProtocolRole;

/**
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 */
public class UserInputRole implements ProtocolRole {
    static final String evts[] =
            {UIChoiceEventReply.MsgLabel,
                    UIValuesEventReply.MsgLabel};
//	{ UIChoiceEventReply.MsgLabel};

    @Override
    public String getProtocol() {
        return UserInputRole.class.getSimpleName();
    }

    @Override
    public String[] getEvents() {
        return evts;
    }

    @Override
    public String[] getStreams() {
        return null;
    }
}
