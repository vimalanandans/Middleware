package com.bezirk.rest;

import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.Ledger;
import com.bezirk.control.messages.UnicastHeader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BezirkRestCallBackImpl implements BezirkRestCallBack {
    //logger
    private static final Logger logger = LoggerFactory.getLogger(BezirkRestCallBackImpl.class);

    //translator utilities class
    private final BezirkRequestTranslator translator = new BezirkRequestTranslator();

    private BezirkRestCommsManager bezirkCommsManager;

    public BezirkRestCallBackImpl() {
        this.bezirkCommsManager = BezirkRestCommsManager.getInstance();
    }

    /**
     * appends the response to the map.
     */
    @Override
    public void callBackForResponse(Ledger event) {
        //Response will be the EventLedger
        if (!(event instanceof EventLedger)) {
            throw new AssertionError("Expected event to be an instance of EventLedger, but it isn't");
        }

        EventLedger eventLedger = (EventLedger) event;

        //translate Ledger to Response object which will be sent to zirk
        String responseString = translator.translateEventToClientResponse(eventLedger);

        //update the response in the map, it will be picked and returned to the client during the GET call.

        //extract the uniqueID and update it to the map
        //TODO :: Parse header either to UnicastHeader or MulticastHeader, and extract it.
        UnicastHeader header = (UnicastHeader) eventLedger.getHeader();
        Integer key = extractUniqueKey(header.getRecipient().zirkId.getBezirkEventId());

        if (logger.isDebugEnabled()) logger.debug("Response for Key ::{} is :: {}", key, responseString);
        bezirkCommsManager.appendResponseToMap(key, responseString);
    }

    /**
     * extracts the uniqueid received from the zirk ID
     *
     * @param zirkID
     * @return
     */
    private Integer extractUniqueKey(String zirkID) {
        String key;
        int startIndex = zirkID.indexOf("$");
        if (startIndex > -1) {
            key = zirkID.substring(startIndex + 1, zirkID.length());
        }   else {
            key = "-1";
        }

        logger.debug("Extracted the key from zirk id: {}", key);

        return Integer.valueOf(key);
    }
}
