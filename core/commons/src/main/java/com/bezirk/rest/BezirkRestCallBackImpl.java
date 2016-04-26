package com.bezirk.rest;

import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.Ledger;
import com.bezirk.control.messages.UnicastHeader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BezirkRestCallBackImpl implements BezirkRestCallBack {
    //logger
    private static final Logger LOGGER = LoggerFactory.getLogger(BezirkRestCallBackImpl.class);

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
        EventLedger eventLedger = (EventLedger) event;

        //translate Ledger to Response object which will be sent to zirk
        String responseString = translator.translateEventToClientResponse(eventLedger);

        //update the response in the map, it will be picked and returned to the client during the GET call.

        //extract the uniqueID and update it to the map
        //TODO :: Parse header either to UnicastHeader or MulticastHeader, and extract it.
        UnicastHeader header = (UnicastHeader) eventLedger.getHeader();
        Integer key = extractUniqueKey(header.getRecipient().zirkId.getBezirkEventId());

        LOGGER.debug("Response for Key ::" + key + " is :: " + responseString);
        bezirkCommsManager.appendResponseToMap(key, responseString);
    }

    /**
     * extracts the uniqueid recived from the zirk ID
     *
     * @param serviceID
     * @return
     */
    private Integer extractUniqueKey(String serviceID) {

        String key = null;
        int startIndex = serviceID.indexOf("$");
        if (startIndex > -1) {
            key = serviceID.substring(startIndex + 1, serviceID.length());
        }

        LOGGER.debug("Extracted the key from zirk id : " + key);
        return Integer.valueOf(key);

    }

}
