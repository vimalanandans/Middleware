//FIXME: should we remove the "upa" portion of the package names?
package com.bezirk.pipe.core;

import com.bezirk.control.messages.Header;
import com.bezirk.control.messages.MulticastHeader;
import com.bezirk.control.messages.UnicastHeader;
import com.bezirk.control.messages.pipes.CloudResponse;
import com.bezirk.control.messages.pipes.CloudStreamResponse;
import com.bezirk.control.messages.pipes.PipeHeader;
import com.bezirk.control.messages.pipes.PipeMulticastHeader;
import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.CloudPipe;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.GetStreamRequest;
import com.bezirk.pipe.cloud.CloudPipeClientImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * Class used to hold data needed to send
 */
public class RemoteSender implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RemoteSender.class);

    /**
     * Reference to the pipe registry so that pipes can be searched for
     */
    protected PipeRegistry pipeRegistry = null;

    protected String serializedEvent = null;

    protected Header uhuHeader = null;
    // Used for callback to return result of remote send back to the pipe monitor
    // so that it can be returned to local services
    protected PipeManagerImpl pipeMonitor = null;
    protected String certFileName = null;
    private PipeHeader pipeHeader = null;

    public RemoteSender() {
        // no-arg constructor intentional
    }

    /**
     * Used to validate that data members are set correctly before run() executes
     */
    protected boolean validateDataMembers() {
        String err = "Cannot send. ";
        boolean valid = true;

        if (pipeMonitor == null) {
            log.error(err + "pipeMonitor is null");
            valid = false;
        }
        if (serializedEvent == null) {
            log.error(err + "serializedEvent is null");
            valid = false;
        }
        if (pipeRegistry == null) {
            log.error(err + "pipeRegistry is null");
            valid = false;
        }
        if (uhuHeader == null) {
            log.error(err + "uhuHeader is null");
            valid = false;
        }
        if (certFileName == null) {
            log.error(err + "certFileName is null");
            valid = false;
        }

        return valid;
    }

    public void run() {
        if (!validateDataMembers()) {
            log.error("Cannot execute PipeSender.run() b/c data members are not specified correctly");
            return;
        }

        if (uhuHeader instanceof UnicastHeader) {
            log.error("Unicast send to a pipe not yet supported. Can't execute RemoteSender.run()");
            return;
        }

        Address address = ((MulticastHeader) uhuHeader).getAddress();

        PipeMulticastHeader pipeMulticastHdr = new PipeMulticastHeader();
        pipeMulticastHdr.setSenderSEP(uhuHeader.getSenderSEP());
        pipeMulticastHdr.setAddress(address);
        pipeMulticastHdr.setTopic(uhuHeader.getTopic());
        pipeHeader = pipeMulticastHdr;

        // Check if the event's location represents a registered pipe
        Pipe pipe = address.getPipe();
        if (!pipeRegistry.isRegistered(pipe)) {
            log.error("Pipe is not registered: " + pipe);
            return;
        }

        // Pipe is registered, send the event to the pipe
        log.info("Sending to pipe: " + pipe);

        if (pipe instanceof CloudPipe) {
            doCloudPipeSend(serializedEvent, (CloudPipe) pipe);
        } else {
            log.error("Unknown pipe type: " + pipe.getClass().getCanonicalName());
        }
    }

    protected void doCloudPipeSend(String serializedEvent, CloudPipe cloudPipe) {
        Event event = Event.fromJson(serializedEvent, Event.class);

        URL url;
        try {
            url = cloudPipe.getURI().toURL();
        } catch (MalformedURLException e) {
            log.error("Can't send to cloud pipe.  URI is not a valid URL: " + cloudPipe.getURI(), e);
            return;
        }

        // TODO: check outgoing event to see if it conforms to allowedOut PipePolicy

        com.bezirk.pipe.cloud.CloudPipeClient client = new CloudPipeClientImpl(url, certFileName);
        log.info("Sending to pipe: " + cloudPipe);

        // Send request for a stream
        if (event.topic.equals(GetStreamRequest.TOPIC)) {
            // Retrieve content as a multipart HTTP response
            CloudStreamResponse response = client.retrieveContent(pipeHeader, serializedEvent);
            if (response == null) {
                log.error("Response was null from client.retrieveContentMultipart()");
                return;
            }

            // The multipart response consists of a StreamDescriptor and the actual stream Content
            String serializedStreamDesc = response.getStreamDescriptor();
            log.debug("response streamDesc: " + serializedStreamDesc);
            InputStream inStream = response.getStreamContent();
            PipeHeader responsePipeHeader = response.getPipeHeader();
            log.debug("response pipeHeader: " + responsePipeHeader.serialize());

            if (inStream == null) {
                log.error("received null inputStream from pipeclient.retrieveContentMultipart()");
                return;
            }

            // TODO: currently we are setting a fake sender SEP
            /*
			UhuServiceId serviceId = new UhuServiceId(FakeServiceRegistration.generateUniqueServiceID());
			UhuServiceEndPoint senderEndpoint = new UhuServiceEndPoint("CloudEchoService", serviceId);
			responsePipeHeader.setSenderSEP(senderEndpoint);
			*/

            // Use a file to transfer the stream back to the service(s) that requested it
            String outputFileName = UUID.randomUUID().toString();
            WriteJob job = new WriteJob();
            job.setShortFileName(outputFileName);
            job.setInputStream(inStream);
            // TODO: do we need this "retain" flag any more?
            job.setRetainFile(true);
            //job.setRetainFile(getStreamEvent.isUhuRetainFile());
            job.setStreamDescriptor(serializedStreamDesc);
            job.setPipeHeader(responsePipeHeader);

            pipeMonitor.processLocalWrite(job);
        }
        // Send a general Event
        else {
            log.info("PipeClient.sendEvent(): " + cloudPipe);
            // Send the event to the cloudpipe
            CloudResponse response = client.sendEvent(pipeHeader, serializedEvent);
            if (response == null) {
                log.error("An error occurred in sendEvent().  CloudResponse was null");
                return;
            }

			/*
			 *  Check to make sure reply event is usable
			 */

            String serializedReply = response.getSerializedEvent();
            if (serializedReply == null) {
                log.error("Reply was null from client.sendEvent()");
                return;
            }
            Event replyEvent = Event.fromJson(serializedReply, Event.class);
            if (!isReplyValid(replyEvent)) {
                log.error("Reply event not valid. Will not pass to receiver for delivery to service.");
                return;
            }

            // TODO: currently we are setting a fake sender SEP
			/*
			UhuServiceId serviceId = new UhuServiceId(FakeServiceRegistration.generateUniqueServiceID());
			UhuServiceEndPoint senderEndpoint = new UhuServiceEndPoint("CloudEchoService", serviceId);
			response.getPipeHeader().setSenderSEP(senderEndpoint);
			*/

            //we have a valid event, send to listeners for local delivery
            pipeMonitor.processLocalSend(response.getPipeHeader(), serializedReply);
        }
    }

    private boolean isReplyValid(Event reply) {
        if (reply == null) {
            log.error("Event is not valid (it is null): ");
            return false;
        }

        // Make sure the topic is valid
        String replyTopic = reply.topic;
        if (replyTopic == null || replyTopic.trim().equals("")) {
            log.error("Topic not set on reply event");
            return false;
        }
        log.debug("Received response from cloudpipe endpoint on topic: " + replyTopic);

        // Make sure the event does not represent an exception
        // FIXME: How should we specify and/or detect errors from the pipe endpoint?
        if (replyTopic.contains("Exception")) {
            log.error("Received exception from cloudpipe");
            return false;
        }

        return true;
    }

    public void setPipeRegistry(PipeRegistry pipeRegistry) {
        this.pipeRegistry = pipeRegistry;
    }


    public void setSerializedEvent(String serializedEvent) {
        this.serializedEvent = serializedEvent;
    }


    public void setPipeMonitor(PipeManagerImpl pipeMonitor) {
        this.pipeMonitor = pipeMonitor;
    }

    public void setUhuHeader(Header uhuHeader) {
        this.uhuHeader = uhuHeader;
    }

    public String getCertFileName() {
        return certFileName;
    }

    public void setCertFileName(String certFileName) {
        this.certFileName = certFileName;
    }
}
