//FIXME: should we remove the "upa" portion of the package names?
package com.bezirk.pipe.core;

import com.bezirk.control.messages.Header;
import com.bezirk.control.messages.MulticastHeader;
import com.bezirk.control.messages.UnicastHeader;
import com.bezirk.control.messages.pipes.PipeHeader;
import com.bezirk.control.messages.pipes.PipeMulticastHeader;
import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.messages.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to hold data needed to send
 */
public class RemoteSender implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RemoteSender.class);

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
            logger.error(err + "pipeMonitor is null");
            valid = false;
        }
        if (serializedEvent == null) {
            logger.error(err + "serializedEvent is null");
            valid = false;
        }
        if (pipeRegistry == null) {
            logger.error(err + "pipeRegistry is null");
            valid = false;
        }
        if (uhuHeader == null) {
            logger.error(err + "bezirkHeader is null");
            valid = false;
        }
        if (certFileName == null) {
            logger.error(err + "certFileName is null");
            valid = false;
        }

        return valid;
    }

    public void run() {
        if (!validateDataMembers()) {
            logger.error("Cannot execute PipeSender.run() b/c data members are not specified correctly");
            return;
        }

        if (uhuHeader instanceof UnicastHeader) {
            logger.error("Unicast send to a pipe not yet supported. Can't execute RemoteSender.run()");
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
            logger.error("Pipe is not registered: " + pipe);
            return;
        }

        // Pipe is registered, send the event to the pipe
        logger.info("Sending to pipe: " + pipe);
    }

    private boolean isReplyValid(Event reply) {
        if (reply == null) {
            logger.error("Event is not valid (it is null): ");
            return false;
        }

        // Make sure the topic is valid
        String replyTopic = reply.topic;
        if (replyTopic == null || replyTopic.trim().equals("")) {
            logger.error("Topic not set on reply event");
            return false;
        }
        logger.debug("Received response from cloudpipe endpoint on topic: " + replyTopic);

        // Make sure the event does not represent an exception
        // FIXME: How should we specify and/or detect errors from the pipe endpoint?
        if (replyTopic.contains("Exception")) {
            logger.error("Received exception from cloudpipe");
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

    public void setBezirkHeader(Header uhuHeader) {
        this.uhuHeader = uhuHeader;
    }

    public String getCertFileName() {
        return certFileName;
    }

    public void setCertFileName(String certFileName) {
        this.certFileName = certFileName;
    }
}
