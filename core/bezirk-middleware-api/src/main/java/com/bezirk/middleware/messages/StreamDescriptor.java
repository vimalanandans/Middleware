package com.bezirk.middleware.messages;

import java.io.File;

/**
 * <h1 style="color: red">Experimental</h1>
 *
 * Base class for non-trivial Bezirk messages and data transfers. A stream represents a set of data
 * elements such as multiple messages or picture and music data. This class is extended by protocol
 * implementors to define concrete streams and their custom attributes and payloads. To implement
 * a simple, small message, extend the {@link Event} class.
 * <p>
 * At this time, streams may only be unicast.
 * </p>
 *
 * @see Message
 * @see Event
 */
public class StreamDescriptor extends Message {

    private static final long serialVersionUID = -9093454918659207882L;
    /**
     * Subclass sets to <code>true</code> if the payload can be processed incrementally (e.g. a
     * music stream) or <code>false</code> if all data elements must be received before processing
     * can continue (e.g. image file data).
     */
    private final boolean incremental;
    /**
     * Subclass sets to <code>true</code> if Bezirk must encrypt the stream's data before transmitting. If
     * set to <code>false</code>, Bezirk will offer the user the opportunity to encrypt the stream anyway.
     * This option is provided to allow users to make a trade-off between privacy and performance where the
     * protocol designer does not believe the stream will always require confidentiality.
     */
    private final boolean encrypted;

    /*
     * The file whose contents will be sent using the <code>stream</code>
     */
    private final File file;

    private StateListener stateListener = null;

    /**
     * streamActionNAme
     */
    private String streamActionName = null;

    /**
     * The concrete implementation of a <code>StreamDescriptor</code> must specify the stream's topic.
     * Message topics are documented in {@link Message}.
     *
     * @param isIncremental <code>true</code>, the recipient may begin processing the payload as data elements
     *                      are received (e.g. a music stream). Otherwise, all of the data must be received first
     *                      (e.g. a picture stream).
     * @param isEncrypted   <code>true</code> if the contents of the stream must be encrypted
     *                      for transmission.
     */
    public StreamDescriptor(boolean isIncremental, boolean isEncrypted, File file, String streamActionName) {
        this.incremental = isIncremental;
        this.encrypted = isEncrypted;
        this.file = file;
        this.streamActionName = streamActionName;
    }


    /**
     * Returns <code>true</code> if the payload can be processed as data elements arrive.
     *
     * @return <code>true</code> if the payload can be processed as data elements arrive
     */
    public boolean isIncremental() {
        return incremental;
    }

    /**
     * Returns <code>true</code> if the stream's contents must be encrypted.
     *
     * @return <code>true</code> if the stream's contents must be encrypted
     */
    public boolean isEncrypted() {
        return encrypted;
    }

    /**
     * Get the listener observing state changes to this stream.
     *
     * @return the listener observing state changes to this stream, or <code>null</code> if
     * one was not set
     */
    public StateListener getStateListener() {
        return stateListener;
    }

    /**
     * Set the listener observing state changes to this stream.
     *
     * @param stateListener the listener observing state changes to this stream, or <code>null</code>
     *                      to remove an existing listener
     */
    public void setStateListener(StateListener stateListener) {
        this.stateListener = stateListener;
    }

    /**
     * returns the file whose contents will be sent using the <code>stream</code>
     * @return
     */
    public File getFile() {
        return file;
    }

    /**
     *
     * @return
     */
    public String getStreamActionName() {
        return streamActionName;
    }

    /**
     *
     * @param streamActionName
     */
    public void setStreamActionName(String streamActionName) {
        this.streamActionName = streamActionName;
    }

    /**
     * States a stream can be in.
     */
    public enum StreamStates {
        LOST_CONNECTION, END_OF_DATA
    }

    /**
     * Interface implemented by observers of a <code>StreamDescriptor</code> that want to be notified when
     * the stream changes states (e.g. gets to the end of its data, prematurely closes, etc.).
     */
    public interface StateListener {
        void streamNotification(StreamStates state);
    }
}