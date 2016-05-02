package com.bezirk.rest;

import com.bezirk.control.messages.EventLedger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.router.RouterNanoHTTPD.DefaultHandler;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

/**
 * A NanoHTTPD default handler which will handle the requests routed to /bezirk and /bezirk/{requestID}
 *
 * @author PIK6KOR
 */
public class BezirkRestRequestHandler extends DefaultHandler {

    private static final Logger logger = LoggerFactory.getLogger(BezirkRestRequestHandler.class);
    //translator utilities class
    private final BezirkRequestTranslator translator = new BezirkRequestTranslator();
    //A rolling map of limited size of 100
    private Map<Integer, List<String>> responseMap;
    //restcomms manager
    private BezirkRestCommsManager bezirkCommsManager;

    public BezirkRestRequestHandler() {
        //initialize the comms to the one which is active
        this.bezirkCommsManager = BezirkRestCommsManager.getInstance();

        if (bezirkCommsManager.getBezirkComms() == null) {
            //Logger.e("Comms has not yet initialized!!!");
            return;
        }
        responseMap = bezirkCommsManager.getResponseMap();

    }

    @Override
    public String getText() {
        logger.debug("Called GET zirk to BezirkRestRequestHandler!!!, Feature not supported");
        return "Not Yet Implemented!!";
    }

    /**
     * @param urlParams
     * @param session
     * @return
     */
    private String callService(Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {

        //sphere which will be used for all the communication.... will retrieved from UI and saved in the BezirkCommsManager object.
        String selectedSphere = bezirkCommsManager.getSelectedSphere();

        if (selectedSphere == null) {
            logger.debug("sphere not selected!!. Select the HTTP Comms sphere");
            //means the user has not set the sphere!!! This has to be set before using the HTTPComms
            return "ERROR:SELECT_SPHERE";
        }

        //Read the query parameters and prepare the Bezirk request object.
        final BezirkHttpRequest requestObject = constructRequestObject(session);
        requestObject.setEventSphere(selectedSphere);

        logger.debug("UniqueID generated is :" + requestObject.getUniqueID());

        //put the request with unique key, will be the random integer
        List<String> emptyList = new ArrayList<String>();
        responseMap.put(requestObject.getUniqueID(), emptyList);

        //construct a eventledger from the client parameters
        final EventLedger eventLedger = translator.translateRequestToEventLedger(requestObject);

        //add the eventledger to the queue
        bezirkCommsManager.getRequestQueue().add(eventLedger);

        logger.debug("sending http rest messge to comms for topic" + requestObject.getEventTopic());
        return requestObject.getUniqueID().toString();
    }


    /**
     * This method constructs the BezirkHttpRequest object by extracting the parameters of the HTTP Body
     *
     * @param session
     * @return
     */
    private BezirkHttpRequest constructRequestObject(
            NanoHTTPD.IHTTPSession session) {
        BezirkHttpRequest requestObject = new BezirkHttpRequest();

        String httpBody = retrieveHttpBody(session);
        requestObject.setEventMsg(httpBody);


        //generate a random int, for uniqueness
        Random generator = new Random();
        Integer randomInt = generator.nextInt(10000) + 1;
        requestObject.setUniqueID(randomInt);

        String uniqueEventId = "THIS-SERVICE-ID-IS-SPOOFED-$" + randomInt;
        requestObject.setUniqueEventId(uniqueEventId);

        String serviceId = "THIS-SERVICE-ID-IS-HTTP-SPOOFED";
        requestObject.setEventServiceId(serviceId);

        for (Map.Entry<String, String> entry : session.getHeaders().entrySet()) {
            switch (entry.getKey()) {
                case "bezirk_event_topic": {
                    requestObject.setEventTopic(entry.getValue());
                    break;
                }

                case "bezirk_expected_response_type": {
                    requestObject.setExpectedResponseType(entry.getValue());
                    break;
                }

                default: {
                    break;
                }

            }

        }
        return requestObject;
    }

    /**
     * Retreive the HTTP Body form  the http post request
     *
     * @param session
     * @return
     */
    private String retrieveHttpBody(NanoHTTPD.IHTTPSession session) {
        //Test
        String inputStreamString = null;
        try {

			/*logger.debug("Retriving http body :"+System.currentTimeMillis());
			isr = new InputStreamReader(session.getInputStream(),"utf-8");
			
			logger.debug("Converted InputStream :"+System.currentTimeMillis());
			BufferedReader br = new BufferedReader(isr);
			// From now on, the right way of moving from bytes to utf-8 characters:

			int b;
			while ((b = br.read()) != -1) {
				buf.append((char) b);
			}
			
			logger.debug("Read Complete :"+System.currentTimeMillis());

			br.close();
			isr.close();*/

            logger.debug("Retriving http body :" + System.currentTimeMillis());
            inputStreamString = new Scanner(session.getInputStream(), "UTF-8").next();

            logger.debug("Body Read complete :" + System.currentTimeMillis());
            logger.debug("HTTP rest request body is :" + inputStreamString);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return inputStreamString;
    }

    /**
     * returns a JSON mime type which will be set to the Response
     */
    @Override
    public String getMimeType() {
        return "application/json";
    }


    /**
     * returns a Accepted status.which will be set to the response.
     */
    @Override
    public NanoHTTPD.Response.IStatus getStatus() {
        return NanoHTTPD.Response.Status.ACCEPTED;
    }


    /**
     * Handles all the GET request!!
     * <p>
     * In the request handler we are supporting only POST, All the GET operation to return the
     * response is supported in {@link BezirkRestResponseHandler}
     * </p>
     */
    @Override
    public NanoHTTPD.Response get(UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        String text = "NOT SUPPORTED";
        ByteArrayInputStream inp = new ByteArrayInputStream(text.getBytes());
        int size = text.getBytes().length;
        return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), inp, size);
    }


    /**
     * handles the POST request!!!
     */
    @Override
    public Response post(UriResource uriResource,
                         Map<String, String> urlParams, IHTTPSession session) {

        String text = callService(urlParams, session);

        //return the response with wait 5sec and a response location  and 	http://localhost:9090/bezirk/service/response/{uniqueMsgId}
		/*
		 * sample response
		 * 
		 * 202
		 * Content-Type: application/json
		 * Location: /bezirk/zirk/response/123456789
		 * Wait: 5000
		 * Push:true // retry at certain intervals
		 * 
		 * 
		 *  Push mechanism
		 * 
		 */

        //set the response as discussed!!!
        NanoHTTPD.Response res = NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), "");
        res.addHeader("response_uri", "/bezirk/zirk/response/" + text);
        res.addHeader("response_wait", "5000");
        res.addHeader("response_retry", "5000");

        return res;


    }

}
