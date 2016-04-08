package com.bezirk.rest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.control.messages.EventLedger;
import com.bosch.upa.uhu.rest.BezirkHttpRequest;
import com.bosch.upa.uhu.rest.BezirkRequestTranslator;
import com.bosch.upa.uhu.rest.BezirkRestCommsManager;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.router.RouterNanoHTTPD.DefaultHandler;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

/**
 * A NanoHTTPD default handler which will handle the requests routed to /bezirk and /bezirk/{requestID}
 * 
 * @author PIK6KOR
 *
 */
public class BezirkRestRequestHandler extends DefaultHandler{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BezirkRestRequestHandler.class);

	//A rolling map of limited size of 100
	private Map<Integer, List<String>> responseMap;
	
	//restcomms manager
	private BezirkRestCommsManager bezirkCommsManager;
	
	//translator utilities class
	private final BezirkRequestTranslator translator = new BezirkRequestTranslator();
	
	public BezirkRestRequestHandler() {
		//initialize the comms to the one which is active
		this.bezirkCommsManager = BezirkRestCommsManager.getInstance();
		
		if(bezirkCommsManager.getUhuComms() == null){
			//Logger.e("Comms has not yet initialized!!!");
			return;
		}
		responseMap = bezirkCommsManager.getResponseMap();
		
	}
	
	@Override
	public String getText() {
		LOGGER.debug("Called GET service to BEzirkRestRequestHandler!!!, Feature not supported");
		return "Not Yet Implemented!!";
	}

	/**
	 * 
	 * @param urlParams
	 * @param session
	 * @return
	 */
	private String callService(Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
		
		//sphere which will be used for all the communication.... will retrieved from UI and saved in the BezirkCommsManager object.
		String selectedSphere = bezirkCommsManager.getSelectedSphere();
		
		if(selectedSphere == null){
			LOGGER.debug("Sphere not selected!!. Select the HTTP Comms Sphere");
			//means the user has not set the sphere!!! This has to be set before using the HTTPComms
			return "ERROR:SELECT_SPHERE";
		}

		//Read the query parameters and prepare the Bezirk request object.
		final BezirkHttpRequest requestObject = constructRequestObject(session);
		requestObject.setEventSphere(selectedSphere);
		
		LOGGER.debug("UniqueID generated is :"+requestObject.getUniqueID());
		
		//put the request with unique key, will be the random integer
		List<String> emptyList = new ArrayList<String>();
		responseMap.put(requestObject.getUniqueID(), emptyList);

		//construct a eventledger from the client parameters
		final EventLedger eventLedger = translator.translateRequestToEventLedger(requestObject);

		//add the eventledger to the queue
		bezirkCommsManager.getRequestQueue().add(eventLedger);
		
		LOGGER.debug("sending http rest messge to comms for topic"+requestObject.getEventTopic());
		return requestObject.getUniqueID().toString();
	}

	
	/**
	 * This method constructs the BezirkHttpRequest object by extracting the parameters of the HTTP Body
	 * @param session
	 * @return
	 */
	private BezirkHttpRequest constructRequestObject(
			NanoHTTPD.IHTTPSession session) {
		BezirkHttpRequest requestObject = new BezirkHttpRequest();

		String httpBody = retreiveHttpBody(session);
		requestObject.setEventMsg(httpBody);
		
		
		//generate a random int, for uniqueness
		Random generator = new Random(); 
		Integer randomInt = generator.nextInt(10000) + 1;
		requestObject.setUniqueID(randomInt);

		String uniqueEventId = "THIS-SERVICE-ID-IS-SPOOFED-$"+randomInt;
		requestObject.setUniqueEventId(uniqueEventId);
		
		String serviceId = "THIS-SERVICE-ID-IS-HTTP-SPOOFED";
		requestObject.setEventServiceId(serviceId);
				for (Map.Entry<String, String> entry : session.getHeaders().entrySet()) {
			switch (entry.getKey()) {
				case "bezirk_event_topic":{
					requestObject.setEventTopic(entry.getValue());
					break;
				}
	
				case "bezirk_expected_response_type":{
					requestObject.setExpectedResponseType(entry.getValue());
					break;
				}
	
				default:{
					break;
				}
			
			}

		}
		return requestObject;
	}
	
	/**
	 * Retreive the HTTP Body form  the http post request
	 * @param session
	 * @return
	 */
	private String retreiveHttpBody(NanoHTTPD.IHTTPSession session) {
		//Test
		String inputStreamString = null;
		try {
			
			/*LOGGER.debug("Retriving http body :"+System.currentTimeMillis());
			isr = new InputStreamReader(session.getInputStream(),"utf-8");
			
			LOGGER.debug("Converted InputStream :"+System.currentTimeMillis());
			BufferedReader br = new BufferedReader(isr);
			// From now on, the right way of moving from bytes to utf-8 characters:

			int b;
			while ((b = br.read()) != -1) {
				buf.append((char) b);
			}
			
			LOGGER.debug("Read Complete :"+System.currentTimeMillis());

			br.close();
			isr.close();*/
			
			LOGGER.debug("Retriving http body :"+System.currentTimeMillis());
			inputStreamString = new Scanner(session.getInputStream(),"UTF-8").next();
			
			LOGGER.debug("Body Read complete :"+System.currentTimeMillis());
			LOGGER.debug("HTTP rest request body is :"+inputStreamString);
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
	 * 
	 * In the request handler we are supporting only POST, All the GET operation to return the response is supported in {@link BezirkRestResponseHandler}
	 * 
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
		 * Location: /bezirk/service/response/123456789
		 * Wait: 5000
		 * Push:true // retry at certain intervals
		 * 
		 * 
		 *  Push mechanism
		 * 
		 */
		
		//set the response as discussed!!!
		NanoHTTPD.Response res = NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), "");
		res.addHeader("response_uri", "/bezirk/service/response/"+text);
		res.addHeader("response_wait", "5000");
		res.addHeader("response_retry", "5000");
		
		return res;
		
		
	}

}
