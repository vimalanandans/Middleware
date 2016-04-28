package com.bezirk.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.router.RouterNanoHTTPD.DefaultHandler;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

/**
 * A Response route for retrieving response data.
 *
 * @author PIK6KOR
 */
public class BezirkRestResponseHandler extends DefaultHandler {
    private static final Logger logger = LoggerFactory.getLogger(BezirkRestResponseHandler.class);

    public String getText(Map<String, String> urlParams,
                          IHTTPSession session) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * returns a JSON mime type which will be set to the Response
     */
    @Override
    public String getMimeType() {
        return "application/json";
    }


    /**
     * returns a ok status.which will be set to the response.
     */
    @Override
    public NanoHTTPD.Response.IStatus getStatus() {
        return NanoHTTPD.Response.Status.OK;
    }

    /**
     * returns the response to the client.
     */
    @Override
    public Response get(UriResource uriResource, Map<String, String> urlParams,
                        IHTTPSession session) {

        //Retrieve the unique msgid from url query parameter
        Integer eventUniqueMsgId = null;

        for (Map.Entry<String, String> entry : urlParams.entrySet()) {
            String Id = entry.getValue();
            eventUniqueMsgId = Integer.valueOf(Id);
        }

        if (logger.isDebugEnabled()) logger.debug("Retrieving response data for key:{}", eventUniqueMsgId);

        List<String> clientResponse = BezirkRestCommsManager.getInstance().getResponseMap().get(eventUniqueMsgId);

        if (clientResponse == null || clientResponse.size() <= 0) {
            if (logger.isDebugEnabled())
                logger.debug("HTTP Comms: No response for key {}", eventUniqueMsgId);
            // send a error code back to client
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NO_CONTENT, getMimeType(), null, 0);
        }

        //remove the data after it has been retrieved, fail-safe removing from the map.
        for (Iterator<Map.Entry<Integer, List<String>>> it = BezirkRestCommsManager.getInstance().getResponseMap().entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, List<String>> entry = it.next();
            if (entry.getKey().equals(eventUniqueMsgId)) {
                it.remove();
            }
        }

        //send the response string to client
        String responseString = clientResponse.toString();
        ByteArrayInputStream inp = new ByteArrayInputStream(responseString.getBytes());
        int size = responseString.getBytes().length;
        return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), inp, size);

    }

    @Override
    public String getText() {
        return "Not Yet Implemented";
    }
}
