package com.bezirk.comms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMsg;
import org.zyre.ZreInterface;

public class JyreCommsSend {
    private static final Logger logger = LoggerFactory.getLogger(JyreCommsSend.class);

    private ZreInterface zre = null;
    private String zreGroup = null;

    public JyreCommsSend(String joinGroup) {
        zre = new ZreInterface();
        this.zreGroup = joinGroup;
        zre.join(joinGroup);

    }

    public boolean sendMessage(final byte[] trasnsmitMsg, final boolean isEvent) {
        ZMsg outgoing = new ZMsg();
        outgoing.addString(zreGroup);
        //outgoing.add(ctrlLedger.getSerializedMessage());
        outgoing.addString("Hello");

        //logger.debug("Sending shout: " + ctrlLedger.getSerializedMessage());
        zre.shout(outgoing);



		/*ZMsg outgoing = new ZMsg();
        outgoing.addString(unicastMsg.getRecipient().device);
		outgoing.addString(ctrlLedger.getSerializedMessage());

		logger.debug("Sending whisper: " + ctrlLedger.getSerializedMessage());
		zre.whisper(outgoing);*/
        return true;
    }
}
