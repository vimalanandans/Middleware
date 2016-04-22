/**
 * Copyright (C) 2014 Robert Bosch, LLC. All Rights Reserved.
 * <p>
 * Authors: Joao de Sousa, 2014
 * Mansimar Aneja, 2014
 * Vijet Badigannavar, 2014
 * Samarjit Das, 2014
 * Cory Henson, 2014
 * Sunil Kumar Meena, 2014
 * Adam Wynne, 2014
 * Jan Zibuschka, 2014
 */
package com.bezirk.middleware.messages;

/**
 * Base class for non-trivial Bezirk messages and data transfers. A stream represents a set of data
 * elements such as multiple messages or picture and music data. This class is extended by protocol
 * implementors to define concrete streams and their custom attributes and payloads. To implement
 * a simple, small message, extend the {@link Event} class.
 * <p>
 * Implementers should favor extending {@link MulticastStream} when a concrete stream will have
 * multiple recipients. {@link UnicastStream} should be favored when the stream will have a single
 * and known recipient.
 * </p>
 *
 * @see Message
 * @see Event
 * @see MulticastStream
 * @see UnicastStream
 */
public class Stream extends Message {
    /**
     * Subclass sets to <code>true</code> if the payload can be processed incrementally (e.g. a
     * music stream) or <code>false</code> if all data elements must be received before processing
     * can continue (e.g. image file data).
     */
    private boolean incremental;

    /**
     * Subclass sets to <code>true</code> if data elements may be dropped from the stream without
     * resending to increase performance, or <code>false</code> if the data elements must be
     * reliably transferred.
     */
    private boolean allowDrops;

    /**
     * Subclass sets to <code>true</code> if Bezirk must encrypt the stream's data before transmitting. If
     * set to <code>false</code>, Bezirk will offer the user the opportunity to encrypt the stream anyway.
     * This option is provided to allow users to make a tradeoff between privacy and performance where the
     * protocol designer does not believe the stream will always require confidentiality.
     */
    private boolean encrypted;

    /**
     * The concrete implentation of a <code>Stream</code> must specify the stream's flag
     * and topic. Message flags and topics are documented in {@link Message}.
     *
     * @param flag  flag to mark the intent of this stream
     * @param topic the pub-sub topic for this stream
     */
    public Stream(Flag flag, String topic) {
        this.flag = flag;
        this.topic = topic;
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
     * Sets whether or not the payload can be processed before all data elements are received.
     * If <code>true</code>, the recipient may begin processing the payload as data elements
     * are received (e.g. a music stream). Otherwise, all of the data must be received first
     * (e.g. a picture stream).
     *
     * @param incremental <code>true</code> if the payload can be processed before all data
     *                    elements arrive
     */
    public void setIncremental(boolean incremental) {
        this.incremental = incremental;
    }

    /**
     * Returns <code>true</code> if the stream may be unreliably transmitted. Essentially,
     * <code>true</code> has the same reliability guarantees as UDP, and <code>false</code> the
     * same guarantees as TCP.
     *
     * @return <code>true</code> if the stream may be unreliably transmitted.
     */
    public boolean isAllowDrops() {
        return allowDrops;
    }

    /**
     * Sets whether or not the middleware must reliably transmit this stream to recipients, or
     * if packets may be dropped. It can be useful to allow packets to be dropped in high
     * performance cases where some loss is acceptable (e.g. streaming video).
     *
     * @param allowDrops <code>true</code> if the stream may be unreliably transmitted
     */
    public void setAllowDrops(boolean allowDrops) {
        this.allowDrops = allowDrops;
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
     * Sets whether or not the stream's contents must be encrypted.
     *
     * @param encrypted <code>true</code> if the stream's contents must be encrypted
     */
    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }
}