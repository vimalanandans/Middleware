package com.bezirk.pipe.core;

import com.bezirk.control.messages.Header;
import com.bezirk.control.messages.pipes.PipeHeader;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;

/**
 * <ol>
 * <li>processRemoteSend() - Send to remote pipe endpoint</li>
 * <li>processLocalWrite() - If result is a stream, write stream to local device with this method</li>
 * <li>processLocalSend()  - Send reply (event or stream) to local bezirk services on this device</li>
 * </ol>
 */
public interface PipeManager {

    /**
     * Send event on a pipe, if one is specified in the header.  The PipeManager will
     * check to make sure that the header refers to a known pipe before sending
     *
     * @param bezirkHeader
     * @param serializedEvent
     */
    void processRemoteSend(Header bezirkHeader, String serializedEvent);

    /**
     * If a stream is requested from the remote site, this method is called to write the
     * stream content locally
     *
     * @param writeJob
     */
    void processLocalWrite(WriteJob writeJob);

    /**
     * Called to send a stream to a local zirk
     *
     * @param localSendJob
     */
    void processLocalSend(LocalStreamSendJob localSendJob);

    /**
     * Called to send an event to a local zirk
     *
     * @param pipeHeader
     * @param serializedEvent
     */
    void processLocalSend(PipeHeader pipeHeader, String serializedEvent);

    /**
     * Return true if the referenced pipe is already registered. Return false otherwise
     *
     * @param pipe
     * @return
     */
    boolean isRegistered(Pipe pipe);

    /**
     * Get the pipe record associated with the specified pipe. The PipeRecord
     * includes information on currently allowed PipePolicies
     *
     * @param pipe
     * @return
     */
    PipeRecord getPipeRecord(Pipe pipe);

    /**
     * Called to notify the PipeManager whether the user has granted access to the pipe and
     * which policies are allowed to pass in and out of the pipe.
     *
     * @param granted        True if the pipe was granted
     * @param pipe           The Pipe originally requested by the zirk
     * @param allowedIn      This PipePolicy contains the collection of Protocols allowed into the local sphere
     * @param allowedOut     This PipePolicy contains the collection of Protocols allowed to pass out of the local sphere
     * @param sphereId       The sphere the pipe has been added to
     * @param bezirkListener The bezirk zirk to notify of the status of the pipe request
     */
    void pipeGranted(boolean granted, Pipe pipe, PipePolicy allowedIn, PipePolicy allowedOut,
                     String sphereId, BezirkListener bezirkListener);
}
